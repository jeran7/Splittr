package com.example.jeran.splittr.helper;

import android.util.Log;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Abhi on 15-Mar-18.
 */

class UtilityMethods {

    public static String callJsonWS(String requestParam, String requestData, String url, String getOrPost) {
        try {
            // avoid creating several instances, should be singleton
            OkHttpClient client = new OkHttpClient();
            Request request = null;
            if (getOrPost == "POST") {
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestData);
                request = new Request.Builder()
                        .url(url)
                        .method(getOrPost, RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();
            } else {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                urlBuilder.addQueryParameter(requestParam, requestData);
                request = new Request.Builder()
                        .url(urlBuilder.build().toString())
                        .build();
            }

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (Exception e) {
            Log.d("Splittr", e.toString());
            return null;
        }
    }
}
