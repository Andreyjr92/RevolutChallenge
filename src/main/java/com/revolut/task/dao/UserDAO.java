package com.revolut.task.dao;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dto.PersonDTO;
import com.revolut.task.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * <p>DAO class, brings {@link User} to database.
 * Withdraw and deposit methods implemented to work in multithreaded environment</p>
 */
public class UserDAO extends User {

    private static final String INSERT_QUERY = "INSERT INTO PERSON (NAME, SURNAME) VALUES (?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM PERSON WHERE NAME = ? AND SURNAME = ?";
    private TransactionalBlockingConnectionPool pool;

    public UserDAO(User user, TransactionalBlockingConnectionPool pool) {
        super(user.getName(), user.getSurname());
        this.pool = pool;
    }

    public UserDAO(PersonDTO personDTO, TransactionalBlockingConnectionPool pool) {
        super(personDTO.getName(), personDTO.getSurname());
        this.pool = pool;
    }

    public Optional<User> get() {
        Connection connection = pool.getConnection();
        try (PreparedStatement selectPersonPS = connection.prepareStatement(SELECT_QUERY)) {
            selectPersonPS.setString(1, name);
            selectPersonPS.setString(2, surname);
            ResultSet resultSet = selectPersonPS.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(new User(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.putConnectionBack(connection);
        }
        return Optional.empty();
    }

    public void submit() {
        Connection connection = pool.getConnection();
        try (PreparedStatement createPersonPS = connection.prepareStatement(INSERT_QUERY)) {
            createPersonPS.setString(1, name);
            createPersonPS.setString(2, surname);
            createPersonPS.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.putConnectionBack(connection);
        }
    }

    public static class Identified {
        private static final String SELECT_QUERY = "SELECT * FROM PERSON WHERE ID = ?";
        private final Long id;
        private final TransactionalBlockingConnectionPool pool;

        public Identified(Long id, TransactionalBlockingConnectionPool pool) {
            this.id = id;
            this.pool = pool;
        }

        public Optional<User> get() {
            Connection connection = pool.getConnection();
            try (PreparedStatement selectPersonPS = connection.prepareStatement(SELECT_QUERY)) {
                selectPersonPS.setLong(1, id);
                ResultSet resultSet = selectPersonPS.executeQuery();
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new User(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                pool.putConnectionBack(connection);
            }
            return Optional.empty();
        }
    }
}
