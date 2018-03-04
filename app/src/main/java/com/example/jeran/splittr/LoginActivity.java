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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogin;
    private EditText editTextEmail;
    private  EditText editTextPassword;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {

            // go to landing page

        }

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            userLogin();

        }
        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this,RegisterActivity.class));

        }
    }

    private void userLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Please enter email");
            return;

        }

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter password");
            return;

        }

        progressDialog.setMessage("Logging in please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    finish();
                    // if sucessfull more to landing page.
                    startActivity(new Intent(LoginActivity.this,LandingActivity.class));
                    progressDialog.dismiss();
                    //  Toast.makeText(this,"Login sucessfull",Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });
    }
}
