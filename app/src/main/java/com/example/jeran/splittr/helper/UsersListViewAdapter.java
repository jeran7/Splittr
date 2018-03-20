package com.example.jeran.splittr.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jeran.splittr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhi on 19-Mar-18.
 */

public class UsersListViewAdapter extends ArrayAdapter<UsersDataModel> {
    private ArrayList<UsersDataModel> data;
    Context context;
    private int fragmentId;

    private static class ViewHolder {
        CircleImageView pic;
        TextView name;
        TextView email;
        CheckBox checkBox;
    }

    public UsersListViewAdapter(ArrayList<UsersDataModel> data, Context context, int fragmentId) {
        super(context, R.layout.user_list_row_item, data);
        this.data = data;
        this.context = context;
        this.fragmentId = fragmentId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UsersDataModel dataModel = getItem(position);
        UsersListViewAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user_list_row_item, parent, false);

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.pic = convertView.findViewById(R.id.pic);
            viewHolder.email = convertView.findViewById(R.id.email);
            viewHolder.checkBox= convertView.findViewById(R.id.checkbox);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UsersListViewAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.with(context)
                .load(dataModel.getPic())
                .into(viewHolder.pic);
        viewHolder.name.setText(dataModel.getname());
        viewHolder.email.setText(dataModel.getEmail());

        if(fragmentId==2){
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}
