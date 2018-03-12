package com.example.jeran.splittr;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class AddFriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText searchQuery;
    private ListView searchQueryList;
    private ArrayList<String> filteredFriendsList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private HashMap<String, String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        initializeFriends();
        searchQuery = (EditText) findViewById(R.id.searchQuery);
        searchQueryList = (ListView) findViewById(R.id.searchResultList);

        for (String friend : friends.keySet()) {
            filteredFriendsList.add(friend);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filteredFriendsList);
        searchQueryList.setAdapter(arrayAdapter);
        searchQueryList.setOnItemClickListener(this);

        searchQuery.addTextChangedListener(queryWatcher);
    }

    private void initializeFriends() {
        friends = new HashMap<>();
        friends.put("Abhi Jadav", "ajadav@scu.edu");
        friends.put("Diksha Jaiswal", "djaiswal@scu.edu");
        friends.put("Dhruv Mevada", "dmevada@scu.edu");
        friends.put("Jeran J", "jjeran@scu.edu");
        friends.put("Saumya Sinha", "ssinha@scu.edu");
        friends.put("Rasika Telang", "rtelang@scu.edu");
        friends.put("Pooja Ranawade", "pranawade@scu.edu");
        friends.put("Netra Agrawal", "nagrawal@scu.edu");
        friends.put("Heena Tarachandani", "htarachandani@scu.edu");
        friends.put("Tejaswi S", "stejaswi@scu.edu");
        friends.put("Ashwini Raravi", "araravi@scu.edu");
        friends.put("Priyanshi Karangia", "pkarangia@scu.edu");
        friends.put("Apurva Patel", "apatel@scu.edu");
    }

    private TextWatcher queryWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String query = searchQuery.getText().toString().toLowerCase(Locale.getDefault());
            filteredFriendsList.clear();
            if (query.length() == 0) {
                for (String friend : friends.keySet()) {
                    filteredFriendsList.add(friend);
                }
            } else {
                for (String friend : friends.keySet()) {
                    if (friend.toLowerCase(Locale.getDefault()).contains(query)) {
                        filteredFriendsList.add(friend);
                    }
                }
            }
            arrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected item text from ListView
        String selectedFriend = (String) parent.getItemAtPosition(position);
        showDialogForSelectedFriend(selectedFriend);
    }

    private void showDialogForSelectedFriend(final String selectedFriend) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(AddFriendsActivity.this, "Added '"+selectedFriend+"" +
                                "' as friend successfully", Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(Html.fromHtml("<b>"+selectedFriend+"</b><br>"+friends.get(selectedFriend)))
                .setPositiveButton("Add", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }
}
