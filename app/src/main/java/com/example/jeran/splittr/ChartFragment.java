package com.example.jeran.splittr;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.helper.InternetUtils;
import com.example.jeran.splittr.helper.JsonCallAsync;
import com.example.jeran.splittr.helper.LinkUtils;
import com.example.jeran.splittr.helper.ResponseBin;
import com.example.jeran.splittr.helper.ResponseListener;
import com.example.jeran.splittr.helper.ToastUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abhi on 20-Mar-18.
 */

public class ChartFragment extends Fragment {

    private View chartView;
    private PieChart pieChart1, pieChart2, pieChart3;
    private TextView pieChart1Title, pieChart2Title, pieChart3Title;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chartView = inflater.inflate(R.layout.fragment_chart, container, false);

        findViewsById();
        loadSummary();

        return chartView;
    }

    private void findViewsById() {
        pieChart1 = (PieChart) chartView.findViewById(R.id.pieChart1);
        pieChart2 = (PieChart) chartView.findViewById(R.id.pieChart2);
        pieChart3 = (PieChart) chartView.findViewById(R.id.pieChart3);
        pieChart1Title = (TextView)chartView.findViewById(R.id.pieChart1Title);
        pieChart2Title = (TextView)chartView.findViewById(R.id.pieChart2Title);
        pieChart3Title = (TextView)chartView.findViewById(R.id.pieChart3Title);
    }

    private void setupPieChart3(HashMap<String, Double> pieChart3Data) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        int i=0;
        for(String key: pieChart3Data.keySet()){
            entries.add(new PieEntry((float) pieChart3Data.get(key).doubleValue(), key, i++));
        }

        PieDataSet dataset = new PieDataSet(entries, "Amount in $");
        dataset.setValueTextSize(25.0f);
        dataset.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataset.setFormSize(17.0f);
        dataset.setValueTextColor(Color.WHITE);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataset);
        pieChart3Title.setText("TOTAL OWED FOR "+LandingActivity.name.toUpperCase());
        pieChart3.setData(data);
        pieChart3.animateY(1000);
    }

    private void setupPieChart2(HashMap<String, Double> pieChart2Data) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        int i=0;
        for(String key: pieChart2Data.keySet()){
            entries.add(new PieEntry((float) pieChart2Data.get(key).doubleValue(), key, i++));
        }

        PieDataSet dataset = new PieDataSet(entries, "Amount in $");
        dataset.setValueTextSize(25.0f);
        dataset.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataset.setFormSize(17.0f);
        dataset.setValueTextColor(Color.WHITE);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataset);
        pieChart2Title.setText("TOTAL LENTED FOR "+LandingActivity.name.toUpperCase());
        pieChart2.setData(data);
        pieChart2.animateY(1000);
    }

    private void setupPieChart1(double totalOwed, double totalLented) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalOwed, "Owed", 0));
        entries.add(new PieEntry((float) totalLented, "Lented", 1));

        PieDataSet dataset = new PieDataSet(entries, "Amount in $");
        dataset.setValueTextSize(25.0f);
        dataset.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataset.setFormSize(17.0f);
        dataset.setValueTextColor(Color.WHITE);
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataset);
        pieChart1Title.setText("TOTAL OWE/LENT FOR "+LandingActivity.name.toUpperCase());
        pieChart1.setData(data);
        pieChart1.animateY(1000);
    }

    private void loadSummary() {
        JSONObject getSummaryObject = new JSONObject();

        try {
            getSummaryObject.put("email", LandingActivity.email);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        if (InternetUtils.hasConnection(getActivity())) {
            new JsonCallAsync(getActivity(), "getSummaryRequest", getSummaryObject.toString(), LinkUtils.GET_SUMMARY_URL, summaryListener, false, "GET").execute();
        } else {
            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
        }
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

                        double totalOwed = 0;
                        double totalLented = 0;
                        HashMap<String, Double> pieChart2Data = new HashMap<>();
                        HashMap<String, Double> pieChart3Data = new HashMap<>();

                        JSONArray users = jsonObject.getJSONArray("users");

                        for (int i = 0; i < users.length(); i++) {
                            String name = users.getJSONObject(i).getString("name");
                            double amount = users.getJSONObject(i).getDouble("amount");

                            if (amount > 0) {
                                // lented money
                                totalLented += amount;
                                pieChart2Data.put(name, amount);
                            } else if (amount < 0) {
                                // owed money
                                totalOwed += Math.abs(amount);
                                pieChart3Data.put(name, Math.abs(amount));
                            }

                        }

                        setupPieChart1(totalOwed, totalLented);
                        setupPieChart2(pieChart2Data);
                        setupPieChart3(pieChart3Data);

                    } else if (result.equals("failed")) {
                        ToastUtils.showToast(getActivity(), "Couldn't retrieve data for chart", false);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


}
