package Operation;

import DBUtil.ProductDB;
import DBUtil.UserDB;
import Model.Customer;
import Model.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import DBUtil.OrderDB;
import Model.Order;
import Model.OrderListResult;

import java.io.*;
import java.util.*;

public class OrderOperation {
    private String filePath = "src/data/orders.json";
    private static OrderOperation instance;

    private OrderOperation() {
    }

    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    public String generateUniqueOrder() {
        String id = "o_";
        StringBuilder orderId = new StringBuilder();
        List<String> availableId = OrderDB.getInstance().getOrders().stream().map(Order::getOrderId).toList();

        long seed = System.nanoTime();
        Random random = new Random(seed);
        boolean isUnique = false;

        while (!isUnique) {
            for (int i = 0; i < 5; i++) {
                int randomNumber = random.nextInt(10);
                orderId.append(randomNumber);
            }
            if (!availableId.contains(orderId.toString())) {
                isUnique = true;
            } else {
                orderId.setLength(0); // Reset the StringBuilder if not unique
            }
        }


        return id + orderId.toString();
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        String orderId = generateUniqueOrder();
        StringBuilder order = new StringBuilder();
        if (customerId == null || productId == null || createTime == null) {
            return false;
        }
        else {
            OrderDB.getInstance().saveOrders(List.of(new Order(orderId, customerId, productId, createTime)));
            return true;
        }
    }

    public boolean deleteOrder(String orderId) {
        StringBuilder order = new StringBuilder();
        String fileContent = "";

        // read file content
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("]")) {
                    line.replace("]", "");
                }
                if (line.contains("[")) {
                    line.replace("[", "");
                }
                fileContent += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // delete order
        String[] lines = fileContent.split("\n");
        for (String line : lines) {
            if (!line.contains(orderId)) {
                order.append(line).append("\n");
            }
        }

        // write updated content to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(order.toString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> customerOrders = OrderDB.getInstance().getOrders().stream().filter(order -> order.getUserId().equals(customerId)).toList();

        final int itemsPerPage = 10;
        int totalOrders = customerOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / itemsPerPage);

        if (totalPages == 0)
            pageNumber = 1;
        else {
            if (pageNumber < 1) pageNumber = 1;
            if (pageNumber > totalPages) pageNumber = totalPages;
        }

        int startIndex = (pageNumber - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalOrders);

        List<Order> pageOrder;
        if (startIndex < 0 || startIndex >= totalOrders) {
            pageOrder = List.of(); // empty result
        } else {
            pageOrder = customerOrders.subList(startIndex, endIndex);
        }

        return new OrderListResult(pageOrder, pageNumber, totalPages);
    }

    /**
     * Automatically generates test data including customers and orders.
     * Creates 10 customers and randomly generates 50-200 orders for each.
     * Order times should be scattered across different months of the year.
     */

    private String generatePassword() {
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    /**
     * generate time order with different months of the year
     */

    private String generateTime() {
        StringBuilder orderTime = new StringBuilder();
        Random random = new Random();
        int day = random.nextInt(28) + 1; // Day between 1 and 28
        int month = random.nextInt(12) + 1; // Month between 1 and 12
        int year = random.nextInt(3) + 2023; // Year between 2023 and 2025
        int hour = random.nextInt(24); // Hour between 0 and 23
        int minute = random.nextInt(60); // Minute between 0 and 59
        int second = 2024; // Second between 0 and 59

        orderTime.append(String.format("%02d-%02d-%04d_%02d:%02d:%02d", day, month, year, hour, minute, second));
        return orderTime.toString();
    }

    public void generateTestOrderData() {
        int customerCount = 10;
        int orderCount = 50 + (int) (Math.random() * 151); // Random number between 50 and 200

        try (BufferedWriter orderWriter = new BufferedWriter(new FileWriter(filePath, true));
            BufferedWriter customerWriter = new BufferedWriter(new FileWriter("src/data/users.json", true))) {
            // generate random number of orders
            UserOperation userOperation = UserOperation.getInstance();
            for (int i = 0; i < customerCount; i++) {
                String customerId = userOperation.generateUserId();
                String userName = userOperation.generateUserName();
                String userEmail = userName + "@example.com";
                String userPhone = CustomerOperation.getInstance().generateCustomerPhone();
                String rawPassword = generatePassword();
                String password = CustomerOperation.getInstance().decryptPassword(rawPassword);
                String registerTime = generateTime();

                // write customer to file
                customerWriter.write(new Customer(customerId, userName, password, registerTime, userEmail, userPhone).toString());
                customerWriter.newLine();
                for (int j = 0; j < orderCount; j++) {
                    String orderId = generateUniqueOrder();
                    String productId = "p" + (int) (Math.random() * 100);
                    String orderTime = generateTime();

                    // write order to file
                    orderWriter.write(new Order(orderId, customerId, productId, orderTime).toString());
                    orderWriter.newLine();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a chart showing the consumption (sum of order prices)
     * across 12 different months for the given customer.
     * @param customerId The ID of the customer
     */
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        List<Order> allOrders = OrderDB.getInstance().getOrders();
        List<Product> allProducts = ProductDB.getInstance().getProducts();

        List<Order> customerOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (customerId.equals(order.getUserId())) {
                customerOrders.add(order);
            }
        }

        // Initialize map with 12 months (01 to 12) set to 0.0
        Map<String, Double> customerConsumption = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            String month = String.format("%02d", i); // "01", "02", ..., "12"
            customerConsumption.put(month, 0.0);
        }

        // Accumulate consumption by month
        for (Order order : customerOrders) {
            String orderTime = order.getOrderTime();
            String month = orderTime.substring(3, 5); // assumes format is "dd-MM-yyyy_..."
            String productId = order.getProId();

            for (Product product : allProducts) {
                if (productId.equals(product.getProId())) {
                    double current = customerConsumption.getOrDefault(month, 0.0);
                    customerConsumption.put(month, current + product.getProCurrentPrice());
                    break;
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : customerConsumption.entrySet()) {
            String month = entry.getKey();
            dataset.addValue(entry.getValue(), "Consumption", month);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Single Customer Consumption",
                "Monthly Consumption",
                "Consumption",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);

        try {
            File output = new File("charts/Single_Customer_Consumption_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, chart, 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAllCustomersConsumptionFigure() {
        Map<String, Double> customerConsumption = new HashMap<>();
        List<Order> allOrders = OrderDB.getInstance().getOrders();
        List<Product> allProducts = ProductDB.getInstance().getProducts();
        for (Order order: allOrders) {
            String customerId = order.getUserId();
            String productId = order.getProId();
            for (Product product: allProducts) {
                if (productId.equals(product.getProId())) {
                    customerConsumption.put(customerId, customerConsumption.getOrDefault(customerId, 0.0) + product.getProCurrentPrice());
                    break;
                }
            }
        }

        // create a dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : customerConsumption.entrySet()) {
            String customerId = entry.getKey();
            if (customerId == null) {
                continue;
            }
            dataset.addValue(entry.getValue(), "Consumption", customerId);
        }


        // create a bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Customer Consumption",
                "Customer ID",
                "Consumption",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.2);

        try {
            File output = new File("charts/All_Customer_Consumption_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a graph showing the top 10 best-selling products
     * sorted in descending order.
     */
    public void generateAllTop10BestSellersFigure() {
        // Implementation using Java charting library
        Map<String, Integer> productSales = new HashMap<>();

        List<Order> products = OrderDB.getInstance().getOrders();
        for (Order order : products) {
            String productId = order.getProId();
            productSales.put(productId, productSales.getOrDefault(productId, 0) + 1);
        }

        // Sort the map by value in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productSales.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // limit to top 10
        if (sortedEntries.size() > 10) {
            sortedEntries = sortedEntries.subList(0, 10);
        }

        // Create a dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry: sortedEntries) {
            String productId = entry.getKey();
            int salesCount = entry.getValue();
            dataset.addValue(salesCount, "Sales", productId);
        }

        // Create a bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 10 Best-Selling Products",
                "Product ID",
                "Sales Count",
                dataset
        );
        // save chart as image
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.2);

        try {
            File output = new File("charts/Top_10_Best-Selling_Products_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllOrders() {
        String filePath = "src/data/orders.json";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
