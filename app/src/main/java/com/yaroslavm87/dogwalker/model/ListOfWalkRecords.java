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

    //private final Map<String, ArrayList<WalkRecord>> LISTS_OF_WALK_RECORDS;
    private final ArrayList<WalkRecord> LIST_OF_WALK_RECORDS;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        LIST_OF_WALK_RECORDS = new ArrayList<>();
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

    public ArrayList<WalkRecord> getList() {
        Log.d(LOG_TAG, "ListOfWalkRecords.getList() | listSize=" + LIST_OF_WALK_RECORDS.size());
        return LIST_OF_WALK_RECORDS;
    }

    public void clearList() {
        Log.d(LOG_TAG, "listSize=" + LIST_OF_WALK_RECORDS.size() + " | ListOfWalkRecords.clearList()");
        LIST_OF_WALK_RECORDS.clear();
    }

//    public ArrayList<WalkRecord> getList(String dogIdAsKey) {
//        return getAppropriateListOfWalkRecords(dogIdAsKey);
//    }

    void addWalkRecord(WalkRecord walkRecord) {
        if(LIST_OF_WALK_RECORDS.add(Objects.requireNonNull(walkRecord))) {
            Log.d(LOG_TAG, "--- listSize " + LIST_OF_WALK_RECORDS.size());
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                            LIST_OF_WALK_RECORDS.size() - 1
                    )
            );
        }
//        ArrayList<WalkRecord> list = getAppropriateListOfWalkRecords(Objects.requireNonNull(walkRecord).getDogId());
//
//        if(list.add(Objects.requireNonNull(walkRecord))) {
//            Log.d(LOG_TAG, "ListOfWalkRecords.addWalkRecord().listSize " + list.size());
//            PUBLISHER.makeSubscribersReceiveUpdate(
//                    Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
//                    (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED, list.size() - 1)
//            );
//        }
    }

//    void deleteWalkRecord(int index) {
//        if(index < 0 || index >= list.size()) {
//            return;
//        }
//        list.remove(index);
//        PUBLISHER.makeSubscribersReceiveUpdate(
//                Event.MODEL_LIST_WALK_RECORDS_ITEM_DELETED,
//                (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_WALK_RECORDS_ITEM_DELETED, index)
//        );
//    }
//
//    void deleteWalkRecord(WalkRecord wr) {
//        if (wr != null) {
//            Log.d(LOG_TAG, String.valueOf(list.contains(wr)));
//            deleteWalkRecord(list.indexOf(wr));
//        }
//    }

//    WalkRecord getWalkRecord(int index) {
//        if(list.size() > index) {
//            return list.get(index);
//        } else return null;
//    }



//    private ArrayList<WalkRecord> getAppropriateListOfWalkRecords(String dogIdAsKey) {
//
//        if (LISTS_OF_WALK_RECORDS.containsKey(dogIdAsKey)) {
//            return LISTS_OF_WALK_RECORDS.get(dogIdAsKey);
//
//        } else {
//            ArrayList<WalkRecord> list = new ArrayList<>();
//            LISTS_OF_WALK_RECORDS.put(dogIdAsKey, list);
//            return list;
//        }
//    }
}