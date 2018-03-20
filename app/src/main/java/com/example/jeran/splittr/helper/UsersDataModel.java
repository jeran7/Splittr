package com.example.jeran.splittr.helper;

/**
 * Created by Abhi on 19-Mar-18.
 */

public class UsersDataModel {
    private String pic;
    private String name;
    private String email;

    public UsersDataModel(String pic, String name, String email) {
        this.pic = pic;
        this.name = name;
        this.email = email;
    }

    public String getPic() {
        return pic;
    }

    public String getname() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
