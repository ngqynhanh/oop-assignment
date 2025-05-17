package Model;

public class Order {
    private String orderId;
    private String userId;
    private String proId;
    private String orderTime;

    // Constructors
    public Order() {
    }
    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

    // Methods
    @Override
    public String toString() {
        return "{" +
                "order_id:\"" + orderId + '\"' +
                ",user_id:\"" + userId + '\"' +
                ",pro_id:\"" + proId + '\"' +
                ",order_time:\"" + orderTime + '\"' +
                '}';
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

}
