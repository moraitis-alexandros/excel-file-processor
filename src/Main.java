import java.io.FileNotFoundException;

/**
 * Main class for testing
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        //Test File Processor
        //        //Initializing File Processor
//        FileProcessor fp = new FileProcessor(
//           "/home/alex/repos/libs/AS.xlsx","Sheet1",false);
//
//        ArrayList<Product> productArrayList;
//        productArrayList = fp.getProductList();

        //Test FileExtractor
        //Creates dummy data and uses its pProductList, stockUnitsPerProduct, stockUnitsPerBinType, nBinsPerBinType in FileExtractor Initialization
        DummyData dummyData = new DummyData();
        //Initializing FileExtractor
        FileExtractor fileExtractor = new FileExtractor(dummyData.pProductList, dummyData.stockUnitsPerProduct, dummyData.stockUnitsPerBinType,
                dummyData.nBinsPerBinType, "/home/alex/repos/extractedDataDemo.xlsx",false);
//        fileExtractor.printExtractedData(); //Used for testing purposes
        fileExtractor.executeExtraction();

    }//main

}//class
