package com.example.db.entity;

import java.util.Objects;

public class User {
    public static final String TABLE_NAME = "user";

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "passeword";

    private String username;
    private String password;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_USERNAME + " TEXT PRIMARY KEY,"
                    + COLUMN_USERNAME + " TEXT,"
                    + ")";

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
