package Model;

public class Product {
    private String proId;
    private String proName;
    private String proModel;
    private String proCategory;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    public Product() {
    }

    public Product(String proId, String proName, String proModel, String proCategory, double proCurrentPrice, double proRawPrice, double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proName = proName;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    //Methods

    @Override
    public String toString() {
        return "Product{" +
                "pro_id:\""+ proId + '\"' +
                ",pro_model:\"" + proModel + '\"' +
                ",pro_category:\"" + proCategory + '\"' +
                ",pro_name:\"" + proName +
                ",pro_current_price:\"" + proCurrentPrice +
                ",pro_raw_price:\"" + proRawPrice +
                ",pro_discount:\"" + proDiscount +
                ",pro_likes_count:\"" + proLikesCount +
                "\"}";
    }


    // Getters and Setters
    public String getProId() {
        return proId;
    }
    public void setProId(String proId) {
        this.proId = proId;
    }
    public String getProName() {
        return proName;
    }
    public void setProName(String proName) {
        this.proName = proName;
    }
    public String getProModel() {
        return proModel;
    }
    public void setProModel(String proModel) {
        this.proModel = proModel;
    }
    public String getProCategory() {
        return proCategory;
    }
    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }
    public double getProCurrentPrice() {
        return proCurrentPrice;
    }
    public void setProCurrentPrice(double proCurrentPrice) {
        this.proCurrentPrice = proCurrentPrice;
    }
    public double getProRawPrice() {
        return proRawPrice;
    }
    public void setProRawPrice(double proRawPrice) {
        this.proRawPrice = proRawPrice;
    }
    public double getProDiscount() {
        return proDiscount;
    }
    public void setProDiscount(double proDiscount) {
        this.proDiscount = proDiscount;
    }
    public int getProLikesCount() {
        return proLikesCount;
    }
    public void setProLikesCount(int proLikesCount) {
        this.proLikesCount = proLikesCount;
    }

}
