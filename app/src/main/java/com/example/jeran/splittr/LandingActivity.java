package com.example.jeran.splittr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity {
        EditText samp;
        FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        firebaseAuth =  FirebaseAuth.getInstance();
        String email = firebaseAuth.getCurrentUser().getEmail().toString();
        samp = findViewById(R.id.editTextdisplaySamp);
        samp.setText(email);


    }
}
