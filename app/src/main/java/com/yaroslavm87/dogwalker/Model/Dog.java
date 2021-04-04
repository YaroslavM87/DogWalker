package com.yaroslavm87.dogwalker.Model;

import com.yaroslavm87.dogwalker.Notifications.CmdPassValToSubscriber;
import com.yaroslavm87.dogwalker.Notifications.Event;
import com.yaroslavm87.dogwalker.Notifications.Observable;
import com.yaroslavm87.dogwalker.Notifications.Publisher;

import java.util.Objects;

public class Dog implements Observable {

    private final int _id;
    private String name;
    private int imageResId;
    private Publisher publisher;

    public Dog(int id, String name) {
        this._id = id;
        this.name = Objects.requireNonNull(name);
    }

    public int getId() {
        return _id;
    }

    public void setName(String name) {
        this.name = name;
        publisher.notifyEventHappened(this, Event.DOG_NAME_CHANGED);
    }

    public String getName() {
        return name;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
        publisher.notifyEventHappened(this, Event.DOG_IMAGE_RES_ID_CHANGED);

    }

    public int getImageResId() {
        return imageResId;
    }

    @Override
    public CmdPassValToSubscriber prepareCommandForUpdate(Event event) {

        return getAppropriateCommand(event);
    }

    private CmdPassValToSubscriber getAppropriateCommand(Event event) {

        switch (event) {

            case DOG_NAME_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(this.name);

            case DOG_IMAGE_RES_ID_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(this.imageResId);

            default:
                return null;
        }
    }
}