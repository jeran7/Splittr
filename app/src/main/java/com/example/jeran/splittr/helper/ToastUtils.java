package com.example.jeran.splittr.helper;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.jeran.splittr.R;

public class ToastUtils
{
    public static void showToast(Context context, String message, boolean isPositiveAction)
    {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setPadding(20, 20, 20, 20);

        if(isPositiveAction)
        {
            view.setBackgroundColor(context.getResources().getColor(R.color.splittrGreen));
        }

        else
        {
            view.setBackgroundColor(context.getResources().getColor(R.color.owes));
        }

        toast.show();
    }
}
