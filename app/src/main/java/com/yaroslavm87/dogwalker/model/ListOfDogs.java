package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.Objects;

class ListOfDogs implements Observable, Subscriber {

    private ArrayList<Dog> list;
    private Publisher publisher;
    private int lastDogMovedIndexBuffer;

    public ListOfDogs(ArrayList<Dog> list) {
        this.list = Objects.requireNonNull(list);
    }

    public void setList(ArrayList<Dog> list) {

        this.list = Objects.requireNonNull(list);

        this.publisher.notifyEventHappened(this, Event.MODEL_LIST_DOGS_CHANGED);
    }

    ArrayList<Dog> getList() {
        return this.list;
    }

    void addDog(Dog dog) {

        if(this.list.add(Objects.requireNonNull(dog))) {

            this.lastDogMovedIndexBuffer = this.list.size() - 1;

            this.publisher.notifyEventHappened(
                    this,
                    Event.MODEL_LIST_DOGS_ITEM_ADDED
            );
        }

        //LIST_DOGS_CHANGED
        //LIST_DOGS_ITEM_ADDED
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

        if(index < 0 || index >= list.size()) {
            return null;
        }

        this.lastDogMovedIndexBuffer = index;

        this.publisher.notifyEventHappened(this, Event.MODEL_LIST_DOGS_ITEM_DELETED);

        return this.list.remove(index);
    }

    int deleteDog(Dog d) {

        if (d != null) {

            int index = this.list.indexOf(d);

            this.deleteDog(index);

            return index;

        } else return -1;

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

            case MODEL_LIST_DOGS_CHANGED:
                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.list);

            case MODEL_LIST_DOGS_ITEM_ADDED:
            case MODEL_LIST_DOGS_ITEM_DELETED:
                return (observable, subscriber) -> subscriber.receiveUpdate(event, Integer.valueOf (this.lastDogMovedIndexBuffer));

            default:
                return null;
        }
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {

        //ArrayList<Dog> list;
        //Dog dog;
        //updatedValue.getClass().getTypeParameters();

        switch(event) {

            //TODO: скорее весго не нужен; убрать
//            case REPO_LIST_DOGS_CHANGED:
//                if(updatedValue instanceof ArrayList) {
//
//                    if(((ArrayList) updatedValue).get(0) instanceof Dog) {
//
//                        this.setList((ArrayList<Dog>) updatedValue);
//                    }
//                }
//                break;

            case REPO_LIST_DOGS_ITEM_ADDED:

                if(updatedValue instanceof Dog) {

                    this.addDog((Dog) updatedValue);
                }
                break;

            case REPO_LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Integer) {

                    this.deleteDog((int) updatedValue);

                } else if (updatedValue instanceof Dog) {

                    this.deleteDog((Dog) updatedValue);
                }

                break;
        }

    }
}