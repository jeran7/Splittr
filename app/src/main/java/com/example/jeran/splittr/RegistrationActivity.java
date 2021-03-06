package com.example.jeran.splittr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.example.jeran.splittr.helper.UtilityMethods;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends Activity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText email;
    private EditText password;

    private EditText firstName;
    private EditText lastName;
    private TextView textViewSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        animateText();
        buttonRegister = findViewById(R.id.buttonRegister);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        firstName = findViewById(R.id.editTextFname);
        lastName = findViewById(R.id.editTextLname);

        textViewSignup = findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    private void animateText() {
        TextView splashScreenText = (TextView) findViewById(R.id.splittrTitle);
        String str = "$ p l i t t r";
        String tempStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        SpannableString spannableString = new SpannableString(tempStr);
        spannableString.setSpan(new RelativeSizeSpan(2f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        splashScreenText.setText(spannableString);
    }

    private void registerUser() {
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();

        if (TextUtils.isEmpty(fName)) {
            firstName.setError("Please enter first name");
            return;
        }

        if (TextUtils.isEmpty(lName)) {
            lastName.setError("Please enter last name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Please enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
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

        if (InternetUtils.hasConnection(RegistrationActivity.this)) {
            new JsonCallAsync(RegistrationActivity.this, "registrationRequest", registrationData.toString(), LinkUtils.REGISTRATION_URL, responseListener, true, "GET").execute();
        } else {
            ToastUtils.showToast(RegistrationActivity.this, "Unable to connect. Please check your Internet connection.", false);
        }
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

                        String token = sharedPref.getString(getString(R.string.GCM_TOKEN), null);
                        UtilityMethods.sendRegistrationToServer(RegistrationActivity.this, email, token);

                        editor.putString(getString(R.string.USER_NAME), name);
                        editor.putString(getString(R.string.USER_EMAIL), email);
                        editor.commit();

                        ToastUtils.showToast(getApplicationContext(), "Registered successfully", true);
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, LandingActivity.class));
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getApplicationContext(), "Registration failed", false);
                    } else if (result.equals("taken")) {
                        ToastUtils.showToast(getApplicationContext(), "A user with this email already exists", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegistrationActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            registerUser();
        }

        if (view == textViewSignup) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
