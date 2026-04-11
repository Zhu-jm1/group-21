package com.bupt.dao;

import com.bupt.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access for User entities, stored in users.txt.
 */
public class UserDao extends FileBaseDao {

    private static final String FILE_NAME = "users.txt";

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (String line : readAllLines(FILE_NAME)) {
            User u = User.fromFileLine(line);
            if (u != null) users.add(u);
        }
        return users;
    }

    public User findById(String id) {
        return findAll().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public User findByUsername(String username) {
        return findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public void save(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        appendLine(FILE_NAME, user.toFileLine());
    }

    public void update(User user) {
        List<User> all = findAll();
        List<String> lines = new ArrayList<>();
        for (User u : all) {
            if (u.getId().equals(user.getId())) {
                lines.add(user.toFileLine());
            } else {
                lines.add(u.toFileLine());
            }
        }
        writeAllLines(FILE_NAME, lines);
    }

    public void delete(String id) {
        List<User> all = findAll();
        List<String> lines = new ArrayList<>();
        for (User u : all) {
            if (!u.getId().equals(id)) {
                lines.add(u.toFileLine());
            }
        }
        writeAllLines(FILE_NAME, lines);
    }
}
