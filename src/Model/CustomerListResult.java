package Model;

import java.util.List;

public class CustomerListResult {
    public List<Customer> customerList;
    public int currentPage;
    public int totalPages;

    public CustomerListResult(List<Customer> customerList, int currentPage, int totalPages) {
        this.customerList = customerList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
