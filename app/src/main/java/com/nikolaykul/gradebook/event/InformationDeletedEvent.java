package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;

public class InformationDeletedEvent {
    @Database.InformationTable public final int TABLE;
    public final Information info;

    public InformationDeletedEvent(@Database.InformationTable final int table,
                                   final Information info) {
        this.TABLE = table;
        this.info = info;
    }
}