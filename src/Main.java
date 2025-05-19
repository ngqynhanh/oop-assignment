import IOInterface.IOInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the E-commerce Application!");
        IOInterface ioInterface = IOInterface.getInstance();
        ioInterface.mainMenu();
    }
}
