package com.bupt.model;

/**
 * User entity representing all system users (TA, MO, Admin).
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String role; // TA, MO, ADMIN
    private String name;
    private String email;
    private String phone;
    private String skills;          // comma-separated skills (for TA)
    private String cvPath;          // CV file path (for TA)
    private String studentId;       // student ID (for TA)
    private String reminderMethod;  // SMS or EMAIL (for TA)

    public User() {}

    public User(String id, String username, String password, String role, String name, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getReminderMethod() { return reminderMethod; }
    public void setReminderMethod(String reminderMethod) { this.reminderMethod = reminderMethod; }

    /**
     * Serialize to a line for txt storage.
     * Format: id|username|password|role|name|email|phone|skills|cvPath|studentId|reminderMethod
     */
    public String toFileLine() {
        return String.join("|",
                safe(id), safe(username), safe(password), safe(role),
                safe(name), safe(email), safe(phone), safe(skills), safe(cvPath),
                safe(studentId), safe(reminderMethod));
    }

    /**
     * Deserialize from a txt line.
     */
    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;
        User u = new User();
        u.setId(parts[0]);
        u.setUsername(parts[1]);
        u.setPassword(parts[2]);
        u.setRole(parts[3]);
        u.setName(parts[4]);
        u.setEmail(parts[5]);
        if (parts.length > 6) u.setPhone(parts[6]);
        if (parts.length > 7) u.setSkills(parts[7]);
        if (parts.length > 8) u.setCvPath(parts[8]);
        if (parts.length > 9) u.setStudentId(parts[9]);
        if (parts.length > 10) u.setReminderMethod(parts[10]);
        return u;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
