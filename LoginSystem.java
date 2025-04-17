import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginSystem {
    private Map<String, User> users;

    public LoginSystem() {
        users = new HashMap<>();
        // Add default admin user
        registerUser("admin", "Administrator", "admin123", true);
    }

    // Register a new user
    public void registerUser(String username, String name, String password, boolean isAdmin) {
        if (users.containsKey(username)) {
            System.out.println(" Username '" + username + "' already exists!");
            return;
        }
        User newUser = new User(username, name, password, isAdmin);
        users.put(username, newUser);
        System.out.println(" :) Registration successful for " + username + "!");
    }

    // Attempt to log a user in
    public User login(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            System.out.println(" :( User not found!");
            return null;
        }

        if (user.checkPassword(password)) {
            System.out.println(": ) Login successful!");
            user.displayUserInfo();
            return user;
        } else {
            System.out.println(" : ( Incorrect password!");
            return null;
        }
    }

    // User registration process
    public User registerNewUser(Scanner scanner) {
        System.out.println("\n=== New User Registration === \n");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Create password: ");
        String password = scanner.nextLine();

        registerUser(username, name, password, false);
        return users.get(username);
    }

    // Optional: Display all users (for debugging)
    public void displayAllUsers() {
        System.out.println("\n Registered Users: \n");
        for (User user : users.values()) {
            user.displayUserInfo();
        }
    }
}
