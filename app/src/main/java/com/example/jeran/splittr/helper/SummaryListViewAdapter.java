package com.example.jeran.splittr.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jeran.splittr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SummaryListViewAdapter extends ArrayAdapter<SummaryListViewDataModel> {
    private ArrayList<SummaryListViewDataModel> data;
    Context context;

    private static class ViewHolder {
        TextView name;
        TextView friendEmail;
        TextView netAmount;
        CircleImageView friendPic;
    }

    public SummaryListViewAdapter(ArrayList<SummaryListViewDataModel> data, Context context) {
        super(context, R.layout.summary_view_row_item, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SummaryListViewDataModel dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.summary_view_row_item, parent, false);

            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.friendPic = convertView.findViewById(R.id.friendPic);
            viewHolder.friendEmail = convertView.findViewById(R.id.friendEmail);
            viewHolder.netAmount = convertView.findViewById(R.id.netAmount);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.with(context)
                .load(LinkUtils.PROFILE_PIC_PATH + dataModel.getEmail() + ".png")
                .into(viewHolder.friendPic);
        viewHolder.name.setText(dataModel.getname());
        viewHolder.friendEmail.setText(dataModel.getEmail());
        String netAmountStr = String.valueOf(dataModel.getAmount());

        if (netAmountStr.indexOf('.') == (netAmountStr.length() - 2)) {
            netAmountStr = String.valueOf(dataModel.getAmount()) + "0";
        }

        if (dataModel.getAmount() == 0) {
            viewHolder.netAmount.setText("You're settled up");
            viewHolder.netAmount.setTextColor(context.getResources().getColor(R.color.gray));
        } else if (dataModel.getAmount() > 0) {
            viewHolder.netAmount.setText("You lent\n $" + netAmountStr);
            viewHolder.netAmount.setTextColor(context.getResources().getColor(R.color.splittrGreen));
        } else {
            viewHolder.netAmount.setText("You owe\n $" + Math.abs(dataModel.getAmount()));
            viewHolder.netAmount.setTextColor(context.getResources().getColor(R.color.owes));
        }

        return convertView;
    }
}
