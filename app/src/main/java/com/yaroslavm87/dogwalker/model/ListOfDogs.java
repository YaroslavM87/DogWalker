package com.yaroslavm87.dogwalker.model;

import android.util.Log;

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
    private final String LOG_TAG;

    {
        this.LOG_TAG = "myLogs";

    }

    public ListOfDogs(ArrayList<Dog> list) {
        this.list = Objects.requireNonNull(list);
    }

    public void setList(ArrayList<Dog> list) {

        this.list = Objects.requireNonNull(list);

        //this.publisher.notifyEventHappened(this, Event.MODEL_LIST_DOGS_CHANGED);
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

    public void updateDog(Dog dog) {

        int indexOfDogToReplace = 0;

        for(Dog d : this.list) {
            Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> " + d.getName() + " - " + dog.getName());


            if(d.getName().equals(dog.getName())) {

                indexOfDogToReplace = this.list.indexOf(d);

                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> index = " + indexOfDogToReplace);

                this.list.set(indexOfDogToReplace, dog);

                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> name = " + this.list.get(indexOfDogToReplace).getName());

                break;
            }
        }

        int finalIndexOfDogToReplace = indexOfDogToReplace;

        this.publisher.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_CHANGED, finalIndexOfDogToReplace)
        );
    }

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
            boolean b = this.list.contains(d);
            Log.d(LOG_TAG, String.valueOf(b));

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
                return (subscriber) -> subscriber.receiveUpdate(event, this.list);

            case MODEL_LIST_DOGS_ITEM_ADDED:
            case MODEL_LIST_DOGS_ITEM_DELETED:
                return (subscriber) -> subscriber.receiveUpdate(event, this.lastDogMovedIndexBuffer);

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

            case REPO_NEW_DOG_OBJ_AVAILABLE:
            case REPO_LIST_DOGS_ITEM_ADDED:

                if(updatedValue instanceof Dog) {

                    this.addDog((Dog) updatedValue);
                }
                break;

            case REPO_LIST_DOGS_ITEM_DELETED:

                if(updatedValue instanceof Integer) {

                    this.deleteDog((int) updatedValue);

                } else if (updatedValue instanceof Dog) {

                    for(Dog d : this.list) {

                        if(d.getName().equals(((Dog) updatedValue).getName())) {

                            this.deleteDog(d);

                            break;
                        }
                    }

                }
                break;

            case REPO_LIST_DOGS_ITEM_CHANGED:

                if(updatedValue instanceof Dog) {

                    this.updateDog((Dog) updatedValue);
                }
                break;
        }

    }
}