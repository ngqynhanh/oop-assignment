package Model;

import java.util.List;

public class ProductListResult {
    private List<Product> products;
    private int currentPage;
    private int totalPages;

    public ProductListResult(List<Product> products, int currentPage, int totalPages) {
        this.products = products;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<Product> getOrders() {
        return products;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

}
