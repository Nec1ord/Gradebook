package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Group;

public class GroupUpdatedEvent {
    public final Group group;

    public GroupUpdatedEvent(final Group group) {
        this.group = group;
    }

}
