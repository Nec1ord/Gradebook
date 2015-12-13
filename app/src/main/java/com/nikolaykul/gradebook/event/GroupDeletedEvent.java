package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Group;

public class GroupDeletedEvent {
    public final Group group;

    public GroupDeletedEvent(final Group group) {
        this.group = group;
    }

}
