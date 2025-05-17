package DBUtil;

import Model.Order;
import Model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDB {
    private static ProductDB instance;
    final private List<Product> productList = loadProducts();

    private ProductDB() {
    }

    public static ProductDB getInstance() {
        if (instance == null) {
            instance = new ProductDB();
        }
        return instance;
    }

    public List<String> getOrderId() {
        List<String> ids = new ArrayList<>();
        for (Product product : productList) {
            ids.add(product.getProId());
        }
        return ids;
    }

    public List<Product> getProducts() {
        return productList;
    }

    private static List<Product> loadProducts() {
        List<Product> orders = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("src/data/products.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

            JSONObject json = new JSONObject(line);

            String pro_id = json.optString("pro_id", null);
            String pro_model = json.optString("pro_model", null);
            String pro_category = json.optString("pro_category", null);
            String pro_name = json.optString("pro_name", null);
            double pro_current_price = json.optDouble("pro_current_price", 0);
            double pro_raw_price = json.optDouble("pro_raw_price", 0);
            double pro_discount = json.optDouble("pro_discount", 0);
            int pro_likes_count = json.optInt("pro_likes_count", 0);

            if (pro_id != null && pro_model != null && pro_category != null && pro_name != null) {
                orders.add(new Product(pro_id, pro_name, pro_model, pro_category, pro_current_price,
                        pro_raw_price, pro_discount, pro_likes_count));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    public void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/data/products.json"))) {
            for (Product product : products) {
                String jsonString = product.toString();
                bw.write(jsonString);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
