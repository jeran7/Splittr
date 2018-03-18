package com.example.jeran.splittr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchActivity extends Activity {

    private static boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        animateText();
        final Button register = findViewById(R.id.buttonRegister);
        final Button login = findViewById(R.id.buttonLogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void animateText() {
        TextView splashScreenText = (TextView) findViewById(R.id.splittrTitle);
        String str = "$ p l i t t r";
        String tempStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        SpannableString spannableString = new SpannableString(tempStr);
        spannableString.setSpan(new RelativeSizeSpan(2f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        splashScreenText.setText(spannableString);
    }

    public void onBackPressed() {

        if (LaunchActivity.doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }else{
            LaunchActivity.doubleBackToExitPressedOnce = true;

            Toast.makeText(LaunchActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    LaunchActivity.doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
    }
}
