package IOInterface;

import DBUtil.OrderDB;
import DBUtil.UserDB;
import Model.Customer;
import Model.Product;
import Model.ProductListResult;
import Model.User;
import Operation.*;
import java.util.List;
import java.util.Scanner;

public class IOInterface {

    private static IOInterface instance = null;
    private Scanner scanner = new Scanner(System.in);
    private static String userID = null;
    ProductOperation productOperation = ProductOperation.getInstance();
    OrderOperation orderOperation = OrderOperation.getInstance();
    CustomerOperation customerOperation = CustomerOperation.getInstance();
    UserOperation userOperation = UserOperation.getInstance();

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

                User user = userOperation.login(username, password);
                if (user == null) {
                    System.out.println("Login failed. Please check your credentials.");
                    mainMenu();
                }
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
                String[] registerDetails = getUserInput("Enter your username, password, email, and phone number: ", 4);
                String regUsername = registerDetails[0];
                String regPassword = registerDetails[1];
                String regEmail = registerDetails[2];
                String regPhone = registerDetails[3];

                if (customerOperation.registerCustomer(regUsername, regPassword, regEmail, regPhone)) {
                    System.out.println("Registration successful. Welcome, " + regUsername + "!");
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

                    IOInterface.getInstance().showList("admin", "Product", products, currentPage, totalPages);

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
                    String[] details = getUserInput("Enter customer details (username, password, email, phone): ", 4);
                    String username = details[0];
                    String password = details[1];
                    String email = details[2];
                    String phone = details[3];

                    if (customerOperation.registerCustomer(username, password, email, phone)) {
                        System.out.println("Customer " + username + " added successfully.");
                    } else {
                        System.out.println("Failed to add customer " + username + ". Username may already exist or invalid input.");
                    }
                    isEnd = true;
                }
                mainMenu();
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
                        mainMenu();
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
                mainMenu();
            case "6":
                System.out.println("================================");
                System.out.println("Generating all statistical figures...");
                String customerId = getUserInput("Enter customer ID (or leave blank for all): ", 1)[0];

                orderOperation.generateSingleCustomerConsumptionFigure(customerId);
                orderOperation.generateAllCustomersConsumptionFigure();
                orderOperation.generateAllTop10BestSellersFigure();

                productOperation.generateDiscountFigure();
                productOperation.generateCategoryFigure();
                productOperation.generateCategoryFigure();
                mainMenu();
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
                mainMenu();
            case "8":
                System.out.println("================================");
                System.out.println("Logging out...");
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
                mainMenu();
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
        Customer customer = null;
        for (User user : UserDB.getInstance().getUsers()) {
            if (user.getId().equals(userID) && user instanceof Customer) {
                customer = (Customer) user;
                break;
            }
        }

        switch (choice) {
            case "1":
                System.out.println("================================");
                System.out.println("Showing profile...");
                System.out.println(customer.toString());
                mainMenu();
            case "2":
                System.out.println("================================");
                System.out.println("Updating profile...");
                String[] details = getUserInput("Enter your new details: ", 2);
                if (customerOperation.updateProfile(details[0], details[1], customer))
                    System.out.println("Profile updated successfully.");
                else
                    System.out.println("Profile update failed.");
                mainMenu();
            case "3":
                System.out.println("================================");
                System.out.println("Showing products...");
                productOperation.getProductById(customer.getId());
                mainMenu();
            case "4":
                System.out.println("================================");
                System.out.println("Showing history orders...");
                orderOperation.getOrderList(customer.getId(), 1);
                mainMenu();
            case "5":
                System.out.println("================================");
                System.out.println("Generating all consumption figures...");
                orderOperation.generateSingleCustomerConsumptionFigure(customer.getId());
                mainMenu();
            case "6":
                System.out.println("================================");
                System.out.println("Logging out...");
                System.exit(0);
            default:
                System.out.println("Invalid option. Please try again.");
                mainMenu();
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
}
