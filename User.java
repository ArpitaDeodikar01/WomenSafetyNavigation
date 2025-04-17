import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String username;
    private String name;
    private String encryptedPassword;
    private boolean isAdmin;

    public User(String username, String name, String password, boolean isAdmin) {
        this.username = username;
        this.name = name;
        this.encryptedPassword = encryptPassword(password);
        this.isAdmin = isAdmin;
    }

    // Encrypt password using SHA-256 hashing
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encryption error :(", e);
        }
    }

    public boolean checkPassword(String password) {
        return this.encryptedPassword.equals(encryptPassword(password));
    }

    public void displayUserInfo() {
        System.out.println("\n User Info: \n");
        System.out.println("Username: " + username);
        System.out.println("Name: " + name);
        System.out.println("Role: " + (isAdmin ? "* Administrator * " : "* Regular User *"));
        System.out.println("Password (Encrypted): * " + encryptedPassword.substring(0, 12) + "...");
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
