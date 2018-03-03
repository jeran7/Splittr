package com.example.jeran.splittr;

/**
 * Created by Diksha,Dhruv,Jeran on 3/3/2018.
 */

public class ListViewFriendsData {
    private String friendsName;
    private double amount;

    public ListViewFriendsData(String friendsName, double amount) {
        this.friendsName = friendsName;
        this.amount = amount;
    }

    public String getFriendsName() {
        return friendsName;
    }

    public Double getAmount() {
        return amount;
    }
}
