package com.example.jeran.splittr.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Abhi on 15-Mar-18.
 */

public class UtilityMethods {

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

    public static void sendRegistrationToServer(final Context context, String email, String token) {
        final JSONObject tokenData = new JSONObject();
        try {
            tokenData.put("email", email);
            tokenData.put("gcmToken", token);
        } catch (JSONException e) {
            Log.d("Splittr", e.toString());
        }

        Log.d("Splittr", "URL => " + LinkUtils.SAVE_GCM_TOKEN_URL);
        Log.d("Splittr", "Param => " + tokenData.toString());

        final ResponseListener responseListener = new ResponseListener() {
            @Override
            public void setOnResponseListener(ResponseBin responseBin) {
                if (responseBin != null && responseBin.getResponse() != null) {
                    String response = responseBin.getResponse();
                    Log.d("Splittr", response.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.getString("result");

                        if (result.equals("failed")) {
                            ToastUtils.showToast(context, "Failed to register GCM Token to server. You may not receive the push notifications.", false);
                        }
                    } catch (JSONException jsonException) {
                        Log.d("Splittr", jsonException.toString() + " Came from MyFirebaseInstanceIDService");
                    }
                }
            }
        };
        new JsonCallAsync(context, "saveGcmTokenRequest", tokenData.toString(), LinkUtils.SAVE_GCM_TOKEN_URL, responseListener, false, "GET").execute();
    }


    public static void insertFragment(Fragment fragment, int container, FragmentActivity activity) {
        // TODO Auto-generated method stub

        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(container, fragment)
                    .commit();
        }

    }
}
