package com.sarita.insideout;

import android.util.Log;

import java.util.ArrayList;

public class User {

    String name, email, password, image, phone;
    static User currentUser;
    public User(String name, String email, String password, String image, String phone) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.phone = phone;
    }

    public User() {

    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static void createUser(String name, String email, String password, String image, String phone){
        currentUser = new User(name, email,password, image, phone);
    }



    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}