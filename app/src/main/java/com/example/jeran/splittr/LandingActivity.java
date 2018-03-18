package com.example.jeran.splittr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static boolean doubleBackToExitPressedOnce = false;

    ArrayList<SummaryListViewDataModel> dataModels;
    ListView listView;
    private SummaryListViewAdapter adapter;

    private SharedPreferences sharedPreferences;
    private String email = "";
    private TextView currentUserName;
    private TextView currentUserBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        FloatingActionButton fb = findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.summaryListView);
        currentUserName = (TextView) findViewById(R.id.currentUserName);
        currentUserBalance = (TextView) findViewById(R.id.currentUserBalance);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");
        fb.setOnClickListener(this);

        dataModels = new ArrayList<>();
        adapter = new SummaryListViewAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
        loadSummary();
    }

    private void loadSummary() {

        currentUserName.setText(getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString("name", null));
        JSONObject getSummaryObject = new JSONObject();

        try {
            getSummaryObject.put("email", email);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(LandingActivity.this, "getSummaryRequest", getSummaryObject.toString(), LinkUtils.GET_SUMMARY_URL, responseListener, true, "GET").execute();
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
        } else {
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
        switch (view.getId()) {
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

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        double netBalance = 0;
                        dataModels.clear();
                        JSONArray users = jsonObject.getJSONArray("users");

                        for (int i = 0; i < users.length(); i++) {
                            String name = users.getJSONObject(i).getString("name");
                            double amount = users.getJSONObject(i).getDouble("amount");
                            String email = users.getJSONObject(i).getString("email");
                            netBalance += amount;

                            dataModels.add(new SummaryListViewDataModel(name, amount, email));
                        }

                        adapter.notifyDataSetChanged();
                        listView.setOnItemClickListener(LandingActivity.this);

                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        if (netBalance < 0) {
                            currentUserBalance.setTextColor(getResources().getColor(R.color.owes));
                            currentUserBalance.setText("You owe\n$" + df.format(Math.abs(netBalance)));
                        } else if (netBalance > 0) {
                            currentUserBalance.setText("You lent\n$" + df.format(Math.abs(netBalance)));
                        } else {
                            currentUserBalance.setText("You're\nsettled up");
                        }
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getApplicationContext(), "Couldn't retrieve summary", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(LandingActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void showDialogForSelectedFriend(final String selectedFriend) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject settleUpData = new JSONObject();

                        try {
                            settleUpData.put("email", email);
                            settleUpData.put("friend", selectedFriend);
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        new JsonCallAsync(LandingActivity.this, "settleUpRequest", settleUpData.toString(), LinkUtils.SETTLE_UP_URL, settleUpListener, true, "GET").execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Settle Up")
                .setMessage(Html.fromHtml("Are you sure you want to settle up with <b>" + selectedFriend + "</b>?"))
                .setPositiveButton("Settle Up", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private ResponseListener settleUpListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        ToastUtils.showToast(LandingActivity.this, "Successfully settled up", true);
                        loadSummary();
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(LandingActivity.this, "Failed to settle up", false);
                    }

                } catch (JSONException e) {
                    Toast.makeText(LandingActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogForSelectedFriend(adapter.getItem(position).getFriendName());
    }
}
