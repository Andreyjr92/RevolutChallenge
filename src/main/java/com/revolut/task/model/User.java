package com.revolut.task.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class User {
    protected Long id;
    protected String name;
    protected String surname;

    public User(Long id) {
        this.id = id;
    }

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public User(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public User(ResultSet resultSet) {
        try {
            this.id = resultSet.getLong("id");
            this.name = resultSet.getString("name");
            this.surname = resultSet.getString("surname");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                name.equals(user.name) &&
                surname.equals(user.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname);
    }
}