import AutostoreParameters.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * It extracts data to an excel workbook. The workbook may containe 2 sheets (Data Sheet & Sorted Data Sheet).
 * The data display is row per product id.
 * The headings are Product ID, Q_BinTypeX, N_BinTypeX, Total_Quantity
 * It uses the Apache POI XSSF library
 * IMPORTANT! It works only with files with extension .xlsx
 * It works only with Strings and Numeric Data. i.e no formulas and dates
 * In initialization we should provide:
 * an ArrayList<Product> pProductList,
 * a HashMap<String, Integer> stockUnitsPerProduct,
 * an ArrayList<HashMap<String, Integer>> stockUnitsPerBinType,
 * an ArrayList<HashMap<String, Integer>> nBinsPerBinType
 * an absolute path for extracting the files i.e "/home/alex/repos/extractedDataDemo.xlsx"
 * IMPORTANT! the path provided above is in linux format. If you use windows you
 * must provide something like "C:\\Users\\<YourUsername>\\Desktop\\myDataFile.xlsx"
 * a boolean indicating if we want to order the results (true/false) i.e if 
 * col Q_type1 will be next to col N_type1 and so on.
 * The class can work with numerous products and bins. However, to use the ordering function
 * the bins must have EXACTLY the name format that was provided in the description document:
 * Q_BinType{x}, N_BinType{x} (i.e. Q_BinType1, N_BinType1) where x is an integer 
 */

public class FileExtractor {
    ArrayList<Product> pProductList;
    XSSFWorkbook wb;
    HashMap<String, Integer> stockUnitsPerProduct;
    ArrayList<HashMap<String, Integer>> stockUnitsPerBinType;
    ArrayList<HashMap<String, Integer>> nBinsPerBinType;
    ArrayList<String> binTypes;
    private final String outPath;
    CreationHelper creationHelper ;
    Sheet sheet1;
    Sheet sheet2;
    int colLastActive;
    Boolean removeUnsortedSheet;
    Row rowSorted;
    Row row;
    Cell cell;

    public FileExtractor(ArrayList<Product> pProductList,
                         HashMap<String, Integer> stockUnitsPerProduct,
                         ArrayList<HashMap<String, Integer>> stockUnitsPerBinType,
                         ArrayList<HashMap<String, Integer>> nBinsPerBinType, String outPath, Boolean removeUnsortedSheet
                         ) {

        this.pProductList = pProductList;
        this.stockUnitsPerProduct = stockUnitsPerProduct;
        this.stockUnitsPerBinType = stockUnitsPerBinType;
        this.nBinsPerBinType = nBinsPerBinType;
        this.outPath = outPath;
        this.binTypes = new ArrayList<>();
        this.wb = new XSSFWorkbook();
        creationHelper = wb.getCreationHelper();
        this.sheet1 = wb.createSheet("Data Sheet");
        //create a sheet2 for sorted data
        this.sheet2= wb.createSheet("Sorted Data Sheet");
        this.removeUnsortedSheet = removeUnsortedSheet;
    }//fileExtractor constructor


    /**
     * It executes the extraction. Check if bin types already exist in binTypes ArrayList.
     * If it find that something does not exists it adds it.
     * Then it makes another pass to add the data to the corresponding bins
     */
    public void executeExtraction(){
        try (OutputStream fileOut = new FileOutputStream(outPath)) {
            //Create Headings
            Row row = sheet1.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Product ID");

            //Create Heading for sorted data sheet
            Row rowSorted = sheet2.createRow(0);
            Cell cellSorted = rowSorted.createCell(0);
            cellSorted.setCellValue("Product ID");


             int rowIterator = 1;
             colLastActive = 1;
            for (Product product : pProductList) {
                row = sheet1.createRow(rowIterator);
                cell = row.createCell(0);
                String productId = product.getProductID();;
                cell.setCellValue(productId);
                //For sorted data sheet
                rowSorted = sheet2.createRow(rowIterator);
                cellSorted = rowSorted.createCell(0);
                cellSorted.setCellValue(productId);
                HashMap<String, Integer> q = stockUnitsPerBinType.get(rowIterator-1);
                Set<String> qKeys = q.keySet();
                //Check if bin types already exist in binTypes ArrayList.
                //If it find that something does not exists it adds it.
                //Then it makes another pass to add the data to the corresponding bins
                for (String key : qKeys) {
                    if (!qBinExists(key)) {
                        binTypes.add("Q_"+key);
                        row = sheet1.getRow(0);
                        cell = row.createCell(colLastActive);
                        cell.setCellValue("Q_"+key);
                        row = sheet1.getRow(rowIterator);
                        cell = row.createCell(colLastActive);
                        cell.setCellValue(q.get(key));
                        colLastActive++;
                    }
                }
                for (String key : qKeys) {
                    if (qBinExists(key)) {
                        int index = binTypes.indexOf("Q_"+key) + 1;
                        row = sheet1.getRow(rowIterator);
                        cell = row.createCell(index);
                        cell.setCellValue(q.get(key));
                    }
                }
//                Create Bins per Bin Type Columns
                HashMap<String, Integer> n = nBinsPerBinType.get(rowIterator-1);
                Set<String> nKeys = n.keySet();
                for (String key : nKeys) {
                    if (!nBinExists(key)) {
                        binTypes.add("N_"+key);
                        row = sheet1.getRow(0);
                        cell = row.createCell(colLastActive);
                        cell.setCellValue("N_"+key);
                        row = sheet1.getRow(rowIterator);
                        cell = row.createCell(colLastActive);
                        cell.setCellValue(n.get(key));
                        colLastActive++;
                    }
                }
                for (String key : nKeys) {
                    if (nBinExists(key)) {
                        int index = binTypes.indexOf("N_"+key) + 1;
                        row = sheet1.getRow(rowIterator);
                        cell = row.createCell(index);
                        cell.setCellValue(n.get(key));
                    }
                }
                rowIterator++;
            }

            sortData();
            replaceZerosWithNulls(sheet2);
            //Applies product total quantity to the sheets
            addProductStockQuantityColumn(sheet1);
            addProductStockQuantityColumn(sheet2);
            autoSizeSheetColumns();


            //removes the first sheet with unsorted data.
            if (removeUnsortedSheet) {
                wb.removeSheetAt(0);
            }
            wb.write(fileOut);
            System.out.println("The extraction completed successfully");
        } catch (IOException e) {
            System.out.println("There was an error to the extraction! Not correct path provided");
            throw new RuntimeException(e);
        }
    }//executeExtraction


    /**
     * Sorting into a separate sheet (Sorted Data Sheet) the columns of the original data sheet (Data Sheet)
     * i.e. col Q_type1 will be next to col N_type1 and so on.
     * IMPORTANT the name of the bins must have EXACTLY the name format that was provided in the description document:
     * Q_BinType{x}, N_BinType{x} (i.e. Q_BinType1, N_BinType1) where x is an integer otherwise sortData function
     * will lead to an exception
     * The function firstly sorts the Qbins  and secondly the Nbins in ascending order using a selection sort
     * algorithm based on the substring x, of BinType{x}
     * After sorting it starts populating Sorted Data Sheet by placing Qbins in even columns and Nbins in odd columns
     * accordingly.
     */
    public void sortData() {
        int sortingLastCol = colLastActive - 1;
        for (int i = 1; i <= sortingLastCol - 1; i++ ) {

            row = sheet1.getRow(0);
            Cell cellA =row.getCell(i);
            for (int j = i + 1; j <= sortingLastCol; j++) {
//                System.out.println("col "+j);
                row = sheet1.getRow(0);
                Cell cellB = row.getCell(j);
                //Sort QBins
                if (cellA.getStringCellValue().charAt(0) == 'Q' && cellB.getStringCellValue().charAt(0) == 'Q') {
                    transpose(cellA, cellB, i, j);
                }
            }//inner loop
        }//outer loop



        //Sort NBins
        sortingLastCol = colLastActive - 1;
        for (int i = 1; i <= sortingLastCol - 1; i++ ) {
            row = sheet1.getRow(0);
            Cell cellA =row.getCell(i);
//            System.out.println(cellA.getStringCellValue());
            for (int j = i + 1; j <= sortingLastCol; j++) {
                row = sheet1.getRow(0);
                Cell cellB = row.getCell(j);
                if (cellA.getStringCellValue().charAt(0) == 'N' && cellB.getStringCellValue().charAt(0) == 'N') {
                    transpose(cellA, cellB, i, j);
                }
            }//inner loop
        }//outer loop


        //Start checking sheet1
        int sheet2IndexQ = 1;
        int sheet2IndexN = 2;
        sortingLastCol = colLastActive - 1;
        for (int i = 1; i <= sortingLastCol; i++ ) {
            row = sheet1.getRow(0);

            //Starts placing Qbins in even columns
            Cell cellA = row.getCell(i);
            if (cellA.getStringCellValue().charAt(0) == 'Q') {
                //start populating col
                rowSorted = sheet2.getRow(0);
                rowSorted.createCell(sheet2IndexQ).setCellValue(cellA.getStringCellValue());

                for  (int p = 1; p <= pProductList.size(); p++) {
                    rowSorted = sheet2.getRow(p);
                    cell = sheet1.getRow(p).getCell(i);
                    if (cell != null) {
                        rowSorted.createCell(sheet2IndexQ).setCellValue(cell.getNumericCellValue());
                    }
                }
                sheet2IndexQ = sheet2IndexQ+2;
            }//if

            //Starts placing Nbins in odd columns
            if (cellA.getStringCellValue().charAt(0) == 'N') {
                //start populating col
                rowSorted = sheet2.getRow(0);
                rowSorted.createCell(sheet2IndexN).setCellValue(cellA.getStringCellValue());

                for  (int p = 1; p <= pProductList.size(); p++) {
                    rowSorted = sheet2.getRow(p);
                    cell = sheet1.getRow(p).getCell(i);
                    if (cell != null) {
                        rowSorted.createCell(sheet2IndexN).setCellValue(cell.getNumericCellValue());
                    }
                }
                sheet2IndexN = sheet2IndexN+2;
            }//if

        }

    }//sortData

    /**
     * Applies the product stock quantity column to the specified sheet
     */
    public void addProductStockQuantityColumn(Sheet sheet) {
            row = sheet.getRow(0);
            row.createCell(colLastActive).setCellValue("Total Quantity");
        for (int i = 1; i <= pProductList.size(); i++) {
            row = sheet.getRow(i);
            String id = row.getCell(0).getStringCellValue();
            cell = row.createCell(colLastActive);
            int stockUnits = stockUnitsPerProduct.get(id);
            cell.setCellValue(stockUnits);
        }
    }//addProductStockQuantityColumn

    /**
     * Resizes the width of columns of the created sheets to fit into contents width
     */
    public void autoSizeSheetColumns() {
        for (int i= 0; i <= colLastActive; i++) {
            sheet1.autoSizeColumn(i); //adjust width of the i column
            sheet2.autoSizeColumn(i);
        }
    }//autoSizeSheetColumns

    /**
     * It replaces the zero values of the sheet in null, for better readability
     */
    public void replaceZerosWithNulls(Sheet sheet) {
        for (Row row1 : sheet) {
            for (Cell cell1 : row1) {
                if (cell1 != null) {
                    // Check if the cell is numeric
                    if (cell1.getCellType() == CellType.NUMERIC) {
                        if (cell1.getNumericCellValue() == 0) {
                            cell1.setCellValue(""); // Replace numeric 0 with an empty string
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if the Qbin exists in BinTypesArray
     */
    public boolean qBinExists(String bin) {
        return binTypes.contains("Q_"+bin);
    }

    /**
     * Check if the Nbin exists in BinTypesArray
     */
    public boolean nBinExists(String bin) {
        return binTypes.contains("N_"+bin);
    }

    /**
     * Print the extracted data from HashMaps and Arraylist. Recommended only for debugging
     */
    public void printExtractedData()  {
        System.out.println("Products and their Stock Units:");
        for (Product product : pProductList) {
            System.out.println(product.getProductID() + " -> " + stockUnitsPerProduct.get(product.getProductID()));
        }

        System.out.println("\nStock Units per Bin Type:");
        for (int i = 0; i < pProductList.size(); i++) {
            Product product = pProductList.get(i);
            HashMap<String, Integer> binStock = stockUnitsPerBinType.get(i);
            System.out.println(product.getProductID() + ":");
            for (String binType : binStock.keySet()) {
                System.out.println("  " + binType + " -> " + binStock.get(binType));
            }
        }

        System.out.println("\nNumber of Bins per Bin Type:");
        for (int i = 0; i < pProductList.size(); i++) {
            Product product = pProductList.get(i);
            HashMap<String, Integer> binCount = nBinsPerBinType.get(i);
            System.out.println(product.getProductID() + ":");
            for (String binType : binCount.keySet()) {
                System.out.println("  " + binType + " -> " + binCount.get(binType));
            }
        }
    }//printExtractedData

    /**
     * It makes transpose between cells for entire columns
     */
    public void transpose(Cell cellA, Cell cellB, int i, int j) {
        int cellNumberA = Integer.parseInt(cellA.getStringCellValue().substring(9));
        int cellNumberB = Integer.parseInt(cellB.getStringCellValue().substring(9));

        if (cellNumberA > cellNumberB) {
//                        System.out.println("Transposing N"+cellNumberA+" with "+cellNumberB);
            String temp = cellA.getStringCellValue();
            cellA.setCellValue(cellB.getStringCellValue());
            cellB.setCellValue(temp);

            //Also change the columns
            for (int p = 1; p <= pProductList.size(); p++) {
                row = sheet1.getRow(p);

                // Ensure cells exist before accessing their values
                Cell cellI = row.getCell(i);
                Cell cellJ = row.getCell(j);

                if (cellI == null && cellJ != null) {
                    // Create cellI and copy value from cellJ
                    cellI = row.createCell(i);
                    cellI.setCellValue(cellJ.getNumericCellValue());
                    cellJ.setCellValue(0); // Clear numeric value in cellJ
                } else if (cellJ == null && cellI != null) {
                    // Create cellJ and copy value from cellI
                    cellJ = row.createCell(j);
                    cellJ.setCellValue(cellI.getNumericCellValue());
                    cellI.setCellValue(0); // Clear numeric value in cellI
                } else if (cellI != null && cellJ != null) {
                    // Swap values between cellI and cellJ
                    double temp2 = cellI.getNumericCellValue();
                    cellI.setCellValue(cellJ.getNumericCellValue());
                    cellJ.setCellValue(temp2);
                }
                // If both cells are null, nothing to do for this row
            }

        }//if
    }

}//class
