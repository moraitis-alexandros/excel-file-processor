/**
 * It creates a class with Product attributes. Uses a constructor as
 * well as getters and setters for all attributes
 * Before use adjust the package accordingly if needed
 */
package AutostoreParameters;


public class Product {
    private String id;
    private int refid;
    private double pWeight;
    private double pVolume;
    private double pDemand; // in number of items
    private double pValue;

    private double dimWidth;
    private double dimHeight;
    private double dimLength;

    //New variable
    private String pDescription;


//    public Product(int iRef, String vID) {
public Product(String vID) {
//        this.refid=iRef;
        this.id=vID;
    }//constructor

    public String getProductID() {
        return id;
    }

    public double getDemand() {
        return pDemand;
    }
    public double getValue() {
        return pValue;
    }

    public double getDimLength() {
        return dimLength;
    }
    public double getDimWidth() {
        return dimWidth;
    }
    public double getDimHeight() {
        return dimHeight;
    }

    //ADDED NEW CODE (Getter/Setters for use)
    /**
     * This is a second constructor used for integration with  FileProcessor
     */
    public Product() {

    }

    public String getpDescription() {return pDescription;}

    public void setpDescription(String pDescription) {this.pDescription = pDescription;}

    public void setpValue(double pValue) {this.pValue = pValue;}

    public void setpDemand(double pDemand) {this.pDemand = pDemand;}

    public double getpVolume() {return pVolume;}

    public void setpVolume(double pVolume) {this.pVolume = pVolume;}

    public double getpWeight() {return pWeight;}

    public void setpWeight(double pWeight) {this.pWeight = pWeight;}

    public void setId(String id) {this.id = id;}

    public void setDimHeight(double dimHeight) {this.dimHeight = dimHeight;}

    public void setDimLength(double dimLength) {this.dimLength = dimLength;}

    public void setDimWidth(double dimWidth) {this.dimWidth = dimWidth;}

}//class
