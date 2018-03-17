package com.example.jeran.splittr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.google.api.client.json.JsonString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class AddFriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText searchQuery;
    private ListView searchQueryList;
    private ArrayList<String> filteredFriendsList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private HashMap<String, String> friends = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        searchQuery = (EditText) findViewById(R.id.searchQuery);
        searchQueryList = (ListView) findViewById(R.id.searchResultList);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");

        JSONObject registrationData = new JSONObject();

        try
        {
            registrationData.put("email", email);
            registrationData.put("includeSelf", true);
        }

        catch (JSONException e)
        {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(AddFriendsActivity.this, "listUsersRequest", registrationData.toString(), LinkUtils.LIST_USERS_URL, responseListener, true, "GET").execute();
    }

    private TextWatcher queryWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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

                        JSONObject addFriendData = new JSONObject();

                        try
                        {
                            addFriendData.put("email", email);
                            addFriendData.put("friend", friends.get(selectedFriend));
                        }

                        catch (JSONException e)
                        {
                            Log.d("Splittr", e.toString());
                        }

                        new JsonCallAsync(AddFriendsActivity.this, "addFriendRequest", addFriendData.toString(), LinkUtils.ADD_FRIENDS_URL, addFriendListener, true, "GET").execute();
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

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {

            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {

                        JSONArray users = jsonObject.getJSONArray("users");

                        for(int i = 0; i < users.length(); i++)
                        {
                            String name = users.getJSONObject(i).getString("first") + " " + users.getJSONObject(i).getString("last");
                            String email = users.getJSONObject(i).getString("email");

                            friends.put(name, email);
                        }

                        for (String friend : friends.keySet()) {
                            filteredFriendsList.add(friend);
                        }

                        arrayAdapter = new ArrayAdapter<String>(AddFriendsActivity.this, android.R.layout.simple_list_item_1, filteredFriendsList);
                        searchQueryList.setAdapter(arrayAdapter);
                        searchQueryList.setOnItemClickListener(AddFriendsActivity.this);

                        searchQuery.addTextChangedListener(queryWatcher);
                    }

                    else if (result.equals("failed")) {
                        ToastUtils.showToast(getApplicationContext(), "Could not fetch list of available friends", false);
                    }
                }

                catch (JSONException e) {
                    Toast.makeText(AddFriendsActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    ResponseListener addFriendListener = new ResponseListener() {
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
                        ToastUtils.showToast(getApplicationContext(), "Added friend successfully", true);
                    }

                    else if (result.equals("isFriendAlready"))
                    {
                        ToastUtils.showToast(getApplicationContext(), "Already a friend", false);
                    }
                }

                catch(JSONException error)
                {
                    Toast.makeText(AddFriendsActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
