import AutostoreParameters.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

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
    int colLastActive;
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
    }//fileExtractor constructor


    /**
     * It executes the extraction.
     */
    public void executeExtraction(){
        try (OutputStream fileOut = new FileOutputStream(outPath)) {
            addHeadingsIntoSortedSheet();
            populateSpreadsheet();
            addProductStockQuantityColumn(sheet1);
            autoSizeSheetColumns();
            wb.write(fileOut);
            System.out.println("The extraction completed successfully");
        } catch (IOException e) {
            System.out.println("There was an error to the extraction! Not correct path provided");
            throw new RuntimeException(e);
        }
    }//executeExtraction

    /**
     * It populates the spreadsheet based on headings. It iterates on each product corresponding hashmap.
     * If it belongs in stockUnitsPerBinType array (i.e need "Q_Type) then it assigns value to the cell that its heading substring
     * is equal with the hash value key. Otherwise if it belongs to nBinsPerBinType array (i.e need "N_Type) it makes
     * a similar assignement
     */
    public void populateSpreadsheet() {
        //Populate spreadsheet based on values
        int rowIterator = 1;
        for (int i =0; i < pProductList.size(); i++) {
            //Make the first column with the product id
            row = sheet1.createRow(rowIterator);
            cell = row.createCell(0);
            String productId = pProductList.get(i).getProductID();;
            cell.setCellValue(productId);


            HashMap<String, Integer> q = stockUnitsPerBinType.get(i);
            Set<String> qKeys = q.keySet();

            for (String key : qKeys) {
                int index = 0;
                for (int j = 1; j <= colLastActive; j++) {
                    row = sheet1.getRow(0);
                    if ((Integer.parseInt(key.substring(7)) == Integer.parseInt(row.getCell(j).getStringCellValue().substring(9)))
                            && (row.getCell(j).getStringCellValue().charAt(0) == 'Q'))
                    {
                        index = j;
                        break;
                    }

                }
                row = sheet1.getRow(rowIterator);
                row.createCell(index).setCellValue(q.get(key));
            }

            HashMap<String, Integer> s = nBinsPerBinType.get(i);
            Set<String> sKeys = s.keySet();

            for (String key : sKeys) {
                int index = 0;
                for (int j = 1; j <= colLastActive; j++) {
                    row = sheet1.getRow(0);
                    if ((Integer.parseInt(key.substring(7)) == Integer.parseInt(row.getCell(j).getStringCellValue().substring(9)))
                            && (row.getCell(j).getStringCellValue().charAt(0) == 'N'))
                    {
                        index = j;
                        break;
                    }

                }
                row = sheet1.getRow(rowIterator);
                row.createCell(index).setCellValue(s.get(key));
            }

            rowIterator++;
        }
    }//populateSpreadsheet

    /**
     * It creates the headings based on bin Types. At first all products and their corresponding
     * hashmaps are seached for unique values. The unique values BinType{x} are added to binTypes array
     * After the array is sorted based on a comparator. Then the headings are populated from column 1 till end
     * in a way that even columns correspond to Q_Type{x} and odd columns correspond to N_Type{x}.
     */
    public void addHeadingsIntoSortedSheet() {
        //Create Headings
        Row row = sheet1.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Product ID");



        //iterate for finding the unique keys
        for (int i = 1; i < pProductList.size(); i++) {
            HashMap<String,Integer> j = stockUnitsPerBinType.get(i);
            Set<String> jKeys = j.keySet();
            for (String key : jKeys) {
                if (!binExists(key)) {
                    binTypes.add(key);
                }
            }
        }


        //order binType
        Collections.sort(binTypes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int num1 = Integer.parseInt(o1.substring(7));
                int num2 = Integer.parseInt(o2.substring(7));
                return Integer.compare(num1, num2);
            }
        });

        System.out.println("Found all bin types and sorted them sucessfully");
        //Create headings based on the binTypes sorted list
        row = sheet1.getRow(0);
        //Populate Q & N
        int binIndexQ = 1;
        int binIndexN = 2;
        for (int i = 0; i < binTypes.size(); i++) {
            row.createCell(binIndexQ).setCellValue("Q_"+binTypes.get(i));
            row.createCell(binIndexN).setCellValue("N_"+binTypes.get(i));
            binIndexQ = binIndexQ + 2;
            binIndexN = binIndexN + 2;
        }

        //find the last active col in sheet
        colLastActive = binIndexN - 2;
    }//addHeadingsIntoSortedSheet

    /**
     * Applies the product stock quantity column to the specified sheet
     */
    public void addProductStockQuantityColumn(Sheet sheet) {
            row = sheet.getRow(0);
            row.createCell(colLastActive + 1).setCellValue("Total Quantity");
        for (int i = 1; i <= pProductList.size(); i++) {
            row = sheet.getRow(i);
            String id = row.getCell(0).getStringCellValue();
            cell = row.createCell(colLastActive + 1);
            int stockUnits = stockUnitsPerProduct.get(id);
            cell.setCellValue(stockUnits);
        }
    }//addProductStockQuantityColumn

    /**
     * Resizes the width of columns of the created sheets to fit into contents width
     */
    public void autoSizeSheetColumns() {
        for (int i= 0; i <= colLastActive + 1; i++) {
            sheet1.autoSizeColumn(i); //adjust width of the i column
        }
    }//autoSizeSheetColumns

    /**
     * Check if the bin exists in binTypesArray
     */
    public boolean binExists(String bin) {
        return binTypes.contains(bin);
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

}//class
