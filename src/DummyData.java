import AutostoreParameters.Product;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Applies synthetic data (8 products and 7 bins) generated with ChatGPT
 * For testing purposes
 */
public class DummyData {
    ArrayList<Product> pProductList;

    public HashMap<String, Integer> getStockUnitsPerProduct() {
        return stockUnitsPerProduct;
    }

    public ArrayList<HashMap<String, Integer>> getnBinsPerBinType() {
        return nBinsPerBinType;
    }

    public ArrayList<HashMap<String, Integer>> getStockUnitsPerBinType() {
        return stockUnitsPerBinType;
    }

    public ArrayList<Product> getpProductList() {
        return pProductList;
    }

    HashMap<String, Integer> stockUnitsPerProduct;
    ArrayList<HashMap<String, Integer>> stockUnitsPerBinType;
    ArrayList<HashMap<String, Integer>> nBinsPerBinType;


    public DummyData() {
        this.pProductList = new ArrayList<>();
        pProductList.add(new Product("Product1"));
        pProductList.add(new Product("Product2"));
        pProductList.add(new Product("Product3"));
        pProductList.add(new Product("Product4"));
        pProductList.add(new Product("Product5"));
        pProductList.add(new Product("Product6"));
        pProductList.add(new Product("Product7"));
        pProductList.add(new Product("Product8"));

// Stock units per product
        this.stockUnitsPerProduct = new HashMap<>();
        stockUnitsPerProduct.put("Product1", 75);
        stockUnitsPerProduct.put("Product2", 120);
        stockUnitsPerProduct.put("Product3", 50);
        stockUnitsPerProduct.put("Product4", 90);
        stockUnitsPerProduct.put("Product5", 100);
        stockUnitsPerProduct.put("Product6", 80);
        stockUnitsPerProduct.put("Product7", 110);
        stockUnitsPerProduct.put("Product8", 60);

// Stock units per bin type
        this.stockUnitsPerBinType = new ArrayList<>();

        HashMap<String, Integer> product1BinStock = new HashMap<>();
        product1BinStock.put("BinType1977", 40);
        product1BinStock.put("BinType2", 35);
        product1BinStock.put("BinType3", 20);
        stockUnitsPerBinType.add(product1BinStock);

        HashMap<String, Integer> product2BinStock = new HashMap<>();
        product2BinStock.put("BinType1977", 60);
        product2BinStock.put("BinType2", 60);
        product2BinStock.put("BinType4", 40);
        stockUnitsPerBinType.add(product2BinStock);

        HashMap<String, Integer> product3BinStock = new HashMap<>();
        product3BinStock.put("BinType33", 50);
        product3BinStock.put("BinType5", 30);
        stockUnitsPerBinType.add(product3BinStock);

        HashMap<String, Integer> product4BinStock = new HashMap<>();
        product4BinStock.put("BinType2", 30);
        product4BinStock.put("BinType33", 60);
        product4BinStock.put("BinType6", 25);
        stockUnitsPerBinType.add(product4BinStock);

        HashMap<String, Integer> product5BinStock = new HashMap<>();
        product5BinStock.put("BinType1977", 50);
        product5BinStock.put("BinType3", 25);
        product5BinStock.put("BinType5", 25);
        stockUnitsPerBinType.add(product5BinStock);

        HashMap<String, Integer> product6BinStock = new HashMap<>();
        product6BinStock.put("BinType2", 45);
        product6BinStock.put("BinType4", 35);
        product6BinStock.put("BinType6", 25);
        stockUnitsPerBinType.add(product6BinStock);

        HashMap<String, Integer> product7BinStock = new HashMap<>();
        product7BinStock.put("BinType33", 50);
        product7BinStock.put("BinType5", 60);
        stockUnitsPerBinType.add(product7BinStock);

        HashMap<String, Integer> product8BinStock = new HashMap<>();
        product8BinStock.put("BinType2", 20);
        product8BinStock.put("BinType6", 40);
        stockUnitsPerBinType.add(product8BinStock);

// Number of bins per bin type
        this.nBinsPerBinType = new ArrayList<>();

        HashMap<String, Integer> product1BinCount = new HashMap<>();
        product1BinCount.put("BinType1977", 1);
        product1BinCount.put("BinType2", 1);
        product1BinCount.put("BinType3", 1);
        nBinsPerBinType.add(product1BinCount);

        HashMap<String, Integer> product2BinCount = new HashMap<>();
        product2BinCount.put("BinType1977", 1);
        product2BinCount.put("BinType2", 2);
        product2BinCount.put("BinType4", 1);
        nBinsPerBinType.add(product2BinCount);

        HashMap<String, Integer> product3BinCount = new HashMap<>();
        product3BinCount.put("BinType33", 1);
        product3BinCount.put("BinType5", 1);
        nBinsPerBinType.add(product3BinCount);

        HashMap<String, Integer> product4BinCount = new HashMap<>();
        product4BinCount.put("BinType2", 1);
        product4BinCount.put("BinType33", 1);
        product4BinCount.put("BinType6", 1);
        nBinsPerBinType.add(product4BinCount);

        HashMap<String, Integer> product5BinCount = new HashMap<>();
        product5BinCount.put("BinType1977", 1);
        product5BinCount.put("BinType3", 1);
        product5BinCount.put("BinType5", 1);
        nBinsPerBinType.add(product5BinCount);

        HashMap<String, Integer> product6BinCount = new HashMap<>();
        product6BinCount.put("BinType2", 1);
        product6BinCount.put("BinType4", 1);
        product6BinCount.put("BinType6", 1);
        nBinsPerBinType.add(product6BinCount);

        HashMap<String, Integer> product7BinCount = new HashMap<>();
        product7BinCount.put("BinType33", 1);
        product7BinCount.put("BinType5", 1);
        nBinsPerBinType.add(product7BinCount);

        HashMap<String, Integer> product8BinCount = new HashMap<>();
        product8BinCount.put("BinType2", 1);
        product8BinCount.put("BinType6", 1);
        nBinsPerBinType.add(product8BinCount);
    }//constructor


}//Dummy data
