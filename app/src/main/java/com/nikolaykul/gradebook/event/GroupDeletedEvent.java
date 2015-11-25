package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.model.Group;

public class GroupDeletedEvent {
    public Group group;

    public GroupDeletedEvent(Group group) {
        this.group = group;
    }

}
