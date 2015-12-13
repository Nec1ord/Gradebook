package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.local.Database;

public class InformationDeletedEvent {
    @Database.InformationTable public final int TABLE;

    public InformationDeletedEvent(@Database.InformationTable final int table) {
        this.TABLE = table;
    }
}