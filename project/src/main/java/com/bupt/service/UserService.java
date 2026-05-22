package com.bupt.service;

import com.bupt.dao.UserDao;
import com.bupt.model.User;
import java.util.List;

/**
 * Business logic for user operations.
 */
public class UserService {

    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        userDao.save(user);
        return true;
    }

    public User findById(String id) {
        return userDao.findById(id);
    }

    public void updateProfile(User user) {
        userDao.update(user);
    }

    public List<User> findAllTAs() {
        List<User> all = userDao.findAll();
        all.removeIf(u -> !"TA".equals(u.getRole()));
        return all;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public void deleteUser(String id) {
        userDao.delete(id);
    }

    public void updateUser(User user) {
        userDao.update(user);
    }
}
