package Operation;

import DBUtil.OrderDB;
import DBUtil.ProductDB;
import Model.Product;
import Model.ProductListResult;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ProductOperation {
    private String filePath = "src/data/products.json";
    private static ProductOperation instance;

    private ProductOperation() {

    }

    public static ProductOperation getInstance() {
        if (instance == null) {
            instance = new ProductOperation();
        }
        return instance;
    }

    /**
     * Extracts product information from the given product data files.
     * The data is saved into the data/products.txt file.
     */
    public void extractProductFromFiles() {
        String input = "scr/data/temp_products.json";

        ProductDB.getInstance().saveProducts(ProductDB.getInstance().loadProducts(input));
    }

    public ProductListResult getProductList(int pageNumber) {
        List<Product> list = ProductDB.getInstance().getProducts().stream().toList();

        final int itemsPerPage = 10;
        int totalProducts= list.size();
        int totalPages = (int) Math.ceil((double) totalProducts / itemsPerPage);

        if (totalPages == 0)
            pageNumber = 1;
        else {
            if (pageNumber < 1) pageNumber = 1;
            if (pageNumber > totalPages) pageNumber = totalPages;
        }

        int startIndex = (pageNumber - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalProducts);

        List<Product> pageProduct;
        if (startIndex < 0 || startIndex >= totalProducts) {
            pageProduct = List.of(); // empty result
        } else {
            pageProduct = list.subList(startIndex, endIndex);
        }

        return new ProductListResult(pageProduct, pageNumber, totalPages);
    }

    public boolean deleteProduct(String productId) {
        StringBuilder product = new StringBuilder();
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
            if (!line.contains(productId)) {
                product.append(line).append("\n");
            }
        }

        // write updated content to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(product.toString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all products whose name contains the keyword (case insensitive).
     * @param keyword The search keyword
     * @return A list of Product objects matching the keyword
     */

    public List<Product> getProductListByKeyword(String keyword) {
        List<Product> productList = new ArrayList<>();
        List<Product> allProducts = ProductDB.getInstance().getProducts();

        for (Product product: allProducts) {
            if (product.getProName().toLowerCase().contains(keyword.toLowerCase())) {
                productList.add(product);
            }
        }
        return productList;
    }

    public Product getProductById(String productId) {
        Product product = null;
        List<Product> allProducts = ProductDB.getInstance().getProducts();

        for (Product p : allProducts) {
            if (p.getProId().equals(productId)) {
                product = p;
                break;
            }
        }
        return product;
    }

    /**
     * Generates a bar chart showing the total number of products
     * for each category in descending order.
     * Saves the figure into the data/figure folder.
     */
    public void generateCategoryFigure() {
        List<Product> allProducts = ProductDB.getInstance().getProducts();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Product product: allProducts) {
            String productCategory = product.getProCategory();
            categoryCount.put(productCategory, categoryCount.getOrDefault(productCategory, 0) + 1);
        }

        // Sort the map by value in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryCount.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Create a dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry: sortedEntries) {
            String catergory = entry.getKey();
            int numbers = entry.getValue();
            dataset.addValue(numbers, "Sales", catergory);
        }

        // Create a bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Category Figure",
                "Product Category",
                "Count",
                dataset
        );
        // save chart as image
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);

        try {
            File output = new File("charts/Category_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a pie chart showing the proportion of products that have
     * a discount value less than 30, between 30 and 60 inclusive,
     * and greater than 60.
     * Saves the figure into the data/figure folder.
     */

    public void generateDiscountFigure() {
        List<Product> allProducts = ProductDB.getInstance().getProducts();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Product product: allProducts) {
            double proDiscount = product.getProDiscount();
            if (proDiscount < 30) {
                categoryCount.put("Less than 30", categoryCount.getOrDefault("Less than 30", 0) + 1);
            } else if (proDiscount >= 30 && proDiscount <= 60) {
                categoryCount.put("Between 30 and 60", categoryCount.getOrDefault("Between 30 and 60", 0) + 1);
            } else {
                categoryCount.put("Greater than 60", categoryCount.getOrDefault("Greater than 60", 0) + 1);
            }
        }

        // Create a dataset for the pie chart
        DefaultPieDataset dataset = new DefaultPieDataset( );
        for(Map.Entry<String, Integer> entry: categoryCount.entrySet()) {
            String category = entry.getKey();
            int numbers = entry.getValue();
            dataset.setValue(category, numbers);
        }

        // Create a bar chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Discount Figure",
                dataset,
                true,
                true,
                false
        );

        try {
            File output = new File("charts/Discount_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, pieChart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a chart displaying the sum of products' likes_count
     * for each category in ascending order.
     * Saves the figure into the data/figure folder.
     */
    public void generateLikesCountFigure() {
        List<Product> allProducts = ProductDB.getInstance().getProducts();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Product product: allProducts) {
            int prodLikeCount = product.getProLikesCount();
            categoryCount.put(product.getProName(), prodLikeCount);
        }

        // Sort the map by value in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryCount.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

        // Create a dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry: sortedEntries) {
            String name = entry.getKey();
            int numbers = entry.getValue();
            dataset.addValue(numbers, "Sales", name);
        }

        // Create a bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Likes Count Figure",
                "Product Name",
                "Likes Count",
                dataset
        );
        // save chart as image
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);

        try {
            File output = new File("charts/Likes_Count_Figure.png");
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs(); // create directories if they don't exist
            }
            ChartUtils.saveChartAsPNG(output, chart, 1200, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllProducts() {
        String filePath = "src/data/products.json";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
