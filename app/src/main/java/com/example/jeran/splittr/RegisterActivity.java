package com.example.jeran.splittr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity implements View.OnClickListener {

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
        final String password = editTextPassword.getText().toString().trim();
        final String fName= firstName.getText().toString().trim();
        final String lName=lastName.getText().toString().trim();

        if(TextUtils.isEmpty(fName)) {
            firstName.setError("Please enter first name");
            return;
        }

        if(TextUtils.isEmpty(lName)) {
            lastName.setError("Please enter last name");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            return;
        }

        JSONObject registrationData = new JSONObject();
        try {
            registrationData.put("firstName", fName);
            registrationData.put("lastName", lName);
            registrationData.put("email", email);
            registrationData.put("password", password);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }
        new JsonCallAsync(RegisterActivity.this, "registrationRequest", registrationData.toString(), LinkUtils.REGISTRATION_URL, responseListener, true, "GET").execute();
    }

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {

            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String result=jsonObject.getString("result");

                    if (result.equals("success")) {
                        Toast.makeText(RegisterActivity.this, "Registered successfully.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this,LandingActivity.class));
                    } else if (result.equals("failed")) {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_LONG).show();
                    } else if (result.equals("taken")) {
                        Toast.makeText(RegisterActivity.this, "User ID already taken. Try agin with different user ID.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

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
