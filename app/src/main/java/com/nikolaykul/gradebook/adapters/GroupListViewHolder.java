package com.nikolaykul.gradebook.adapters;

import android.view.View;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.models.StudentGroup;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.row_text_view)
public class GroupListViewHolder extends ItemViewHolder<StudentGroup> {
    @ViewId(R.id.text) TextView tvText;

    public GroupListViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(StudentGroup group, PositionInfo positionInfo) {
        tvText.setText(group.name);
    }

    @Override
    public void onSetListeners() {
        tvText.setOnClickListener(iView -> {
            StudentGroupListener listener = getListener(StudentGroupListener.class);
            if (null != listener) listener.onClick(getItem());
        });
    }

    public interface StudentGroupListener {
        void onClick(StudentGroup group);
    }

}
