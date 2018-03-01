package com.example.jeran.splittr;

import android.app.ProgressDialog;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{



    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth =  FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        textViewSignup = findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }



    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Registering User.....Please Wait.....");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // move to landing page
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Registered Sucessfully",Toast.LENGTH_SHORT).show();


                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"User Already present. Try Logging in",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    @Override
    public void onClick(View view){

        if(view == buttonRegister){
            registerUser();
        }
        if(view == textViewSignup){
            //Open lgoin page

        }

    }



}
