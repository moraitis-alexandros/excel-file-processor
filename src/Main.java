import AutostoreParameters.Product;

import java.util.ArrayList;

/**
 * Main class for testing
 */
public class Main {
    public static void main(String[] args) {
        //Initializing File Processor
        FileProcessor fp = new FileProcessor(
           "C:\\Users\\mypc\\Desktop\\FileProcessor\\AS.xlsx","Sheet1",false);

        ArrayList<Product> productArrayList;
        productArrayList = fp.getProductList();

    }//main
}//class
