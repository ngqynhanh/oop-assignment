package DBUtil;
import Model.Order;
import java.util.*;

public class OrderDB {

    private static OrderDB instance;
    final private List<Order> orderList;

    private OrderDB() {
        orderList = Implementation.loadFromFile("src/data/orders.json", Order.class);
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
}
