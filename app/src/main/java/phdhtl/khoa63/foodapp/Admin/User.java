package phdhtl.khoa63.foodapp.Admin;

import com.google.firebase.database.Exclude;

public class User {
    private String userId;
    private String name;
    private String email;
    private String role;
    private boolean isBlocked;

    public User() {} // Constructor rỗng cần cho Firebase

    public User(String userId, String name, String email, String role, boolean isBlocked) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.isBlocked = isBlocked;
    }

    // Getters
    @Exclude
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isBlocked() { return isBlocked; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
}
