package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class ListOfShelters implements Subscriber {

    private ArrayList<Shelter> LIST;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        LIST = new ArrayList<>();
        PUBLISHER = Publisher.INSTANCE;
        LOG_TAG = "myLogs";
    }

    ListOfShelters() {
        PUBLISHER.subscribeForEvent(
                this,
                Event.REPO_NEW_SHELTER_OBJ_AVAILABLE
        );
    }

    public ArrayList<Shelter> getList() {
        return LIST;
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {
        switch(event) {

            case REPO_NEW_SHELTER_OBJ_AVAILABLE:
                if(updatedValue instanceof Shelter) {
                    addToListAndNotify((Shelter) updatedValue);
                }
                break;
        }
    }

    private void addToListAndNotify(Shelter shelter) {
        LIST.add(shelter);

        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_SHELTER_ITEM_ADDED,
                (subscriber) -> subscriber.receiveUpdate(
                        Event.MODEL_LIST_SHELTER_ITEM_ADDED,
                        LIST.size() - 1
                )
        );
    }
}