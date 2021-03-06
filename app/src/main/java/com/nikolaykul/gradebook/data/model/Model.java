package com.nikolaykul.gradebook.data.model;

import com.nikolaykul.gradebook.data.local.Database;
import com.squareup.otto.Bus;

public interface Model {
    String setTitle(String newTitle);
    String getTitle();
    void insertInDatabase(Database database);
    void removeFromDatabase(Database database);
    void updateInDatabase(Database database);
    void notifyInserted(Bus bus);
    void notifyRemoved(Bus bus);
    void notifyUpdated(Bus bus);
    void notifyClicked(Bus bus);
}
