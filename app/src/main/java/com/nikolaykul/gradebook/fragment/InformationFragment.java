package com.nikolaykul.gradebook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Group;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.data.model.Student;
import com.nikolaykul.gradebook.event.FloatingActionButtonEvent;
import com.nikolaykul.gradebook.event.GroupDeletedEvent;
import com.nikolaykul.gradebook.event.StudentAddedEvent;
import com.nikolaykul.gradebook.event.StudentDeletedEvent;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class InformationFragment extends BaseFragment {
    private static final String BUNDLE_TAB_NUM = "tabNum";
    private static final String BUNDLE_INFO_TABLE = "infoTable";
    private static final String BUNDLE_GROUP = "group";
    @Bind(R.id.table) TableLayout mTable;
    @Bind(R.id.students_layout) LinearLayout mColumnStudents;
    @Bind(R.id.header_layout) LinearLayout mHeaderLayout;
    @Bind(R.id.scroll_students_layout) NestedScrollView mScrollStudentsColumn;
    @Bind(R.id.scroll_table) NestedScrollView mScrollTable;
    @Inject Activity mActivity;
    @Inject Database mDatabase;
    @Inject Bus mBus;
    private List<Student> mStudents;
    private int mTabNum;
    private int mInfoTable;
    private long mGroupId;
    // dimens
    private float mStudentsTextSize;
    private float mHeaderTextSize;
    private int mStudentsTextPadding;
    private int mRowViewHeight;
    private int mRowViewWidth;

    public static InformationFragment newInstance(int tabNum,
                                                  @Database.InformationTable int infoTable,
                                                  long groupId) {
        InformationFragment fragment = new InformationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TAB_NUM, tabNum);
        bundle.putInt(BUNDLE_INFO_TABLE, infoTable);
        bundle.putLong(BUNDLE_GROUP, groupId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void setActivityComponent() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.register(this);

        Bundle args = getArguments();
        if (null != args) {
            mTabNum = args.getInt(BUNDLE_TAB_NUM, -1);
            mInfoTable = args.getInt(BUNDLE_INFO_TABLE, -1);
            mGroupId = args.getLong(BUNDLE_GROUP, -1);
        } else {
            mTabNum = mInfoTable = -1;
            mGroupId = -1;
        }
        mStudents = mDatabase.getStudents(mGroupId);
        mStudentsTextSize = getResources().getDimension(R.dimen.text_small_size);
        mStudentsTextPadding = (int) getResources().getDimension(R.dimen.text_normal_padding);
        mHeaderTextSize = getResources().getDimension(R.dimen.text_tiny_size);
        mRowViewWidth = (int) getResources().getDimension(R.dimen.table_row_view_width);
        mRowViewHeight = (int) getResources().getDimension(R.dimen.table_row_view_height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setScrollListener();
        refreshContainers();
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Subscribe public void showDialogToAddNewInformation(FloatingActionButtonEvent event) {
        if (mTabNum != event.currentTabNum) return;
        if (-1 == mGroupId) {
            View focusedView = mActivity.getCurrentFocus();
            if (null != focusedView) {
                Snackbar.make(focusedView,
                        R.string.error_missing_group,
                        Snackbar.LENGTH_SHORT).show();
            }
            return;
        }

        if (Database.TABLE_ATTENDANCE == mInfoTable) {
            showCalendarDialog();
        } else {
            showTitleDialog();
        }
    }

    @Subscribe public void onGroupSelected(final Group group) {
        mGroupId = group.getId();
        mStudents.clear();
        mStudents.addAll(mDatabase.getStudents(mGroupId));
        refreshContainers();
    }

    @Subscribe public void onGroupDeleted(final GroupDeletedEvent event) {
        if (mGroupId == event.group.getId()) {
            mGroupId = -1;
            mStudents.clear();
            mStudents.addAll(mDatabase.getStudents(mGroupId));
            refreshContainers();
        }
    }

    @Subscribe public void onStudentAdded(StudentAddedEvent event) {
        mStudents.clear();
        mStudents.addAll(mDatabase.getStudents(mGroupId));
        refreshContainers();
    }

    @Subscribe public void onStudentDeleted(StudentDeletedEvent event) {
        mStudents.clear();
        mStudents.addAll(mDatabase.getStudents(mGroupId));
        refreshContainers();
    }

    private void showCalendarDialog() {
        // create callback
        MaterialDialog.SingleButtonCallback positiveCallback = (materialDialog, dialogAction) -> {
            materialDialog.dismiss();
            MaterialCalendarView calendarView =
                    (MaterialCalendarView) materialDialog.getCustomView();
            if (null == calendarView) return;

            List<CalendarDay> calendarDayList = calendarView.getSelectedDates();
            if (calendarDayList.isEmpty()) return;

            Information newInformation = new Information();
            for (CalendarDay calendarDay : calendarDayList) {
                newInformation.setDate(new DateTime(calendarDay.getDate()));
                mDatabase.insertInformation(newInformation, mGroupId, mInfoTable);
            }

            Toast.makeText(mActivity, R.string.message_add_success, Toast.LENGTH_SHORT).show();
            refreshContainers();
        };

        // create calendar
        MaterialCalendarView calendarView = new MaterialCalendarView(mActivity);
        calendarView.clearSelection();
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        // show dialog
        new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_add_information_calendar_title)
                .customView(calendarView, true)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_add)
                .onPositive(positiveCallback)
                .build()
                .show();
    }

    private void showTitleDialog() {
        // create callback
        MaterialDialog.SingleButtonCallback positiveCallback = (materialDialog, dialogAction) -> {
            materialDialog.dismiss();
            if (null != materialDialog.getInputEditText()) {
                String title = materialDialog.getInputEditText().getText().toString();
                if (!title.isEmpty()) {
                    Information newInformation = new Information().setTitle(title);
                    mDatabase.insertInformation(newInformation, mGroupId, mInfoTable);

                    Toast.makeText(mActivity,
                            R.string.message_add_success,
                            Toast.LENGTH_SHORT).show();
                    refreshContainers();
                }
            }
        };

        // show dialog
        new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_add_information_title)
                .input(R.string.dialog_add_information_hint, 0,
                        (materialDialog, charSequence) -> {/* no filter */})
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_add)
                .onPositive(positiveCallback)
                .build()
                .show();
    }

    private void showDialogToDeleteInformation(Information info) {
        // create callback
        MaterialDialog.SingleButtonCallback positiveCallback = (materialDialog, dialogAction) -> {
            materialDialog.dismiss();
            mDatabase.removeInformation(info, mGroupId, mInfoTable);
            refreshContainers();
        };

        // generate message
        String message = getResources().getString(R.string.dialog_delete_information_message);
        if (Database.TABLE_ATTENDANCE == mInfoTable) {
            final DateFormat df = new SimpleDateFormat("dd/MM", Locale.getDefault());
            message = String.format(message, df.format(info.getDate()));
        } else {
            message = String.format(message, info.getTitle());
        }

        // show dialog
        new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_delete_information_title)
                .content(message)
                .negativeText(R.string.action_cancel)
                .positiveText(R.string.action_delete)
                .onPositive(positiveCallback)
                .build()
                .show();
    }

    /**
     * Enable scrolling {@link #mScrollStudentsColumn} and {@link #mScrollTable} simultaneously.
     */
    private void setScrollListener() {
        NestedScrollView.OnScrollChangeListener scrollChangeListener =
                (NestedScrollView view, int x, int y, int oldX, int oldY) -> {
                    if (view == mScrollStudentsColumn) {
                        mScrollTable.scrollTo(x, y);
                    } else {
                        mScrollStudentsColumn.scrollTo(x, y);
                    }
                };
        mScrollTable.setOnScrollChangeListener(scrollChangeListener);
        mScrollStudentsColumn.setOnScrollChangeListener(scrollChangeListener);
    }

    private void refreshContainers() {
        // clear
        mHeaderLayout.removeAllViews();
        mColumnStudents.removeAllViews();
        mTable.removeAllViews();

        // if there're no students or no any information about them -> there's nothing to show
        if (mStudents.isEmpty()
                || mDatabase.getInformation(mStudents.get(0).getId(), mInfoTable).isEmpty()) return;

        // populate
        populateHeader();
        for (Student student : mStudents) {
            // populate LinearLayout (students)
            mColumnStudents.addView(createViewStudentName(student.getFullName()));

            // populate TableLayout (content)
            mTable.addView(createRowContent(student.getId()));
            mTable.addView(createDivider(true));
        }
    }

    private void populateHeader() {
        mColumnStudents.addView(createViewEmpty());

        long someStudentId = mStudents.get(0).getId();
        List<Information> infoList = mDatabase.getInformation(someStudentId, mInfoTable);
        for (Information info : infoList) {
            mHeaderLayout.addView(createViewHeader(info));
        }
    }

    private TableRow createRowContent(long studentId) {
        TableRow row = new TableRow(mActivity);
        List<Information> infoList = mDatabase.getInformation(studentId, mInfoTable);
        for (Information info : infoList) {
            row.addView(createViewContent(info));
            row.addView(createDivider(false));
        }
        return row;
    }

    private TextView createViewStudentName(String studentName) {
        TextView tv = new TextView(mActivity);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                mRowViewHeight));
        tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        tv.setPadding(mStudentsTextPadding, 0, 0, 0);
        tv.setSingleLine();
        tv.setTextSize(mStudentsTextSize);
        tv.setText(studentName);
        return tv;
    }

    private TextView createViewHeader(Information info) {
        String text;
        if (Database.TABLE_ATTENDANCE == mInfoTable) {
            text = info.getDate().monthOfYear().getAsShortText() +
                    "/" + info.getDate().dayOfWeek().getAsShortText();
        } else {
            text = info.getTitle();
        }

        TextView tv = new TextView(mActivity);
        tv.setLayoutParams(new TableRow.LayoutParams(mRowViewWidth, mRowViewHeight));
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tv.setTextSize(mHeaderTextSize);
        tv.setText(text);
        tv.setTag(info);
        tv.setOnLongClickListener(iView -> {
            Information currentInfo = (Information) tv.getTag();
            showDialogToDeleteInformation(currentInfo);
            return true;
        });
        return tv;
    }

    private View createViewContent(Information info) {
        View view = new View(mActivity);
        view.setLayoutParams(new TableRow.LayoutParams(mRowViewWidth, mRowViewHeight));
        view.setBackgroundColor(ContextCompat.getColor(mActivity, info.isPassed()
                ? R.color.green
                : R.color.red));
        view.setTag(info);
        view.setOnClickListener(iView -> {
            Information currentInfo = (Information) view.getTag();
            currentInfo.setPassed(!currentInfo.isPassed());
            view.setBackgroundColor(ContextCompat.getColor(mActivity, currentInfo.isPassed()
                    ? R.color.green
                    : R.color.red));
            mDatabase.updateInformation(currentInfo, mInfoTable);
            view.setTag(currentInfo);
        });
        return view;
    }

    private View createViewEmpty() {
        View view = new View(mActivity);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                mRowViewHeight));
        return view;
    }

    /**
     * Creates whether horizontal (for rows) or vertical (for columns) divider.
     * @return view that represents a divider.
     */
    private View createDivider(boolean isHorizontalDivider) {
        int width  = isHorizontalDivider ? TableRow.LayoutParams.MATCH_PARENT : 1;
        int height = isHorizontalDivider ? 1 : TableRow.LayoutParams.MATCH_PARENT;

        View view = new View(mActivity);
        view.setLayoutParams(new TableRow.LayoutParams(width, height));
        view.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gray));
        return view;
    }

}
