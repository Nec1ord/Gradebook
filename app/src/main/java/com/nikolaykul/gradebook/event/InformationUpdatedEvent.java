package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Information;

public class InformationUpdatedEvent {
    public final Information info;

    public InformationUpdatedEvent(Information info) {
        this.info = info;
    }
}
