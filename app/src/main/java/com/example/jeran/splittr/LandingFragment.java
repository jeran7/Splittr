package com.example.jeran.splittr;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.SummaryListViewAdapter;
import com.example.jeran.splittr.helper.SummaryListViewDataModel;
import com.example.jeran.splittr.helper.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class LandingFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match

    private View view;
    ListView listView;
    private TextView currentUserName;
    private TextView currentUserBalance;
    private ArrayList<SummaryListViewDataModel> dataModels;
    private SummaryListViewAdapter adapter;

    public LandingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LandingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandingFragment newInstance() {
        LandingFragment fragment = new LandingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_landing, container, false);
        findViewsById();
        dataModels = new ArrayList<>();
        adapter = new SummaryListViewAdapter(dataModels, getActivity());
        listView.setAdapter(adapter);
        loadSummary();

        return view;
    }

    private void findViewsById() {
        listView = (ListView) view.findViewById(R.id.summaryListView);
        currentUserName = (TextView) view.findViewById(R.id.currentUserName);
        currentUserBalance = (TextView) view.findViewById(R.id.currentUserBalance);
    }

    private void loadSummary() {

        currentUserName.setText(LandingActivity.name);
        JSONObject getSummaryObject = new JSONObject();

        try {
            getSummaryObject.put("email", LandingActivity.email);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        new JsonCallAsync(getActivity(), "getSummaryRequest", getSummaryObject.toString(), LinkUtils.GET_SUMMARY_URL, summaryListener, true, "GET").execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogForSelectedFriend(adapter.getItem(position).getFriendName());
    }

    private void showDialogForSelectedFriend(final String selectedFriend) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject settleUpData = new JSONObject();

                        try {
                            settleUpData.put("email", LandingActivity.email);
                            settleUpData.put("friend", selectedFriend);
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        new JsonCallAsync(getActivity(), "settleUpRequest", settleUpData.toString(), LinkUtils.SETTLE_UP_URL, settleUpListener, true, "GET").execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Settle Up")
                .setMessage(Html.fromHtml("Are you sure you want to settle up with <b>" + selectedFriend + "</b>?"))
                .setPositiveButton("Settle Up", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    ResponseListener summaryListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        double netBalance = 0;
                        dataModels.clear();
                        JSONArray users = jsonObject.getJSONArray("users");

                        for (int i = 0; i < users.length(); i++) {
                            String name = users.getJSONObject(i).getString("name");
                            double amount = users.getJSONObject(i).getDouble("amount");
                            String email = users.getJSONObject(i).getString("email");
                            netBalance += amount;

                            dataModels.add(new SummaryListViewDataModel(name, amount, email));
                        }

                        adapter.notifyDataSetChanged();
                        listView.setOnItemClickListener(LandingFragment.this);

                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        if (netBalance < 0) {
                            currentUserBalance.setTextColor(getResources().getColor(R.color.owes));
                            currentUserBalance.setText("You owe\n$" + df.format(Math.abs(netBalance)));
                        } else if (netBalance > 0) {
                            currentUserBalance.setText("You lent\n$" + df.format(Math.abs(netBalance)));
                        } else {
                            currentUserBalance.setText("You're\nsettled up");
                        }
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Couldn't retrieve summary", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private ResponseListener settleUpListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        ToastUtils.showToast(getActivity(), "Successfully settled up", true);
                        loadSummary();
                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Failed to settle up", false);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

}
