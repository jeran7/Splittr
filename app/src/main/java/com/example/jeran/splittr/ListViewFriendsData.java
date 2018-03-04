package com.example.jeran.splittr;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
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
