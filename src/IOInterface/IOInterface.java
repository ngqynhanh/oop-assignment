package IOInterface;

import DBUtil.OrderDB;
import DBUtil.UserDB;
import Model.*;
import Operation.*;
import java.util.List;
import java.util.Scanner;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class IOInterface {

    private static IOInterface instance = null;
    private Scanner scanner = new Scanner(System.in);
    private ProductOperation productOperation = ProductOperation.getInstance();
    private OrderOperation orderOperation = OrderOperation.getInstance();
    private CustomerOperation customerOperation = CustomerOperation.getInstance();
    private UserOperation userOperation = UserOperation.getInstance();
    private static User user = null;

    private IOInterface() {
    }

    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    /**
     * - The message is used for the input prompt.
     * - User inputs have only one format with all arguments connected by a whitespace ” “.
     * - If users input more than numOfArgs arguments, ignore the extras.
     * - If users input fewer than numOfArgs arguments, fill the remaining with empty strings.
     * @param message The message to display for input prompt
     * @param numOfArgs The number of arguments expected
     * @return An array of strings containing the arguments
     */
    public String[] getUserInput(String message, int numOfArgs) {
        String[] userInput = new String[numOfArgs];
        System.out.print(message);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        String[] inputs = input.split(" ");
        for (int i = 0; i < numOfArgs; i++) {
            if (i < inputs.length) {
                userInput[i] = inputs[i];
            } else {
                userInput[i] = "";
            }
        }
        return userInput;
    }

    /**
     * Display the login menu with options: (1) Login, (2) Register, (3) Quit.
     * The admin account cannot be registered.
     */
    public void mainMenu() {
        System.out.println("===== E-Commerce System =====");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Quit");
        System.out.print("Please select an option: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                System.out.println("================================");
                System.out.println("Log in");
                String[] loginDetails = getUserInput("Enter your username and password: ", 2);
                String username = loginDetails[0];
                String password = loginDetails[1];

                user = userOperation.login(username, password);
                if (user.getRole().equals("admin")) {
                    System.out.println("Login successful. Welcome, " + username + "!");
                    adminMenu();
                } else if (user.getRole().equals("customer")) {
                    System.out.println("Login successful. Welcome, " + username + "!");
                    customerMenu();
                } else {
                    System.out.println("Invalid username or password.");
                }
                mainMenu();
            case "2":
                System.out.println("================================");
                System.out.println("Register");
                String[] registerDetails = getUserInput("Enter your username, password, email, and mobile number: ", 4);
                String regUsername = registerDetails[0];
                String regPassword = registerDetails[1];
                String regEmail = registerDetails[2];
                String regPhone = registerDetails[3];

                if (customerOperation.registerCustomer(regUsername, regPassword, regEmail, regPhone)) {
                    System.out.println("Registration successful. Welcome, " + regUsername + "!");
                    for (User user : UserDB.getInstance().getUsers()) {
                        if (user.getId().equals(regUsername) && user instanceof Customer) {
                            IOInterface.user = new Customer(user.getId(), user.getName(), user.getPassword(), user.getRegisteredAt(), ((Customer) user).getEmail(), ((Customer) user).getPhone());
                            break;
                        }
                    }
                    customerMenu();
                } else {
                    System.out.println("Registration failed. Username may already exist or invalid input.");
                }


                mainMenu();
            case "3":
                System.out.println("Exiting the system...");
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Display the admin menu with options:
     * (1) Show products
     * (2) Add customers
     * (3) Show customers
     * (4) Show orders
     * (5) Generate test data
     * (6) Generate all statistical figures
     * (7) Delete all data
     * (8) Logout
     */
    public void adminMenu() {
        System.out.println("===== Admin Menu =====");
        System.out.println("1. Show products");
        System.out.println("2. Add customers");
        System.out.println("3. Show customers");
        System.out.println("4. Show orders");
        System.out.println("5. Generate test data");
        System.out.println("6. Generate all statistical figures");
        System.out.println("7. Delete all data");
        System.out.println("8. Logout");

        String choice = getUserInput("Please select an option: ", 1)[0];

        switch (choice) {
            case "1":
                System.out.println("================================");
                System.out.println("Showing products...");

                int currentPage = 1;

                while (true) {
                    ProductListResult result = ProductOperation.getInstance().getProductList(currentPage);
                    List<Product> products = result.getProducts();
                    int totalPages = result.getTotalPages();

                    showList("admin", "Product", products, currentPage, totalPages);

                    System.out.println();
                    choice = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1)[0];

                    if (choice.equals("n")) {
                        if (currentPage < totalPages) {
                            currentPage++;
                        } else {
                            System.out.println("Already on the last page.");
                        }
                    } else if (choice.equals("p")) {
                        if (currentPage > 1) {
                            currentPage--;
                        } else {
                            System.out.println("Already on the first page.");
                        }
                    } else if (choice.equals("b")) {
                        adminMenu();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            case "2":
                System.out.println("================================");
                System.out.println("Adding customers...");

                boolean isEnd = false;
                while (!isEnd) {
                    String[] details = getUserInput("Enter customer details (username, password, email, phone) or 'end' to finish: ", 4);
                    if (details[0].equalsIgnoreCase("end")) {
                        isEnd = true;
                        break;
                    }
                    String username = details[0];
                    String password = details[1];
                    String email = details[2];
                    String phone = details[3];

                    if (customerOperation.registerCustomer(username, password, email, phone)) {
                        System.out.println("Customer " + username + " added successfully.");
                        for (User user : UserDB.getInstance().getUsers()) {
                            if (user.getId().equals(username) && user instanceof Customer) {
                                IOInterface.user = new Customer(user.getId(), user.getName(), user.getPassword(), user.getRegisteredAt(), ((Customer) user).getEmail(), ((Customer) user).getPhone());
                                break;
                            }
                        }
                    } else {
                        System.out.println("Failed to add customer " + username + ". Username may already exist or invalid input.");
                    }
                    isEnd = true;
                }
                adminMenu();
            case "3":
                System.out.println("================================");
                System.out.println("Showing customers...");
                int currentPageCustomers = 1;
                while (true) {
                    ProductListResult result = ProductOperation.getInstance().getProductList(currentPageCustomers);
                    List<Product> products = result.getProducts();
                    int totalPages = result.getTotalPages();

                    IOInterface.getInstance().showList("admin", "Customer", products, currentPageCustomers, totalPages);

                    System.out.println();
                    choice = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1)[0];

                    if (choice.equals("n")) {
                        if (currentPageCustomers < totalPages) {
                            currentPageCustomers++;
                        } else {
                            System.out.println("Already on the last page.");
                        }
                    } else if (choice.equals("p")) {
                        if (currentPageCustomers > 1) {
                            currentPageCustomers--;
                        } else {
                            System.out.println("Already on the first page.");
                        }
                    } else if (choice.equals("b")) {
                        adminMenu();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            case "4":
                System.out.println("================================");
                System.out.println("Showing orders...");
                int currentPageOrders = 1;
                while(true) {
                    ProductListResult result = ProductOperation.getInstance().getProductList(currentPageOrders);
                    List<Product> products = result.getProducts();
                    int totalPages = result.getTotalPages();

                    IOInterface.getInstance().showList("admin", "Order", products, currentPageOrders, totalPages);

                    System.out.println();
                    choice = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1)[0];

                    if (choice.equals("n")) {
                        if (currentPageOrders < totalPages) {
                            currentPageOrders++;
                        } else {
                            System.out.println("Already on the last page.");
                        }
                    } else if (choice.equals("p")) {
                        if (currentPageOrders > 1) {
                            currentPageOrders--;
                        } else {
                            System.out.println("Already on the first page.");
                        }
                    } else if (choice.equals("b")) {
                        adminMenu();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            case "5":
                System.out.println("================================");
                System.out.println("Generating test data...");
                orderOperation.generateTestOrderData();
                for(int i = 0; i < OrderDB.getInstance().getOrders().size(); i++) {
                    System.out.println(i + 1 + ". " + OrderDB.getInstance().getOrders().get(i));
                }
                adminMenu();
            case "6":
                System.out.println("================================");
                System.out.println("Generating all statistical figures...");
                String customerId = getUserInput("Enter customer ID (or leave blank for all): ", 1)[0];

                //open files in windows app
//                if (!customerId.isEmpty()) {
//                    orderOperation.generateSingleCustomerConsumptionFigure(customerId);
//                    System.out.println("Single Customer Consumption Figure: " + customerId + " generated.");
//                    showChart("src/data/figure/Single_Customer_Consumption_Figure.png");
//                }
//
//                orderOperation.generateAllCustomersConsumptionFigure();
//                System.out.println("All Customers Consumption Figure generated.");
//                showChart("src/data/figure/All_Customer_Consumption_Figure.png");
//
//                orderOperation.generateAllTop10BestSellersFigure();
//                System.out.println("Top 10 Best Sellers Figure generated.");
//                showChart("src/data/figure/Top_10_Best_Selling_Figure.png");
//
//                productOperation.generateDiscountFigure();
//                System.out.println("Discount Figure generated.");
//                showChart("src/data/figure/Discount_Figure.png");
//
//                productOperation.generateCategoryFigure();
//                System.out.println("Category Figure generated.");
//                showChart("src/data/figure/Category_Figure.png");
//
//                productOperation.generateLikesCountFigure();
//                System.out.println("Likes Count Figure generated.");
//                showChart("src/data/figure/Likes_Count_Figure.png");

                // open in GUI
                if (!customerId.isEmpty()) {
                    orderOperation.generateSingleCustomerConsumptionFigure(customerId);
                    System.out.println("Single Customer Consumption Figure: " + customerId + " generated.");
                }

                orderOperation.generateAllTop10BestSellersFigure();
                System.out.println("Top 10 Best Sellers Figure generated.");

                orderOperation.generateAllCustomersConsumptionFigure();
                System.out.println("All Customers Consumption Figure generated.");

                productOperation.generateDiscountFigure();
                System.out.println("Discount Figure generated.");

                productOperation.generateCategoryFigure();
                System.out.println("Category Figure generated.");

                productOperation.generateLikesCountFigure();
                System.out.println("Likes Count Figure generated.");


                adminMenu();
            case "7":
                System.out.println("================================");
                System.out.println("Deleting all data...");
                System.out.println("Are you sure you want to delete all data? (yes/no)");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    customerOperation.deleteAllCustomers();
                    productOperation.deleteAllProducts();
                    orderOperation.deleteAllOrders();
                    System.out.println("All data deleted successfully.");
                } else {
                    System.out.println("Data deletion canceled.");
                }
                adminMenu();
            case "8":
                System.out.println("================================");
                System.out.println("Logging out...");
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
                adminMenu();
        }
    }

    /**
     * Display the customer menu with options:
     * (1) Show profile
     * (2) Update profile
     * (3) Show products (user input could be "3 keyword" or "3")
     * (4) Show history orders
     * (5) Generate all consumption figures
     * (6) Logout
     */

    public void customerMenu() {
        System.out.println("===== Customer Menu =====");
        System.out.println("1. Show profile");
        System.out.println("2. Update profile");
        System.out.println("3. Show products");
        System.out.println("4. Show history orders");
        System.out.println("5. Generate all consumption figures");
        System.out.println("6. Logout");

        String choice = getUserInput("Please select an option: ", 1)[0];
        ProductOperation productOperation = ProductOperation.getInstance();
        OrderOperation orderOperation = OrderOperation.getInstance();
        CustomerOperation customerOperation = CustomerOperation.getInstance();

        switch (choice) {
            case "1":
                System.out.println("================================");
                System.out.println("Showing profile...");
                printObject(IOInterface.user);
                customerMenu();
            case "2":
                System.out.println("================================");
                System.out.println("Updating profile...");
                System.out.println("The attributes allow to update: user_name, user_password, user_email, user_mobile)");
                String[] details = getUserInput("Enter your new details (for example: user_email john.doe@example.com.vn): ", 2);
                if (customerOperation.updateProfile(details[0], details[1], (Customer) user))
                    System.out.println("Profile updated successfully.");
                else
                    System.out.println("Profile update failed.");
                customerMenu();
            case "3":
                System.out.println("================================");
                System.out.println("Showing products...");

                int currentPage = 1;
                while (true) {
                    ProductListResult result = productOperation.getProductList(currentPage);
                    List<Product> products = result.getProducts();
                    int totalPages = result.getTotalPages();

                    showList("customer", "Product", products, currentPage, totalPages);

                    System.out.println();
                    choice = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1)[0];

                    if (choice.equals("n")) {
                        if (currentPage < totalPages) {
                            currentPage++;
                        } else {
                            System.out.println("Already on the last page.");
                        }
                    } else if (choice.equals("p")) {
                        if (currentPage > 1) {
                            currentPage--;
                        } else {
                            System.out.println("Already on the first page.");
                        }
                    } else if (choice.equals("b")) {
                        customerMenu();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            case "4":
                System.out.println("================================");
                System.out.println("Showing history orders...");

                int currentPageOrders = 1;
                while (true) {
                    OrderListResult result = orderOperation.getOrderList(this.user.getId(),currentPageOrders);
                    List<Order> orders = result.getOrders();
                    int totalPages = result.getTotalPages();

                    showList("customer", "Order", orders, currentPageOrders, totalPages);

                    System.out.println();
                    choice = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back\nEnter your choice: ", 1)[0];

                    if (choice.equals("n")) {
                        if (currentPageOrders < totalPages) {
                            currentPageOrders++;
                        } else {
                            System.out.println("Already on the last page.");
                        }
                    } else if (choice.equals("p")) {
                        if (currentPageOrders > 1) {
                            currentPageOrders--;
                        } else {
                            System.out.println("Already on the first page.");
                        }
                    } else if (choice.equals("b")) {
                        customerMenu();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                }
            case "5":
                System.out.println("================================");
                System.out.println("Generating all consumption figures...");
                orderOperation.generateSingleCustomerConsumptionFigure(user.getId());
                customerMenu();
            case "6":
                System.out.println("================================");
                System.out.println("Logging out...");
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
                customerMenu();
        }
    }

    /**
     * Prints out different types of lists (Customer, Product, Order).
     * Shows row number, page number, and total page number.
     * @param userRole The role of the current user
     * @param listType The type of list to display
     * @param objectList The list of objects to display
     * @param pageNumber The current page number
     * @param totalPages The total number of pages
     */
    // Simpler: one-page-only view
    public <T> void showList(String userRole, String listType, List<T> objectList, int pageNumber, int totalPages) {
        System.out.println("===== " + listType + " List =====");
        System.out.println("Page " + pageNumber + " of " + totalPages);
        for (int i = 0; i < objectList.size(); i++) {
            System.out.println((i + 1) + ". " + objectList.get(i));
        }
        System.out.println("====================================");
        System.out.print("Press Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printErrorMessage(String message) {
        System.out.println("Error: " + message);
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printObject(Object targetObject) {
        System.out.println(targetObject.toString());
    }

    public void showChart(String filePath) {
        try {
            File chartFile = new File(filePath);
            if (chartFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(chartFile);
            } else {
                printMessage("Chart file not found or Desktop not supported.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
