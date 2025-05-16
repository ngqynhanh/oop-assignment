package DBUtil;

import Model.Order;
import Model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDB {
    private static ProductDB instance;
    final private List<Product> productList;

    private ProductDB() {
        productList = Implementation.loadFromFile("products.json", Product.class);
    }

    public static ProductDB getInstance() {
        if (instance == null) {
            instance = new ProductDB();
        }
        return instance;
    }

    public List<String> getOrderId() {
        List<String> ids = new ArrayList<>();
        for (Product order : productList) {
            ids.add(order.getProId());
        }
        return ids;
    }

    public List<Product> getProducts() {
        return productList;
    }
}
