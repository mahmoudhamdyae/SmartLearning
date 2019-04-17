package com.project.csed.smartlearning;

public class User {
    private String userName;
    private String email;
    private String password;
    private String type;
    private String userId;

    public User(){}

    public User(String userName, String email, String password, String type, String userId){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.type = type;
        this.userId = userId;
    }

    public User(String userName, String email){
        this.userName = userName;
        this.email = email;
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

    public String getUserId(){return userId;}

    public void setUserId(String userId){ this.userId = userId;}
}
