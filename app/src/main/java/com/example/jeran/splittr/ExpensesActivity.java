package com.example.jeran.splittr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Diksha Jaiswal
 * Jeran Jeyachandran
 * Dhruv Mevada
 */
public class ExpensesActivity extends AppCompatActivity
{
    DatabaseReference expensesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        expensesDB = FirebaseDatabase.getInstance().getReference("expenses");

        String id = expensesDB.push().getKey();
        Expense expenses = new Expense(id, "test1@g.com", 100, 75);
        expensesDB.child(id).setValue(expenses);
    }
}