package com.example.jeran.splittr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {
    private static boolean doubleBackToExitPressedOnce = false;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    ArrayList<SummaryListViewDataModel> dataModels;
    ListView listView;
    private static SummaryListViewAdapter adapter;

    private SharedPreferences sharedPreferences;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        if (!isUserLoggedIn()) {
            startActivity(new Intent(LandingActivity.this, LaunchActivity.class));
            return;
        }

        FloatingActionButton fb = findViewById(R.id.fab);
        listView = findViewById(R.id.summaryListView);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");
        fb.setOnClickListener(this);

        JSONObject getSummaryObject = new JSONObject();

        try
        {
            getSummaryObject.put("email", email);
        }

        catch (JSONException e)
        {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(LandingActivity.this, "getSummaryRequest", getSummaryObject.toString(), LinkUtils.GET_SUMMARY_URL, responseListener, true, "GET").execute();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(getString(R.string.USER_LOGIN), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addFriend:
                startActivity(new Intent(LandingActivity.this, AddFriendsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {

        if (LandingActivity.doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }else{
            LandingActivity.doubleBackToExitPressedOnce = true;

            Toast.makeText(LandingActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    LandingActivity.doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
//        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.fab:
                Intent intent = new Intent(getApplicationContext(), AddBillActivity.class);
                startActivity(intent);
                break;
        }
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

                    if(result.equals("success"))
                    {
                        JSONArray users = jsonObject.getJSONArray("users");
                        dataModels = new ArrayList<>();

                        for(int i = 0; i < users.length(); i++)
                        {
                            String name = users.getJSONObject(i).getString("name");
                            double amount = users.getJSONObject(i).getDouble("amount");

                            dataModels.add(new SummaryListViewDataModel(name, amount));
                        }

                        adapter = new SummaryListViewAdapter(dataModels, getApplicationContext());
                        listView.setAdapter(adapter);
                    }

                    else if(result.equals("failed"))
                    {
                        ToastUtils.showToast(getApplicationContext(), "Couldn't retrieve summary",false);
                    }
                }

                catch(JSONException e)
                {
                    Toast.makeText(LandingActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
