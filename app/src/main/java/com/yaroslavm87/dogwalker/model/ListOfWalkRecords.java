package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.LinkedList;
import java.util.Objects;

public class ListOfWalkRecords implements Observable, Subscriber {

    private final LinkedList<WalkRecord> LIST;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        LIST = new LinkedList<>();
        PUBLISHER = Publisher.INSTANCE;
        LOG_TAG = "myLogs";
    }

    ListOfWalkRecords() {
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
                    // + Thread.currentThread().getName())
                    addWalkRecord((WalkRecord) updatedValue);
                }
                break;
        }
    }

    LinkedList<WalkRecord> getList() {
        return LIST;
    }

    void clearList() {
        LIST.clear();
    }

    void addWalkRecord(WalkRecord walkRecord) {
        WalkRecord record = Objects.requireNonNull(walkRecord);
        LIST.addLast(record);

        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                (subscriber) -> subscriber.receiveUpdate(
                        Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                        walkRecord
                )
        );
    }
}