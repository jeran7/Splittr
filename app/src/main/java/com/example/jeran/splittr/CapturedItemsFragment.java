package com.example.jeran.splittr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.jeran.splittr.helper.UtilityMethods;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhi on 20-Mar-18.
 */

public class CapturedItemsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private HashMap<String, Double> capturedItemsHashMap;
    private View view;
    private ListView capturedItemListViewListView;
    private ArrayList<HashMap<String, String>> capturedItems;
    private SimpleAdapter adapter;

    public CapturedItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CapturedItemsFragment newInstance() {
        CapturedItemsFragment fragment = new CapturedItemsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_captured_items, container, false);
        capturedItemsHashMap = (HashMap<String, Double>) this.getArguments().getSerializable("capturedItems");
        capturedItemListViewListView = view.findViewById(R.id.capturedItemListViewListView);
        capturedItems = new ArrayList<HashMap<String, String>>();
//        for (String key : capturedItemsHashMap.keySet()) {
//            capturedItems.add(key + " - $" + capturedItemsHashMap.get(key));
//        }
        for (String key : capturedItemsHashMap.keySet()) {
            HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
            hashMap.put("name", key);
            hashMap.put("amount", "$"+capturedItemsHashMap.get(key).toString());
            capturedItems.add(hashMap);//add the hashmap into arrayList
        }
//        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, capturedItems);
        String[] from = {"name", "amount"};//string array
        int[] to = {R.id.capturedItemName, R.id.capturedItemAmount};//int array of views id's
        adapter = new SimpleAdapter(getActivity(), capturedItems, R.layout.captured_row_item, from, to);
        capturedItemListViewListView.setAdapter(adapter);
        capturedItemListViewListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EnterBillFragment billFragment = EnterBillFragment.newInstance();
        Bundle args = new Bundle();
//        Log.d("Splittr", adapter.getItem(position).toString());
//        String[] itemNameCost = adapter.getItem(position).toString().split(" - $");
        String name = ((TextView) view.findViewById(R.id.capturedItemName)).getText().toString();
        String amount = ((TextView) view.findViewById(R.id.capturedItemAmount)).getText().toString().substring(1);
        args.putString("itemName", name);
        args.putString("itemCost", amount);
        args.putSerializable("capturedItems", capturedItemsHashMap);
        billFragment.setArguments(args);
        UtilityMethods.insertFragment(billFragment, R.id.content_frame, getActivity());
    }
}
