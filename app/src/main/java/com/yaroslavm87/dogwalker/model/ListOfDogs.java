package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListOfDogs implements Observable {

    private ArrayList<Dog> list;
    private Publisher publisher;
    private Dog dogBuffer;

    public ListOfDogs(ArrayList<Dog> list) {
        this.list = Objects.requireNonNull(list);
    }

    public void setList(ArrayList<Dog> list) {

        this.list = Objects.requireNonNull(list);

        this.publisher.notifyEventHappened(this, Event.LIST_DOGS_CHANGED);
    }

    ArrayList<Dog> getList() {
        return this.list;
    }

    void addDog(Dog dog) {

        this.list.add(Objects.requireNonNull(dog));

        this.dogBuffer = Objects.requireNonNull(dog);

        this.publisher.notifyEventHappened(this, Event.LIST_DOGS_ITEM_ADDED);
    }

//    public void deleteDog(Dog dog) {
//
//        this.list.remove(Objects.requireNonNull(dog));
//
//        this.dogBuffer = Objects.requireNonNull(dog);
//
//        this.publisher.notifyEventHappened(this, Event.LIST_DOGS_ITEM_DELETED);
//    }

    Dog deleteDog(int index) {

        if(index >= 0 & index < list.size()) {
            return null;
        }

        this.publisher.notifyEventHappened(this, Event.LIST_DOGS_ITEM_DELETED);

        return this.list.remove(index);
    }

    Dog getDog(int dogId) {

        if(this.list.size() > dogId) {

            return list.get(dogId);

        } else return null;
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

            case LIST_DOGS_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(this.list);

            case LIST_DOGS_ITEM_ADDED:
            case LIST_DOGS_ITEM_DELETED:
                return (observable, subscriber) -> {

                    subscriber.receiveUpdate(this.dogBuffer);

                    this.dogBuffer = null;
                };

            default:
                return null;
        }
    }
}