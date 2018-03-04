package com.example.jeran.splittr;

import java.util.ArrayList;

/**
 * Created by Diksha Jaiswal on 3/4/2018.
 */

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private ArrayList<String> friends;

    public User(String email, String firstName, String lastName, ArrayList<String> friends) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = friends;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }
    public User()
    {

    }
}
