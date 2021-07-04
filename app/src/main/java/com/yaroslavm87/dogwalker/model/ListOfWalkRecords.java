package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListOfWalkRecords implements Observable, Subscriber {

    private final LinkedList<WalkRecord> LIST_OF_WALK_RECORDS;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        LIST_OF_WALK_RECORDS = new LinkedList<>();
        PUBLISHER = Publisher.INSTANCE;
        LOG_TAG = "myLogs";
    }

    public ListOfWalkRecords() {
        PUBLISHER.subscribeForEvent(
                this,
                Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE
        );
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {

        switch(event) {

            case REPO_NEW_WALK_RECORD_OBJ_AVAILABLE:
                if(updatedValue instanceof WalkRecord) {
                    Log.d(LOG_TAG, "ListOfWalkRecords.receiveUpdate() new WalkRecord " + ((WalkRecord) updatedValue).getId());
                    // + Thread.currentThread().getName())
                    addWalkRecord((WalkRecord) updatedValue);
                }
                break;
        }
    }

    public LinkedList<WalkRecord> getList() {
        return LIST_OF_WALK_RECORDS;
    }

    public void clearList() {
        Log.d(LOG_TAG, "ListOfWalkRecords clearList()");
        LIST_OF_WALK_RECORDS.clear();
    }

    void addWalkRecord(WalkRecord walkRecord) {
        WalkRecord record = Objects.requireNonNull(walkRecord);
        LIST_OF_WALK_RECORDS.addFirst(record);
        Log.d(LOG_TAG, "--- listSize " + LIST_OF_WALK_RECORDS.size());

        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                (subscriber) -> subscriber.receiveUpdate(
                        Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                        walkRecord
                )
        );
    }
}