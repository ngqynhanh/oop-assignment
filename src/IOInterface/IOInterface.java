package IOInterface;

import Model.Product;
import Model.ProductListResult;

import java.util.List;

public class IOInterface {

    private static IOInterface instance = null;

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
        String input = System.console().readLine();
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
    public <T> void showList(String userRole, String listType, List<T> objectList, int pageNumber, int totalPages) {
        System.out.println("===== " + listType + " List =====");
        System.out.println("Page " + pageNumber + " of " + totalPages);
        System.out.println("====================================");
        System.out.println("====================================");
        System.out.print("Press Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
