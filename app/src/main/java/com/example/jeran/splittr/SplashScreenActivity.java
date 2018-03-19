package com.example.jeran.splittr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Window;

import com.example.jeran.splittr.helper.TypeWriter;

public class SplashScreenActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    private TypeWriter splashScreenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        splashScreenText = (TypeWriter) findViewById(R.id.splashScreenText);
        splashScreenText.setCharacterDelay(150);
        String str = "$ p l i t t r";
        String tempStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        SpannableString spannableString = new SpannableString(tempStr);
        spannableString.setSpan(new RelativeSizeSpan(2f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        splashScreenText.animateText(spannableString);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isUserLoggedIn()) {
                    startActivity(new Intent(SplashScreenActivity.this, LaunchActivity.class));
                }else{
                    startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                }

                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(getString(R.string.USER_LOGIN), false);
    }
}
