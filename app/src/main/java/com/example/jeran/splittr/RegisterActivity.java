package com.example.jeran.splittr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.jeran.splittr.helper.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText email;
    private EditText password;

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

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        firstName = findViewById(R.id.editTextFname);
        lastName = findViewById(R.id.editTextLname);

        textViewSignup = findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    private void registerUser() {
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();

        if(TextUtils.isEmpty(fName)) {
            firstName.setError("Please enter first name");
            return;
        }

        if(TextUtils.isEmpty(lName)) {
            lastName.setError("Please enter last name");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            this.email.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            this.password.setError("Please enter password");
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
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {

                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.USER_LOGIN), true);


                        String email = jsonObject.getString("email");
                        String name = jsonObject.getString("name");

                        editor.putString(getString(R.string.USER_NAME), name);
                        editor.putString(getString(R.string.USER_EMAIL), email);
                        editor.commit();

                        ToastUtils.showToast(getApplicationContext(), "Registered successfully", true);
                        finish();
                        startActivity(new Intent(RegisterActivity.this,LandingActivity.class));
                    }

                    else if (result.equals("failed")) {
                        ToastUtils.showToast(getApplicationContext(), "Registration failed", false);
                    }

                    else if (result.equals("taken")) {
                        ToastUtils.showToast(getApplicationContext(), "A user with this email already exists", false);
                    }
                }

                catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
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
