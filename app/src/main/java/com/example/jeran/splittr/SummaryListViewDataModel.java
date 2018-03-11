package com.example.jeran.splittr;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
 */
public class SummaryListViewDataModel
{
    private String friendName;
    private double amount;

    public SummaryListViewDataModel(String friendName, double amount)
    {
        this.friendName = friendName;
        this.amount = amount;
    }

    public String getFriendName() {
        return friendName;
    }

    public Double getAmount() {
        return amount;
    }
}
