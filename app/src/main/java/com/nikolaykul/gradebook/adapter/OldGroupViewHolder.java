package com.nikolaykul.gradebook.adapter;

import android.view.View;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.model.Group;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.row_text_view)
public class OldGroupViewHolder extends ItemViewHolder<Group> {
    @ViewId(R.id.simple_text) TextView tvText;

    public OldGroupViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Group group, PositionInfo positionInfo) {
        tvText.setText(group.getName());
    }

    @Override
    public void onSetListeners() {
        tvText.setOnClickListener(iView -> {
            StudentGroupListener listener = getListener(StudentGroupListener.class);
            if (null != listener) listener.onClick(getItem());
        });
    }

    public interface StudentGroupListener {
        void onClick(Group group);
    }

}
