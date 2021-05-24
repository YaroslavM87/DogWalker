package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.Objects;

public class Dog implements Observable {

    private int _id;
    private String name;
    private int imageResId;
    private long lastTimeWalk;
    private transient Publisher publisher;

    public Dog() {
    }

    public Dog(String name) {
        this._id = -1;
        this.name = Objects.requireNonNull(name);
        this.imageResId = -1;
        this.lastTimeWalk = -1L;
    }

    public Dog(int id, String name, int imageResId, int lastTimeWalk) {
        this._id = id;
        this.name = Objects.requireNonNull(name);
        this.imageResId = imageResId;
        this.lastTimeWalk = lastTimeWalk;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //publisher.notifyEventHappened(this, Event.DOG_NAME_CHANGED);
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
        //publisher.notifyEventHappened(this, Event.DOG_IMAGE_RES_ID_CHANGED);
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
    public PassValToSubscriber prepareCommandForUpdate(Event event) {

        return getAppropriateCommand(event);
    }

    private PassValToSubscriber getAppropriateCommand(Event event) {

        switch (event) {

            case DOG_NAME_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.name);

            case DOG_IMAGE_RES_ID_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.imageResId);

            default:
                return null;
        }
    }
}