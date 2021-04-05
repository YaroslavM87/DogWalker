package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.notifications.CmdPassValToSubscriber;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.Objects;

public class Dog implements Observable {

    private final int _id;
    private String name;
    private int imageResId;
    private long lastTimeWalk;
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

    public long getLastTimeWalk() {
        return lastTimeWalk;
    }

    public void setLastTimeWalk(long lastTimeWalk) {
        this.lastTimeWalk = lastTimeWalk;
    }

    void setPublisher(Publisher publisher) {
        this.publisher = publisher;
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