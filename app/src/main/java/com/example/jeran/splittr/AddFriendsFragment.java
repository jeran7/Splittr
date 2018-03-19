package com.example.jeran.splittr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.FriendListViewDataModel;
import com.example.jeran.splittr.helper.FriendsListViewAdapter;
import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class AddFriendsFragment extends Fragment {

    private EditText searchQuery;
    private ListView friendsListView;
    private View view;
    private FriendsListViewAdapter adapter;
    private ArrayList<FriendListViewDataModel> friendsDataModels;
    private ArrayList<FriendListViewDataModel> filteredFriendsDataModel;

    public AddFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
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
        setUpQueryWatcher();
        setUpListView();
        loadUsers();

        return view;
    }

    private void setUpQueryWatcher() {
        searchQuery.addTextChangedListener(queryWatcher);
    }

    private void setUpListView() {
        friendsDataModels = new ArrayList<>();
        filteredFriendsDataModel = new ArrayList<>();
        adapter = new FriendsListViewAdapter(filteredFriendsDataModel, getActivity());
        friendsListView.setAdapter(adapter);
    }

    private void loadUsers() {
        JSONObject listUsersObject = new JSONObject();
        try {
            listUsersObject.put("email", LandingActivity.email);
            listUsersObject.put("includeSelf", true);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        if (InternetUtils.hasConnection(getActivity())) {
            new JsonCallAsync(getActivity(), "listUsersRequest", listUsersObject.toString(), LinkUtils.LIST_USERS_URL, responseListener, true, "GET").execute();
        } else {
            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
        }
    }

    private void findViewsById() {
        searchQuery = (EditText) view.findViewById(R.id.searchQuery);
        friendsListView = (ListView) view.findViewById(R.id.searchResultList);
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
            filteredFriendsDataModel.clear();
            if (query.length() == 0) {
                for (FriendListViewDataModel friend : friendsDataModels) {
                    filteredFriendsDataModel.add(friend);
                }
            } else {
                for (FriendListViewDataModel friend : friendsDataModels) {
                    if (friend.getname().toLowerCase(Locale.getDefault()).contains(query) || friend.getFriendEmail().toLowerCase(Locale.getDefault()).contains(query)) {
                        filteredFriendsDataModel.add(friend);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {

            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        friendsDataModels.clear();

                        JSONArray users = jsonObject.getJSONArray("users");

                        for (int i = 0; i < users.length(); i++) {
                            String name = users.getJSONObject(i).getString("first") + " " + users.getJSONObject(i).getString("last");
                            String email = users.getJSONObject(i).getString("email");

                            friendsDataModels.add(new FriendListViewDataModel(LinkUtils.PROFILE_PIC_PATH + email + ".png", name, email));
                        }

                        for (FriendListViewDataModel friend : friendsDataModels) {
                            filteredFriendsDataModel.add(friend);
                        }

                        adapter.notifyDataSetChanged();
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Could not fetch list of available friends", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
