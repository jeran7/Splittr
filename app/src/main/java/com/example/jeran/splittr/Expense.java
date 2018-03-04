package com.example.jeran.splittr;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
 */

public class Expense
{
    private String id;
    private String ownerEmail;
    private double owes;
    private double lent;

    public Expense(String id, String ownerEmail, double owes, double lent)
    {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.owes = owes;
        this.lent = lent;
    }

    public Expense()
    {

    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public double getOwes() {
        return owes;
    }

    public double getLent() {
        return lent;
    }

}
