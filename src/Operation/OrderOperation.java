package Operation;

import DBUtil.ProductDB;
import Model.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONObject;

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

        // create order
        order.append("{\"order_id\":\"").append(orderId).append("\",")
                .append("\"user_id\":\"").append(customerId).append("\",")
                .append("\"pro_id\":\"").append(productId).append("\",")
                .append("\"order_time\":\"").append(createTime).append("\"}");

        // write order to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(order.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
// need change when the users classes are done
    public void generateTestOrderData() {
        int customerCount = 10;
        int orderCount = 50 + (int) (Math.random() * 151); // Random number between 50 and 200

        try (BufferedWriter orderWriter = new BufferedWriter(new FileWriter(filePath));
            BufferedWriter customerWriter = new BufferedWriter(new FileWriter("src/data/users.json"))) {
            // generate random number of orders
            for (int i = 0; i < customerCount; i++) {
                String customerId = "user_" + i;
                String userName = "user_" + i;
                String userEmail = "user_" + i + "@example.com";
                String userPhone = "123456789" + i;

                // write customer to file
                customerWriter.write("{\"user_id\":\"" + customerId + "\",\"user_name\":\"" + userName + "\",\"user_email\":\"" + userEmail + "\",\"user_phone\":\"" + userPhone + "\"}");
                customerWriter.newLine();
                for (int j = 0; j < orderCount; j++) {
                    String orderId = generateUniqueOrder();
                    String productId = "p" + (int) (Math.random() * 100);
                    String orderTime = "2023-" + ((int) (Math.random() * 12) + 1) + "-" + ((int) (Math.random() * 28) + 1) + "T" + ((int) (Math.random() * 24)) + ":" + ((int) (Math.random() * 60)) + ":00Z";

                    // write order to file
                    orderWriter.write("{\"order_id\":\"" + orderId + "\",\"user_id\":\"" + customerId + "\",\"pro_id\":\"" + productId + "\",\"order_time\":\"" + orderTime + "\"}");
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
    // need change for 12 months (not only for the month the users buy products)
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        List<Order> allOrders = OrderDB.getInstance().getOrders();
        List<Product> allProducts = ProductDB.getInstance().getProducts();

        List<Order> customerOrders = new ArrayList<>();
        for (Order order : allOrders) {
            if (customerId.equals(order.getUserId())) {
                customerOrders.add(order);
            }
        }

        Map<String, Double> customerConsumption = new HashMap<>();
        for (Order order : customerOrders) {
            String orderTime = order.getOrderTime();
            String month = orderTime.substring(3, 5);
            String productId = order.getProId();
            for (Product product : allProducts) {
                if (productId.equals(product.getProId())) {
                    customerConsumption.put(month, customerConsumption.getOrDefault(month, 0.0) + product.getProCurrentPrice());
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
            ChartUtils.saveChartAsPNG(output, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllOrder() {
        String filePath = "src/data/orders.json";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
