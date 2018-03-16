package com.example.jeran.splittr.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/*
Filename: com.example.jeran.splittr.helper.JsonCallAsync.java
Version: 1.0
Objective: Making JSON Calls to the server and returning the results from the server.
Author: Abhi Jadav
created: 20-Jan-2016
List of API/Library: Android SDK libraries
 */
public class JsonCallAsync extends AsyncTask<Void, Void, String> {


    private final String getOrPost;
    private String requestData;
    private ResponseListener responseListener;
    private Context context;
    private String requestUrl;
    private boolean is_progress;
    private ProgressDialog progressDialog;
    private ResponseBin responseBin = new ResponseBin();
    private String requestParam;

    public JsonCallAsync(Context context,String requestParam, String requestData, String url, ResponseListener responseListener, boolean visibleProgress, String getOrPost) {
        this.context = context;
        this.responseListener = responseListener;
        this.requestParam = requestParam;
        this.requestData = requestData;
        this.getOrPost = getOrPost;
        requestUrl = url;
        is_progress = visibleProgress;

        if (is_progress) {
            // show loading icon
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d("Splittr","URL => " + requestUrl);
        Log.d("Splittr","Param => " + requestData);
        return UtilityMethods.callJsonWS(requestParam, requestData, requestUrl, getOrPost);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (is_progress) {
            progressDialog.cancel();
        }

        Log.d("Splittr", result);

        responseBin.setResponse(result);
        responseListener.setOnResponseListener(responseBin);
    }

}
