package com.example.jeran.splittr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private EditText firstName;
    private EditText lastName;
    private TextView textViewSignup;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth =  FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        buttonRegister = findViewById(R.id.buttonRegister);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        firstName=findViewById(R.id.editTextFname);
        lastName=findViewById(R.id.editTextLname);

        textViewSignup = findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String fName= firstName.getText().toString().trim();
        final String lName=lastName.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            return;
        }

        if(TextUtils.isEmpty(fName)) {
            firstName.setError("Please enter first name");
            return;
        }

        if(TextUtils.isEmpty(lName)) {
            lastName.setError("Please enter last name");
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // move to landing page
                    ArrayList<String> friends=new ArrayList<String>();
                    friends.add(email);
                    userDB = FirebaseDatabase.getInstance().getReference("users");
                    String id = userDB.push().getKey();
                    User user = new User(email,fName,lName,friends);
                    userDB.child(id).setValue(user);

                    Toast.makeText(RegisterActivity.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(RegisterActivity.this,"User already exists, please login.",Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view == buttonRegister) {
            registerUser();
        }

        if(view == textViewSignup) {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
