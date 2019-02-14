package com.project.csed.smartlearning;

public class User {
    private String userName;
    private String email;
    private String password;
    private String type;

    public User(){}

    public User(String userName, String email, String password, String type){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
