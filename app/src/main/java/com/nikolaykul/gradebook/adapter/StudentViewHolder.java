package com.nikolaykul.gradebook.adapter;

import android.view.View;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.model.Student;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.row_text_view)
public class StudentViewHolder extends ItemViewHolder<Student> {

    @ViewId(R.id.simple_text) TextView tvText;

    public StudentViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(Student student, PositionInfo positionInfo) {
        tvText.setText(student.getFullName());
    }

    @Override
    public void onSetListeners() {
        tvText.setOnClickListener(view -> {
            StudentListener listener = getListener(StudentListener.class);
            if (listener != null) {
                listener.onClick(getItem());
            }
        });
    }

    public interface StudentListener {
        void onClick(Student student);
    }
}
