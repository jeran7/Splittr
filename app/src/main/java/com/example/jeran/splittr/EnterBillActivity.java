package com.example.jeran.splittr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class EnterBillActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText billTitle;
    private EditText billAmount;
    private ListView friendsList;
    private Button addBillButton;

    private SharedPreferences sharedPreferences;
    private String email = "";

    ArrayList<String> selectedFriends;
    private ArrayList<String> checkedEmails;
    private ArrayList<HashMap<String, String>> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_bill);

        billTitle = findViewById(R.id.billTitle);
        billAmount = findViewById(R.id.billAmount);
        friendsList = findViewById(R.id.friendsList);
        addBillButton = findViewById(R.id.addBillButton);

        checkedEmails = new ArrayList<>();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        email = sharedPreferences.getString(getString(R.string.USER_EMAIL), "");

        JSONObject listFriendsObject = new JSONObject();

        try
        {
            listFriendsObject.put("email", email);
            listFriendsObject.put("includeSelf", true);
        }

        catch (JSONException e)
        {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(EnterBillActivity.this, "listFriendsRequest", listFriendsObject.toString(), LinkUtils.LIST_FRIENDS_URL, responseListener, true, "GET").execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.addBillButton:
                if(checkedEmails.size() == 0)
                {
                    ToastUtils.showToast(getApplicationContext(),"Please select atleast 1 friend", false);
                } else if(TextUtils.isEmpty(billTitle.getText().toString())){
                    billTitle.setError("Please enter the description");
                } else if(TextUtils.isEmpty(billAmount.getText().toString())){
                    billAmount.setError("Please enter the amount");
                } else
                {
                    StringBuilder sb = new StringBuilder();
                    for (int i=0; i<checkedEmails.size(); i++){
                        sb.append(checkedEmails.get(i));
                        if((i+1)!=checkedEmails.size()){
                            sb.append(",");
                        }
                    }

                    JSONObject addItemJSONObject = new JSONObject();

                    try
                    {
                        addItemJSONObject.put("owner", email);
                        addItemJSONObject.put("lenter", sb.toString());
                        addItemJSONObject.put("itemName", billTitle.getText().toString().trim());
                        addItemJSONObject.put("itemCost", Double.valueOf(billAmount.getText().toString().trim()));
                    }

                    catch (JSONException e)
                    {
                        Log.d("Splittr", e.toString());
                    }

                    new JsonCallAsync(EnterBillActivity.this, "addItemRequest", addItemJSONObject.toString(), LinkUtils.ADD_ITEM_URL, addItemResponseListener, true, "GET").execute();
                }

                break;
        }
    }

    ResponseListener responseListener = new ResponseListener() {

        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null)
            {
                String response = responseBin.getResponse();

                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if(result.equals("success"))
                    {
                        JSONArray friends = jsonObject.getJSONArray("friends");

                        if(friends.length() == 0)
                        {
                            ToastUtils.showToast(getApplicationContext(), "No friends, cannot split items", false);
                            return;
                        }

                        else
                        {
                            arrayList = new ArrayList<>();

                            for(int i = 0; i < friends.length(); i++)
                            {
                                HashMap<String,String> hashMap = new HashMap<>();

                                String name = friends.getJSONObject(i).getString("first") + " " + friends.getJSONObject(i).getString("last");
                                String email = friends.getJSONObject(i).getString("email");

                                hashMap.put("friendName", name);
                                hashMap.put("email", email);
                                arrayList.add(hashMap);
                            }

                            selectedFriends = new ArrayList<>(friends.length());

                            String[] from = {"friendName"};
                            int[] to = {R.id.friendName};

                            SimpleAdapter adapter = new SimpleAdapter(EnterBillActivity.this, arrayList, R.layout.friends_list_row_item, from, to);
                            friendsList.setAdapter(adapter);
                            friendsList.setOnItemClickListener(EnterBillActivity.this);
                            addBillButton.setOnClickListener(EnterBillActivity.this);
                        }
                    }

                    else if (result.equals("failed"))
                    {
                        ToastUtils.showToast(getApplicationContext(), "Error retrieving friends list", false);
                    }
                }

                catch(JSONException error)
                {
                    Toast.makeText(EnterBillActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String email = arrayList.get(position).get("email");
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkboxFriendsList);
        checkBox.setChecked(!checkBox.isChecked());

        if(checkBox.isChecked()){
            if(!checkedEmails.contains(email)){
                checkedEmails.add(email);
            }
        }else{
            if(checkedEmails.contains(email)){
                checkedEmails.remove(email);
            }
        }
    }

    private ResponseListener addItemResponseListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null)
            {
                String response = responseBin.getResponse();

                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if(result.equals("success"))
                    {
                        ToastUtils.showToast(getApplicationContext(),"'"+billTitle.getText().toString().trim()+"' splitted successfully", true);
                        startActivity(new Intent(EnterBillActivity.this, LandingActivity.class));
                    }
                    else if (result.equals("failed"))
                    {
                        ToastUtils.showToast(getApplicationContext(), "Error splitting the item", false);
                    }
                }

                catch(JSONException error)
                {
                    ToastUtils.showToast(getApplicationContext(), "Error occured", false);
                }
            }
        }
    };

}
