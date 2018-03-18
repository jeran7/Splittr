package com.example.jeran.splittr;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AddFriendsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private EditText searchQuery;
    private ListView searchQueryList;
    private ArrayList<String> filteredFriendsList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private HashMap<String, String> friends = new HashMap<>();
    private SharedPreferences sharedPreferences;
    private View view;

    public AddFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LandingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendsFragment newInstance() {
        AddFriendsFragment fragment = new AddFriendsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_friends, container, false);
        findViewsById();
        loadUsers();

        return view;
    }

    private void loadUsers() {
        JSONObject listUsersObject = new JSONObject();
        try {
            listUsersObject.put("email", LandingActivity.email);
            listUsersObject.put("includeSelf", true);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(getActivity(), "listUsersRequest", listUsersObject.toString(), LinkUtils.LIST_USERS_URL, responseListener, true, "GET").execute();
    }

    private void findViewsById() {
        searchQuery = (EditText) view.findViewById(R.id.searchQuery);
        searchQueryList = (ListView) view.findViewById(R.id.searchResultList);
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

                        JSONObject addFriendData = new JSONObject();

                        try {
                            addFriendData.put("email", LandingActivity.email);
                            addFriendData.put("friend", friends.get(selectedFriend));
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        new JsonCallAsync(getActivity(), "addFriendRequest", addFriendData.toString(), LinkUtils.ADD_FRIENDS_URL, addFriendListener, true, "GET").execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Add Friend")
                .setMessage(Html.fromHtml("<b>" + selectedFriend + "</b><br>" + friends.get(selectedFriend)))
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

                        for (int i = 0; i < users.length(); i++) {
                            String name = users.getJSONObject(i).getString("first") + " " + users.getJSONObject(i).getString("last");
                            String email = users.getJSONObject(i).getString("email");

                            friends.put(name, email);
                        }

                        for (String friend : friends.keySet()) {
                            filteredFriendsList.add(friend);
                        }

                        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filteredFriendsList);
                        searchQueryList.setAdapter(arrayAdapter);
                        searchQueryList.setOnItemClickListener(AddFriendsFragment.this);

                        searchQuery.addTextChangedListener(queryWatcher);
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Could not fetch list of available friends", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    ResponseListener addFriendListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        ToastUtils.showToast(getActivity(), "Added friend successfully", true);
                    } else if (result.equals("isFriendAlready")) {
                        ToastUtils.showToast(getActivity(), "Already a friend", false);
                    }
                } catch (JSONException error) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
