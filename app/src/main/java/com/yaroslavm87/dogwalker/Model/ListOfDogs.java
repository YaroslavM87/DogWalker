package com.yaroslavm87.dogwalker.Model;

import com.yaroslavm87.dogwalker.Notifications.CmdPassValToSubscriber;
import com.yaroslavm87.dogwalker.Notifications.Event;
import com.yaroslavm87.dogwalker.Notifications.Observable;
import com.yaroslavm87.dogwalker.Notifications.Publisher;

import java.util.List;
import java.util.Objects;

class ListOfDogs implements Observable {

    private List<Dog> list;
    private Publisher publisher;
    private Dog dogBuffer;

    ListOfDogs(List<Dog> list) {
        this.list = Objects.requireNonNull(list);
    }

    void setList(List<Dog> list) {

        this.list = Objects.requireNonNull(list);

        publisher.notifyEventHappened(this, Event.LIST_DOGS_CHANGED);
    }

    void addDog(Dog d) {

        this.list.add(Objects.requireNonNull(d));

        this.dogBuffer = Objects.requireNonNull(d);

        publisher.notifyEventHappened(this, Event.LIST_DOGS_NEW_ADDED);
    }

    Dog getDog(int dogId) {

        if(this.list.size() > dogId) {

            return list.get(dogId);

        } else return null;
    }

    @Override
    public CmdPassValToSubscriber prepareCommandForUpdate(Event event) {

        return getAppropriateCommand(event);
    }

    private CmdPassValToSubscriber getAppropriateCommand(Event event) {

        switch (event) {

            case LIST_DOGS_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(this.list);

            case LIST_DOGS_NEW_ADDED:
                return (observable, subscriber) -> {

                    subscriber.receiveUpdate(this.dogBuffer);

                    this.dogBuffer = null;
                };

            default:
                return null;
        }
    }
}