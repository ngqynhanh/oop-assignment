package Model;

import DBUtil.Implementation;
import DBUtil.OrderDB;

import java.util.ArrayList;
import java.util.List;


public class OrderListResult {
    private List<Order> orders;
    private int currentPage;
    private int totalPages;

    public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
        this.orders = orders;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

}
