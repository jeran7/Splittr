package com.example.jeran.splittr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SummaryListViewAdapter extends ArrayAdapter<SummaryListViewDataModel> implements View.OnClickListener
{
    private ArrayList<SummaryListViewDataModel> data;
    Context context;

    private static class ViewHolder
    {
        TextView friendName;
        TextView netAmount;
    }

    public SummaryListViewAdapter(ArrayList<SummaryListViewDataModel> data, Context context)
    {
        super(context, R.layout.summary_view_row_item, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public void onClick(View view)
    {
        int position = (Integer) view.getTag();
        Object object = getItem(position);
        SummaryListViewDataModel dataModel = (SummaryListViewDataModel) object;

        //onclick to see what will happen when a particular expense with a friend is clicked
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SummaryListViewDataModel dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.summary_view_row_item, parent, false);

            viewHolder.friendName = convertView.findViewById(R.id.friendName);
            viewHolder.netAmount = convertView.findViewById(R.id.netAmount);

            result = convertView;
            convertView.setTag(viewHolder);
        }

        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.friendName.setText(dataModel.getFriendName());
        String netAmountStr = String.valueOf(dataModel.getAmount());

        if(netAmountStr.indexOf('.') == (netAmountStr.length() - 2))
        {
            netAmountStr = String.valueOf(dataModel.getAmount()) + "0";
        }

        if(dataModel.getAmount() == 0)
        {
            viewHolder.netAmount.setText("You're settled up");
        }

        else if(dataModel.getAmount() > 0)
        {
            viewHolder.netAmount.setText("You lent\n $" + netAmountStr);
            viewHolder.netAmount.setTextColor(context.getResources().getColor(R.color.splittrGreen));
        }

        else
        {
            viewHolder.netAmount.setText("You owe\n $" + Math.abs(dataModel.getAmount()));
            viewHolder.netAmount.setTextColor(context.getResources().getColor(R.color.owes));
        }

        return convertView;
    }
}
