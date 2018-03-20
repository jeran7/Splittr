package com.example.jeran.splittr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class ActivitiesFragment extends Fragment {

    private View activitiesView;
    private ListView activitiesListView;
    private SimpleAdapter adapter;
    private List<HashMap<String, String>> activitiesList;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    public static ActivitiesFragment newInstance() {
        ActivitiesFragment fragment = new ActivitiesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activitiesView = inflater.inflate(R.layout.fragment_activities, container, false);

        findViewsById();
        setUpListView();
        loadActivities();

        return activitiesView;
    }

    private void findViewsById() {
        activitiesListView = (ListView) activitiesView.findViewById(R.id.activitiesListView);
    }

    private void setUpListView() {
        activitiesList = new ArrayList<>();
        String[] from = {"title", "subTitle", "time"};
        int[] to = {R.id.activityTitle, R.id.activitySubTitle, R.id.activityTime};
        adapter = new SimpleAdapter(getActivity(), activitiesList, R.layout.activities_list_row_item, from, to);
        activitiesListView.setAdapter(adapter);
    }

    private void loadActivities() {
        JSONObject getActivitiesObject = new JSONObject();

        try {
            getActivitiesObject.put("email", LandingActivity.email);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        if (InternetUtils.hasConnection(getActivity())) {
            new JsonCallAsync(getActivity(), "getActivitiesRequest", getActivitiesObject.toString(), LinkUtils.GET_ACTIVITIES_URL, activitiesListener, false, "GET").execute();
        } else {
            ToastUtils.showToast(getActivity(), "Unable to connect. Please check your Internet connection.", false);
        }
    }

    private ResponseListener activitiesListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            String response = responseBin.getResponse();

            try {
                JSONObject jsonObject = new JSONObject(response);
                String result = jsonObject.getString("result");

                if (result.equals("success")) {
                    activitiesList.clear();
                    JSONArray activities = jsonObject.getJSONArray("activities");

                    for (int i = 0; i < activities.length(); i++) {
                        String title = activities .getJSONObject(i).getString("title");
                        String subTitle = activities .getJSONObject(i).getString("subTitle");
                        String time = activities .getJSONObject(i).getString("time");

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("title", title);
                        hashMap.put("subTitle", subTitle);
//                        hashMap.put("time", UtilityMethods.getTimeAgo(time)+" ago");
                        activitiesList.add(hashMap);
                    }

                    adapter.notifyDataSetChanged();
                 } else if (result.equals("failed")) {
                    ToastUtils.showToast(getActivity(), "Couldn't retrieve activities", false);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
