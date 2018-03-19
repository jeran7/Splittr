package com.example.jeran.splittr.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeran.splittr.LandingActivity;
import com.example.jeran.splittr.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Abhi on 19-Mar-18.
 */

public class FriendsListViewAdapter extends ArrayAdapter<FriendListViewDataModel> implements AdapterView.OnItemClickListener {
    private ArrayList<FriendListViewDataModel> data;
    Context context;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendListViewDataModel dataModel = (FriendListViewDataModel) getItem(position);
        showDialogForSelectedFriend(dataModel);
    }

    private static class ViewHolder {
        CircleImageView friendPic;
        TextView name;
        TextView friendEmail;
    }

    public FriendsListViewAdapter(ArrayList<FriendListViewDataModel> data, Context context) {
        super(context, R.layout.friend_list_row_item, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendListViewDataModel dataModel = getItem(position);
        FriendsListViewAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.friend_list_row_item, parent, false);

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.friendPic = convertView.findViewById(R.id.friendPic);
            viewHolder.friendEmail = convertView.findViewById(R.id.friendEmail);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendsListViewAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.with(context)
                .load(LinkUtils.PROFILE_PIC_PATH + dataModel.getFriendEmail() + ".png")
                .into(viewHolder.friendPic);
        viewHolder.name.setText(dataModel.getname());
        viewHolder.friendEmail.setText(dataModel.getFriendEmail());

        return convertView;
    }

    private void showDialogForSelectedFriend(final FriendListViewDataModel selectedFriendDataModel) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        JSONObject addFriendData = new JSONObject();

                        try {
                            addFriendData.put("email", LandingActivity.email);
                            addFriendData.put("friend", selectedFriendDataModel.getFriendEmail());
                        } catch (JSONException e) {
                            Log.d("Splittr", e.toString());
                        }

                        if (InternetUtils.hasConnection(context)) {
                            new JsonCallAsync(context, "addFriendRequest", addFriendData.toString(), LinkUtils.ADD_FRIENDS_URL, addFriendListener, true, "GET").execute();
                        } else {
                            ToastUtils.showToast(context, "Unable to connect. Please check your Internet connection.", false);
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle("Add Friend")
                .setMessage(Html.fromHtml("<b>" + selectedFriendDataModel.getname() + "</b><br>" + selectedFriendDataModel.getFriendEmail()))
                .setPositiveButton("Add", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    ResponseListener addFriendListener = new ResponseListener() {
        @Override
        public void setOnResponseListener(ResponseBin responseBin) {
            if (responseBin != null && responseBin.getResponse() != null) {
                String response = responseBin.getResponse();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");

                    if (result.equals("success")) {
                        ToastUtils.showToast(context, "Added friend successfully", true);
                    } else if (result.equals("isFriendAlready")) {
                        ToastUtils.showToast(context, "Already a friend", false);
                    }
                } catch (JSONException error) {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
