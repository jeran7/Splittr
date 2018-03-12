package com.example.jeran.splittr;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diksha Jaiswal on 3/11/2018.
 */

public class EnterBillActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText billTitle;
    private EditText billAmount;
    private ListView friendsList;
    private Button addBillButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_bill);

        billTitle = (EditText) findViewById(R.id.billTitle);
        billAmount = (EditText) findViewById(R.id.billAmount);
        friendsList = (ListView) findViewById(R.id.friendsList);
        addBillButton = (Button) findViewById(R.id.addBillButton);

        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
        for (int i=0; i<50; i++)
        {
            HashMap<String,String> hashMap=new HashMap<>();
            if(i%2==0){
                hashMap.put("friendName", "Abhi Jadav");
            }else{
                hashMap.put("friendName", "Diksha Jaiswal");
            }
            arrayList.add(hashMap);
        }
        String[] from = {"friendName"};
        int[] to = {R.id.friendName};

        SimpleAdapter adapter = new SimpleAdapter(this, arrayList, R.layout.friends_list_row_item, from, to);
        friendsList.setAdapter(adapter);
        addBillButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addBillButton:
                break;
        }
    }
}
