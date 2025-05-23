package DBUtil;

import Model.Order;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class OrderDB {
    private static OrderDB instance;
    private static final String FILE_PATH = "src/data/orders.json";
    final private List<Order> orderList = loadOrders(FILE_PATH);

    private OrderDB() {
    }

    public static OrderDB getInstance() {
        if (instance == null) {
            instance = new OrderDB();
        }
        return instance;
    }

    public List<String> getOrderId() {
        List<String> ids = new ArrayList<>();
        for (Order order : orderList) {
            ids.add(order.getOrderId());
        }
        return ids;
    }

    public List<Order> getOrders() {
        return orderList;
    }

    public List<Order> loadOrders(String FILE_PATH) {
        List<Order> orders = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                JSONObject json = new JSONObject(line);
                String orderId = json.optString("order_id", null);
                String userId = json.optString("user_id", null);
                String proId = json.optString("pro_id", null);
                String orderTime = json.optString("order_time", null);

                if (orderId != null && userId != null && proId != null && orderTime != null) {
                    orders.add(new Order(orderId, userId, proId, orderTime));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public void saveOrders(List<Order> orders) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/data/orders.json", true))) {
            for (Order order : orders) {
                String jsonString = order.toString();
                bw.write(jsonString);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
