package com.example.nativelife;

import android.net.Uri;

public class UserRegistrationInfo {

    public String firstNameReg;
    public String lastNameReg;
    public String userNameReg;
    public String emailReg;
    public String passwordReg;

    public UserRegistrationInfo() {

    }

    public UserRegistrationInfo(String firstName, String lastName, String userName, String email, String password) {
        this.firstNameReg = firstName;
        this.lastNameReg = lastName;
        this.userNameReg = userName;
        this.emailReg = email;
        this.passwordReg = password;
        //this.pickedImageUri = pickedImageUri;
    }

    public String getFirstNameReg() {
        return firstNameReg;
    }

    public void setFirstNameReg(String firstNameReg) {
        this.firstNameReg = firstNameReg;
    }

    public String getLastNameReg() {
        return lastNameReg;
    }

    public void setLastNameReg(String lastNameReg) {
        this.lastNameReg = lastNameReg;
    }

    public String getUserNameReg() {
        return userNameReg;
    }

    public void setUserNameReg(String userNameReg) {
        this.userNameReg = userNameReg;
    }
}
