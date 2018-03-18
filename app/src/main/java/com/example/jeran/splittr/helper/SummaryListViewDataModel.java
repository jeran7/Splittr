package com.example.jeran.splittr.helper;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
 */
public class SummaryListViewDataModel
{
    private final String email;
    private String friendName;
    private double amount;

    public SummaryListViewDataModel(String friendName, double amount, String email)
    {
        this.friendName = friendName;
        this.amount = amount;
        this.email = email;
    }

    public String getFriendName() {
        return friendName;
    }

    public Double getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }
}
