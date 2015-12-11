package com.nikolaykul.gradebook.other;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nikolaykul.gradebook.R;
import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;
import com.nikolaykul.gradebook.event.InformationUpdatedEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class InfoView extends TextView {
    private Information mInfo;
    @Database.InformationTable private int mTable;

    public InfoView(Context context) {
        super(context);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Information info,
                     @Database.InformationTable int table,
                     Database database, Bus bus) {
        mInfo = info;
        mTable = table;
        updateBackground();
        registerOnClick(database, bus);
        registerEventReceiver(bus);
    }


    private void updateBackground() {
        this.setBackgroundResource(mInfo.isPassed() ? R.color.green : R.color.red);
    }

    private void registerOnClick(Database database, Bus bus) {
        this.setOnClickListener(v -> {
            mInfo.setPassed(!mInfo.isPassed());
            database.updateInformation(mInfo, mTable);
            bus.post(new InformationUpdatedEvent(mInfo));
        });
    }

    private void registerEventReceiver(Bus bus) {
        bus.register(new Object() {
            @Subscribe public void onInformationUpdated(InformationUpdatedEvent event) {
                if (mInfo.equals(event.info)) {
                    mInfo.update(event.info);
                    updateBackground();
                }
            }
        });
    }

}
