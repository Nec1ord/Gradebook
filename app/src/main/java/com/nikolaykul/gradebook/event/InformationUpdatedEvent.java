package com.nikolaykul.gradebook.event;

import com.nikolaykul.gradebook.data.local.Database;
import com.nikolaykul.gradebook.data.model.Information;

public class InformationUpdatedEvent {
    @Database.InformationTable public final int TABLE;
    public final Information info;

    public InformationUpdatedEvent(@Database.InformationTable final int table,
                                   final Information info) {
        this.TABLE = table;
        this.info = info;
    }
}
