package com.nikolaykul.gradebook.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.models.Student;
import com.nikolaykul.gradebook.data.models.StudentGroup;
import com.nikolaykul.gradebook.data.models.StudentInfo;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DialogFactory {

    public static MaterialDialog getMaterialAddDialog(Context context,
                                                      Class<?> object,
                                                      SingleButtonCallback positiveCallback) {
        boolean isStudentInfo = false;
        int titleRes = 0;
        if (object.isAssignableFrom(Student.class)) {
            titleRes = R.string.dialog_add_student_title;
        } else if (object.isAssignableFrom(StudentGroup.class)) {
            titleRes = R.string.dialog_add_student_group_title;
        } else if (object.isAssignableFrom(StudentInfo.class)) {
            titleRes = R.string.dialog_add_student_info_title;
            isStudentInfo = true;
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(titleRes)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_add)
                .onPositive(positiveCallback);

        if (isStudentInfo) {
            builder.customView(createCalendarView(context), true);
        } else {
            builder.input(R.string.dialog_input_hint, 0, (materialDialog, charSequence) -> {
                // no filter
            });
        }

        return builder.build();
    }

    public static MaterialDialog getMaterialDeleteDialog(Context context,
                                                         StudentInfo info,
                                                         SingleButtonCallback positiveCallback) {
        final DateFormat df = new SimpleDateFormat("dd/MM", Locale.getDefault());
        String message =
                context.getResources().getString(R.string.dialog_delete_student_info_message);
        message = String.format(message, df.format(info.date));

        return new MaterialDialog.Builder(context)
                .title(R.string.dialog_delete_student_info_title)
                .content(message)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_delete)
                .onPositive(positiveCallback)
                .build();
    }

    private static MaterialCalendarView createCalendarView(Context context) {
        MaterialCalendarView calendarView = new MaterialCalendarView(context);
        calendarView.clearSelection();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        return calendarView;
    }

}
