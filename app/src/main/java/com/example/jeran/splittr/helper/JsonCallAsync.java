package com.example.jeran.splittr.helper;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/*
Filename: com.example.jeran.splittr.helper.JsonCallAsync.java
Version: 1.0
Objective: Making JSON Calls to the server and returning the results from the server.
Author: Abhi Jadav
created: 20-Jan-2016
List of API/Library: Android SDK libraries
 */

public class JsonCallAsync extends AsyncTask<Void, Void, String> {


    private ContentValues requestBody;
    private ResponseListener responseListener;
    private Context context;
    private String requestUrl;
    private boolean is_progress;
    private ProgressDialog progressDialog;
    private ResponseBin responseBin = new ResponseBin();

    public JsonCallAsync(Context context, ContentValues nameValuePairs, String url, ResponseListener responseListener, boolean visibleProgress) {
        this.context = context;
        this.responseListener = responseListener;
        requestBody = nameValuePairs;
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
        return callJsonWS(requestBody, requestUrl);
    }

    private String callJsonWS(ContentValues nameValuePairs, String url) {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            /*File httpCacheFile = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheFile, httpCacheSize);
            HttpResponseCache cache = HttpResponseCache.getInstalled();*/

            Log.d("Splittr","url===> " + url);
            for (int i = 0; i < nameValuePairs.size(); i++) {
                Log.d("Splittr","Request===> " + nameValuePairs.get(i).getName());
                Log.d("Splittr","Value  ===> " + nameValuePairs.get(i).getValue());
                httppost.setEntity(new StringEntity(nameValuePairs.get(i).getValue(), "UTF-8"));
            }
//            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-Type", "application/json");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            return convertStreamToString(entity.getContent());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String convertStreamToString(InputStream is) {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (is_progress) {
            progressDialog.cancel();
        }

        for (int i = 0; i < requestBody.size(); i++) {
            Log.d("Splittr", "RESPONSE[" + requestBody.get(i).getName() + "] ===> " + result);
        }

        responseBin.setResponse(result);
        responseListener.setOnResponseListener(responseBin);
    }

}
