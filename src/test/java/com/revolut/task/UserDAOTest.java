package com.revolut.task;

import com.revolut.task.dao.UserDAO;
import com.revolut.task.model.User;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class UserDAOTest extends BasicTest {

    @Test
    public void getUserByName() {
        Optional<User> user1Opt = new UserDAO(
                new User("Andreas", "Iniesta"), connectionPool
        ).get();
        Optional<User> user2Opt = new UserDAO(
                new User("David", "Backhem"), connectionPool
        ).get();

        assertTrue(user1Opt.isPresent());
        assertTrue(user2Opt.isPresent());
        assertEquals(user1Opt.get().getId(), new Long(1));
        assertEquals(user2Opt.get().getId(), new Long(3));
    }

    @Test
    public void getUserById() {
        Optional<User> user1Opt = new UserDAO.Identified(4L, connectionPool).get();
        Optional<User> user2Opt = new UserDAO.Identified(2L, connectionPool).get();

        assertTrue(user1Opt.isPresent());
        assertTrue(user2Opt.isPresent());
        assertEquals(user1Opt.get().getName(), "John");
        assertEquals(user2Opt.get().getName(), "Zenedin");
    }

    @Test
    public void noUser() {
        Optional<User> userOpt = new UserDAO(
                new User("Andrey", "Arshavin"), connectionPool
        ).get();

        assertFalse(userOpt.isPresent());
    }

    @Test
    public void newUser() {
        User newUser = new User("Leo", "Messi");
        UserDAO userDAO = new UserDAO(newUser, connectionPool);
        Optional<User> newUserOpt = userDAO.get();

        assertFalse(newUserOpt.isPresent());

        userDAO.submit();
        Optional<User> userOpt = userDAO.get();

        assertTrue(userOpt.isPresent());
        assertEquals(userOpt.get().getId(), new Long(5));
    }
}
