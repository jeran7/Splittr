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

import com.example.jeran.splittr.helper.CircleImageView;
import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.SummaryListViewAdapter;
import com.example.jeran.splittr.helper.SummaryListViewDataModel;
import com.example.jeran.splittr.helper.ToastUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SummaryFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match

    private View view;
    ListView listView;
    private CircleImageView currentUserPic;
    private TextView currentUserName, currentUserEmail, currentUserBalance;
    private ArrayList<SummaryListViewDataModel> dataModels;
    private SummaryListViewAdapter adapter;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        findViewsById();
        setUpListView();
        loadSummary();

        return view;
    }

    private void findViewsById() {
        listView = (ListView) view.findViewById(R.id.summaryListView);
        currentUserPic = (CircleImageView) view.findViewById(R.id.currentUserPic);
        currentUserName = (TextView) view.findViewById(R.id.currentUserName);
        currentUserEmail = (TextView) view.findViewById(R.id.currentUserEmail);
        currentUserBalance = (TextView) view.findViewById(R.id.currentUserBalance);
    }

    private void setUpListView() {
        dataModels = new ArrayList<>();
        adapter = new SummaryListViewAdapter(dataModels, getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(SummaryFragment.this);
    }

    private void loadSummary() {
        Picasso.with(getActivity())
                .load(LinkUtils.PROFILE_PIC_PATH + LandingActivity.email + ".png")
                .into(currentUserPic);
        currentUserName.setText(LandingActivity.name);
        currentUserEmail.setText(LandingActivity.email);
        JSONObject getSummaryObject = new JSONObject();

        try {
            getSummaryObject.put("email", LandingActivity.email);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        if (InternetUtils.hasConnection(getActivity())) {
            new JsonCallAsync(getActivity(), "getSummaryRequest", getSummaryObject.toString(), LinkUtils.GET_SUMMARY_URL, summaryListener, true, "GET").execute();
        } else {
            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialogForSelectedFriend(adapter.getItem(position).getname(), adapter.getItem(position).getEmail());
    }

    private void showDialogForSelectedFriend(String selectedFriend, final String selectedFriendEmail) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject settleUpData = new JSONObject();

                        try {
                            settleUpData.put("email", LandingActivity.email);
                            settleUpData.put("friend", selectedFriendEmail);
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        if (InternetUtils.hasConnection(getActivity())) {
                            new JsonCallAsync(getActivity(), "settleUpRequest", settleUpData.toString(), LinkUtils.SETTLE_UP_URL, settleUpListener, true, "GET").execute();
                        } else {
                            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
                        }

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

                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        if (netBalance < 0) {
                            currentUserBalance.setTextColor(getResources().getColor(R.color.owes));
                            currentUserBalance.setText("You owe\n$" + df.format(Math.abs(netBalance)));
                        } else if (netBalance > 0) {
                            currentUserBalance.setText("You lent\n$" + df.format(Math.abs(netBalance)));
                        } else {
                            currentUserBalance.setTextColor(getResources().getColor(R.color.gray));
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
