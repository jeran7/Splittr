package com.example.jeran.splittr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


/* This is the activity where the users can see his expense. */

public class LandingActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    ArrayList<SummaryListViewDataModel> dataModels;
    ListView listView;
    private static SummaryListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        if (!isUserLoggedIn()) {
            startActivity(new Intent(LandingActivity.this, LaunchActivity.class));
            return;
        }

        listView = findViewById(R.id.summaryListView);

        dataModels = new ArrayList<>();
        dataModels.add(new SummaryListViewDataModel("test", 35));
        dataModels.add(new SummaryListViewDataModel("test2", -35.56));
        dataModels.add(new SummaryListViewDataModel("test3", 0));

        adapter = new SummaryListViewAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);

        FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddBillActivity.class);
                startActivity(intent);
            }
        });

//        db = FirebaseDatabase.getInstance().getReference("expenses");
        /*
        db.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                HashMap<String, Object> map =  (HashMap<String, Object>) dataSnapshot.getValue();

                JSONObject user;
                double owes = 0;
                double lent = 0;
                double total = 0;

                for(String key: map.keySet())
                {
                    try
                    {
                        user = new JSONObject(map.get(key).toString());
                        if(user.getString("ownerEmail").equalsIgnoreCase(email))
                        {
                            owes = user.getDouble("owes");
                            lent = user.getDouble("lent");
                            total = lent - owes;

                            if(total >= 0)
                            {
                                summary.setText("Lent $" + total);
                            }

                            else
                            {
                                summary.setText("Owes $" + Math.abs(total));
                            }
                        }
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        }); */
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
}
