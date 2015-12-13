package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.local.Database;

public class InformationAddedEvent {
    @Database.InformationTable public final int TABLE;

    public InformationAddedEvent(@Database.InformationTable final int table) {
        this.TABLE = table;
    }
}