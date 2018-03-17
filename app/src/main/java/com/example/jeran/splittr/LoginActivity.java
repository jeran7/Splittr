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
import com.example.jeran.splittr.helper.UtilityMethods;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button buttonLogin;
    private EditText email;
    private EditText password;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        password = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.editTextEmail);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogin) {
            userLogin();
        }

        if(view == textViewSignUp) {
            finish();
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }

    private void userLogin() {

        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            this.email.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            this.password.setError("Please enter password");
            return;
        }

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        JSONObject loginData = new JSONObject();

        try
        {
            loginData.put("email", email);
            loginData.put("password", password);
        }

        catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(LoginActivity.this, "loginRequest", loginData.toString(), LinkUtils.LOGIN_URL, responseListener, true, "GET").execute();
    }

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {

            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {

                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.USER_LOGIN), true);

                        String email = jsonObject.getString("email");
                        String name = jsonObject.getString("name");
                        String token = sharedPref.getString(getString(R.string.GCM_TOKEN), null);

                        UtilityMethods.sendRegistrationToServer(LoginActivity.this, email, token);
                        editor.putString(getString(R.string.USER_NAME), name);
                        editor.putString(getString(R.string.USER_EMAIL), email);
                        editor.commit();

                        finish();
                        startActivity(new Intent(LoginActivity.this, LandingActivity.class));
                    }

                    else if (result.equals("failed")) {
                        progressDialog.cancel();
                        ToastUtils.showToast(getApplicationContext(), "Incorrect credentials, try again", false);
                    }

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
}
