package com.nikolaykul.gradebook.other;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;

public class InformationViewFactory {
    private final Context mContext;
    private final CustomDimens mCustomDimens;

    public InformationViewFactory(Context context) {
        mContext = context;
        mCustomDimens = new CustomDimens(context);
    }

    public TextView createHeaderView() {
        TextView view = new TextView(mContext);
        view.setLayoutParams(
                new ViewGroup.LayoutParams(mCustomDimens.WIDTH, mCustomDimens.HEIGHT));
        view.setGravity(Gravity.CENTER);
        view.setSingleLine();
        view.setTextSize(mCustomDimens.HEADER_TEXT_SIZE);
        return view;
    }

    private class CustomDimens {
        public final int WIDTH;
        public final int HEIGHT;
        public final int HEADER_TEXT_SIZE;

        public CustomDimens(Context context) {
            WIDTH = (int) context.getResources().getDimension(R.dimen.table_row_view_width);
            HEIGHT = (int) context.getResources().getDimension(R.dimen.table_row_view_height);
            HEADER_TEXT_SIZE = (int) context.getResources().getDimension(R.dimen.text_tiny_size);
        }

    }

}
