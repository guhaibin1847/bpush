package com.github.guhaibin.api.push;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class User {

    private UserType userType;
    private String username;

    public User(){}

    public User(UserType userType, String username) {
        this.userType = userType;
        this.username = username;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "userType=" + userType +
                ", username='" + username + '\'' +
                '}';
    }
}
