package com.nikolaykul.gradebook.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.nikolaykul.gradebook.R;

public class MViewUtils {
    public static View getDetailRowView(Context context, boolean isGood) {
        int width = (int) context.getResources().getDimension(R.dimen.table_row_view_width);
        int height = (int) context.getResources().getDimension(R.dimen.table_row_view_height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

        View view = new View(context);
        view.setLayoutParams(params);
        view.setBackgroundColor(ContextCompat.getColor(context, isGood
                ? ContextCompat.getColor(context, R.color.green)
                : ContextCompat.getColor(context, R.color.red)));
        return view;
    }
}
