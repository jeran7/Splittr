package com.example.jeran.splittr.helper;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
 */
public class SummaryListViewDataModel
{
    private final String email;
    private String name;
    private double amount;

    public SummaryListViewDataModel(String name, double amount, String email)
    {
        this.name = name;
        this.amount = amount;
        this.email = email;
    }

    public String getname() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }
}
