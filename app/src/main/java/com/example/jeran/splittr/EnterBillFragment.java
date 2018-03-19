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
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.HashMap;

public class EnterBillFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText billTitle;
    private EditText billAmount;
    private ListView friendsList;
    private Button addBillButton;
    ArrayList<String> selectedFriends;
    private ArrayList<String> checkedEmails;
    private ArrayList<HashMap<String, String>> arrayList;
    private View view;


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
        findViewsById();
        checkedEmails = new ArrayList<>();
        loadFriends();

        return view;
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
        friendsList = view.findViewById(R.id.friendsList);
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
        String email = arrayList.get(position).get("email");
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxFriendsList);
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
                        JSONArray friends = jsonObject.getJSONArray("friends");

                        if (friends.length() == 0) {
                            ToastUtils.showToast(getActivity(), "No friends, cannot split items", false);
                            return;
                        } else {
                            arrayList = new ArrayList<>();

                            for (int i = 0; i < friends.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>();

                                String name = friends.getJSONObject(i).getString("first") + " " + friends.getJSONObject(i).getString("last");
                                String email = friends.getJSONObject(i).getString("email");

                                hashMap.put("name", name);
                                hashMap.put("email", email);
                                arrayList.add(hashMap);
                            }

                            selectedFriends = new ArrayList<>(friends.length());

                            String[] from = {"name"};
                            int[] to = {R.id.name};

                            SimpleAdapter adapter = new SimpleAdapter(getActivity(), arrayList, R.layout.select_friends_list_row_item, from, to);
                            friendsList.setAdapter(adapter);
                            friendsList.setOnItemClickListener(EnterBillFragment.this);
                            addBillButton.setOnClickListener(EnterBillFragment.this);
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
                        startActivity(new Intent(getActivity(), LandingActivity.class));
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
