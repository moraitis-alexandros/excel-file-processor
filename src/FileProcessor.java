import AutostoreParameters.Product;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * It extracts from an excel file the products attributes line by line. It creates for
 * each, an instance of Product. Then it adds all of them in a productList.
 * It uses the org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor.
 * IMPORTANT! It works only with files with extension .xlsx
 * It works only with Strings and Numeric Data. i.e no formulas and dates
 * In initialization we should provide complete path of the excel file as well as Sheet name and
 * a boolean indicating if we want to print the results (true/false)
 * i.e FileProcessor("C:\\Users\\mypc\\Desktop\\FileProcessor\\AS.xlsx", "Sheet1", true)
 */

//TO DO
//Provide a column for Value & Weight
//Clarify what Volume means - we assumed that volume is column E (Minimum Replenishment Quantity)
//Create an additional class for performing more checks on cells. Numerics, Strings checks
//min max values...


public class FileProcessor {

    ArrayList<Product> productList;
    FileInputStream fis;
    XSSFWorkbook wb;
    XSSFSheet sheet;
    DataFormatter formatter;
//    Boolean printResultsInTerminal = true; //Prints results in terminal
    HashMap<String,String> productNoImport;

    //Creating getters and setters for productList
    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public FileProcessor(String inPath, String sheetName, Boolean printResultsInTerminal) {
        //Define the Path
//        inPath = "C:\\Users\\mypc\\Desktop\\FileProcessor\\AS.xlsx";
//        String sheetName = "Sheet1";

        try {
            fis = new FileInputStream(inPath);
            wb = new XSSFWorkbook(fis);
            sheet = wb.getSheet(sheetName); //Assume sheet
            formatter = new DataFormatter();
            productList = new ArrayList<>();
            productNoImport = new HashMap<>();
            formatter = new DataFormatter();

            importProducts();
            printResults(printResultsInTerminal); //if false
        } catch (
                IOException ex) {
            System.out.println(ex.getStackTrace());
        }

    }//FileProcessor Constructor

    public void importProducts() {
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; //Skips headings
            Product product = new Product();

            //We assume that there is no product with null id
            String productId = formatter.formatCellValue(row.getCell(0));
            product.setId(productId);

            try {
                //Import Product Description
                product.setpDescription(row.getCell(1).getStringCellValue());
                product.setpDemand((int)row.getCell(10).getNumericCellValue()); //It assumes
                //that the demand corresponds to column K of spreadsheet (PIECES / DAY)
                product.setpValue((int)0); //No column in spreadsheet that corresponds to value
                product.setpVolume((int)row.getCell(4).getNumericCellValue()); //No
                //column fo volume adjust accordingly
                product.setDimLength((int)row.getCell(11).getNumericCellValue());
                product.setDimWidth((int)row.getCell(12).getNumericCellValue());
                product.setDimHeight((int)row.getCell(13).getNumericCellValue());

                //Add product to productList
                productList.add(product);
            } catch (Exception e) {
                productNoImport.put(productId, "Error processing row: " + e.getMessage());
            }
        }//row iterator
        sumReport();
    }//initialize FileProcessor

    /**
     * It prints the Products and their attributes. Also it prints a hashmap with the
     * products that were not imported due to error in values as well as the cause of error
     * @param printIsTrue
     */
    public void printResults(Boolean printIsTrue) {
        if (printIsTrue) {
            int counter = 1;
            for (Product pd : productList) {
                System.out.printf("No %s | Id: %s | Demand: %.0f | " +
                                "Value: %.0f | Volume: %.0f, | Length: %.0f | Width: %.0f | Height: %.0f \n",
                        counter, pd.getProductID(), pd.getDemand(), pd.getValue(),
                        pd.getpVolume(), pd.getDimLength(), pd.getDimWidth(), pd.getDimHeight());
                counter++;
            }//for
        }
    }//printResults

    /**
     * It gives a quick report of the total products that were imported successfully
     * as well as possible errors
     */
    public void sumReport() {
        System.out.println("Products that imported successfully: "+productList.size());
        System.out.println("Products with Errors: "+productNoImport.size());
        for (String s : productNoImport.keySet()) {
            System.out.printf("Product with id: %s has error: %s",s,productNoImport.get(s));
        }
    }//sumReport

}//class
