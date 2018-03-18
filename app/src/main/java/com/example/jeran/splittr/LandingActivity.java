package com.example.jeran.splittr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.example.jeran.splittr.helper.UtilityMethods;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static boolean doubleBackToExitPressedOnce = false;

    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionMenu fabMenu;
    private TextView navName, navEmail;
    private FloatingActionButton fab1, fab2;
    protected static String email="", name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        findViewsById();
        loadUserDetails();
        setUpToolBar();
        setUpFabButton();
        setUpNavigationDrawer();
        setUpHomeFragment();
    }

    private void loadUserDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        LandingActivity.email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");
        LandingActivity.name = sharedPreferences.getString(getString(R.string.USER_NAME), "");
    }

    private void findViewsById() {
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        navEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        fab1 = ((FloatingActionButton) findViewById(R.id.fab1));
        fab2 = ((FloatingActionButton) findViewById(R.id.fab2));
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
                UtilityMethods.insertFragment(AddFriendsFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.close(true);
                fabMenu.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

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
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
    }

    private void setUpFabButton() {
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
    }

    private void setUpNavigationDrawer() {
        navName.setText(LandingActivity.name);
        navEmail.setText(LandingActivity.email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpHomeFragment() {
        UtilityMethods.insertFragment(LandingFragment.newInstance(), R.id.content_frame, LandingActivity.this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab1:
                UtilityMethods.insertFragment(EnterBillFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.close(true);
                fabMenu.setVisibility(View.GONE);
                break;
            case R.id.fab2:
                startActivity(new Intent(getApplicationContext(), PhotoCaptureActivity.class));
                fabMenu.close(true);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                UtilityMethods.insertFragment(LandingFragment.newInstance(), R.id.content_frame, LandingActivity.this);
                fabMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jaiswaldiksha07@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Splittr App");
                startActivity(Intent.createChooser(intent, "Choose an App to send the email"));
                break;
            case R.id.nav_contact_us:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("message/rfc822");
                intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{"jaiswaldiksha07@gmail.com"});
                intent1.putExtra(Intent.EXTRA_SUBJECT, "Splittr App");
                startActivity(Intent.createChooser(intent1, "Choose an App to send the email"));
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Splittr App");
                startActivity(shareIntent);
                break;
            case R.id.nav_logout:
                showLogOutDialog();
                break;
        }

        // set item as selected to persist highlight
        item.setChecked(true);
        // close drawer when item is tapped
        drawer.closeDrawers();

        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here

        return true;
    }

    private void showLogOutDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject deleteGcmTokenData = new JSONObject();

                        try {
                            deleteGcmTokenData.put("email", LandingActivity.email);
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
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
                                            editor.putBoolean(getString(R.string.USER_LOGIN), false);
                                            editor.putString(getString(R.string.USER_NAME), null);
                                            editor.putString(getString(R.string.USER_EMAIL), null);
                                            editor.commit();

                                            startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                                            finish();
                                        } else if (result.equals("failed")) {
                                            ToastUtils.showToast(getApplicationContext(), "Unable to log you out from server. Please try again.", false);
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(LandingActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        };

                        new JsonCallAsync(LandingActivity.this, "deleteGcmTokenRequest", deleteGcmTokenData.toString(), LinkUtils.DELETE_TOKEN_URL, responseListener, true, "GET").execute();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
