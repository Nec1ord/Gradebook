package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Group;

public class GroupUpdatedEvent {
    public Group group;

    public GroupUpdatedEvent(Group group) {
        this.group = group;
    }

}
