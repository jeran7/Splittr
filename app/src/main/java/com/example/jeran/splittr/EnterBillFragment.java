package com.example.jeran.splittr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.example.jeran.splittr.helper.UsersDataModel;
import com.example.jeran.splittr.helper.UsersListViewAdapter;
import com.example.jeran.splittr.helper.UtilityMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EnterBillFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText billTitle;
    private EditText billAmount;
    private ListView friendsListView;
    private Button addBillButton;
    private ArrayList<String> checkedEmails;
    private View view;
    private ArrayList<UsersDataModel> friendsDataModels;
    private UsersListViewAdapter adapter;
    private String itemName = "", itemCost = "";
    private HashMap<String, Double> capturedItems;


    public EnterBillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterBillFragment newInstance() {
        EnterBillFragment fragment = new EnterBillFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enter_bill, container, false);

        Bundle args = this.getArguments();
        if (args != null) {
            itemName = args.getString("itemName");
            itemCost = args.getString("itemCost");
            capturedItems = (HashMap<String, Double>) args.getSerializable("capturedItems");
        }
        findViewsById();
        setUpListView();
        loadFriends();

        return view;
    }

    private void setUpListView() {
        billTitle.setText(itemName);
        billAmount.setText(itemCost);
        checkedEmails = new ArrayList<>();
        friendsDataModels = new ArrayList<>();
        adapter = new UsersListViewAdapter(friendsDataModels, getActivity(), 2);
        friendsListView.setAdapter(adapter);
        friendsListView.setOnItemClickListener(EnterBillFragment.this);
        addBillButton.setOnClickListener(EnterBillFragment.this);
    }

    private void loadFriends() {
        JSONObject listFriendsObject = new JSONObject();

        try {
            listFriendsObject.put("email", LandingActivity.email);
            listFriendsObject.put("includeSelf", true);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        if (InternetUtils.hasConnection(getActivity())) {
            new JsonCallAsync(getActivity(), "listFriendsRequest", listFriendsObject.toString(), LinkUtils.LIST_FRIENDS_URL, responseListener, true, "GET").execute();
        } else {
            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
        }
    }

    private void findViewsById() {
        billTitle = view.findViewById(R.id.billTitle);
        billAmount = view.findViewById(R.id.billAmount);
        friendsListView = view.findViewById(R.id.friendsList);
        addBillButton = view.findViewById(R.id.addBillButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBillButton:
                if (checkedEmails.size() == 0) {
                    ToastUtils.showToast(getActivity(), "Please select atleast 1 friend", false);
                } else if (TextUtils.isEmpty(billTitle.getText().toString())) {
                    billTitle.setError("Please enter the description");
                } else if (TextUtils.isEmpty(billAmount.getText().toString())) {
                    billAmount.setError("Please enter the amount");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < checkedEmails.size(); i++) {
                        sb.append(checkedEmails.get(i));
                        if ((i + 1) != checkedEmails.size()) {
                            sb.append(",");
                        }
                    }

                    JSONObject addItemJSONObject = new JSONObject();
                    try {
                        addItemJSONObject.put("owner", LandingActivity.email);
                        addItemJSONObject.put("lenter", sb.toString());
                        addItemJSONObject.put("itemName", billTitle.getText().toString().trim());
                        addItemJSONObject.put("itemCost", Double.valueOf(billAmount.getText().toString().trim()));
                    } catch (JSONException e) {
                        Log.d("Splittr", e.toString());
                    }

                    if (InternetUtils.hasConnection(getActivity())) {
                        new JsonCallAsync(getActivity(), "addItemRequest", addItemJSONObject.toString(), LinkUtils.ADD_ITEM_URL, addItemResponseListener, true, "GET").execute();
                    } else {
                        ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
                    }
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String email = friendsDataModels.get(position).getEmail();
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setChecked(!checkBox.isChecked());

        if (checkBox.isChecked()) {
            if (!checkedEmails.contains(email)) {
                checkedEmails.add(email);
            }
        } else {
            if (checkedEmails.contains(email)) {
                checkedEmails.remove(email);
            }
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
                        friendsDataModels.clear();

                        JSONArray friends = jsonObject.getJSONArray("friends");

                        if (friends.length() == 0) {
                            ToastUtils.showToast(getActivity(), "No friends, cannot split items", false);
                            return;
                        } else {
                            for (int i = 0; i < friends.length(); i++) {
                                String name = friends.getJSONObject(i).getString("first") + " " + friends.getJSONObject(i).getString("last");
                                String email = friends.getJSONObject(i).getString("email");

                                friendsDataModels.add(new UsersDataModel(LinkUtils.PROFILE_PIC_PATH + email + ".png", name, email));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Error retrieving friends list", false);
                    }
                } catch (JSONException error) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private ResponseListener addItemResponseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        ToastUtils.showToast(getActivity(), "'" + billTitle.getText().toString().trim() + "' splitted successfully", true);

                        if (capturedItems != null) {
                            capturedItems.remove(itemName);
                            if (!capturedItems.isEmpty()) {
                                CapturedItemsFragment capturedItemsFragment = CapturedItemsFragment.newInstance();
                                Bundle args = new Bundle();
                                args.putSerializable("capturedItems", capturedItems);
                                capturedItemsFragment.setArguments(args);

                                UtilityMethods.insertFragment(capturedItemsFragment, R.id.content_frame, getActivity());
                            } else {
                                ToastUtils.showToast(getActivity(), "Succesfully added all items from the bill", true);
                                startActivity(new Intent(getActivity(), LandingActivity.class));
                            }
                        } else {
                            startActivity(new Intent(getActivity(), LandingActivity.class));
                        }

                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Error splitting the item", false);
                    }
                } catch (JSONException error) {
                    ToastUtils.showToast(getActivity(), "Error occured", false);
                }
            }
        }
    };

}
