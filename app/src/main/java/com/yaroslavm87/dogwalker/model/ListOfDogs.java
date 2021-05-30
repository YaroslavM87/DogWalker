package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.Objects;

class ListOfDogs implements Observable, Subscriber {

    private final ArrayList<Dog> list;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        PUBLISHER = Publisher.INSTANCE;
        this.LOG_TAG = "myLogs";
    }

    public ListOfDogs(ArrayList<Dog> list) {
        this.list = Objects.requireNonNull(list);
    }

    ArrayList<Dog> getList() {
        return list;
    }

    void addDog(Dog dog) {

        if(list.add(Objects.requireNonNull(dog))) {

            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_LIST_DOGS_ITEM_ADDED,
                    (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_ADDED, list.size() - 1)
            );
        }
    }

    public void updateDog(Dog dog) {

        int indexOfDogToReplace = 0;

        for(Dog d : list) {
            Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> " + d.getName() + " - " + dog.getName());

            if(d.getName().equals(dog.getName())) {

                indexOfDogToReplace = list.indexOf(d);

                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> index = " + indexOfDogToReplace);

                list.set(indexOfDogToReplace, dog);

                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> name = " + list.get(indexOfDogToReplace).getName());
                break;
            }
        }

        int finalIndexOfDogToReplace = indexOfDogToReplace;

        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_CHANGED, finalIndexOfDogToReplace)
        );
    }

    void deleteDog(int index) {

        if(index < 0 || index >= list.size()) {
            return;
        }

        list.remove(index);

        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_DELETED, index)
        );
    }

    void deleteDog(Dog d) {

        if (d != null) {

            Log.d(LOG_TAG, String.valueOf(list.contains(d)));

            deleteDog(list.indexOf(d));
        }
    }

    Dog getDog(int dogId) {

        if(list.size() > dogId) {

            return list.get(dogId);

        } else return null;
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {

        switch(event) {

            case REPO_NEW_DOG_OBJ_AVAILABLE:

                if(updatedValue instanceof Dog) {

                    addDog((Dog) updatedValue);
                }
                break;

            case REPO_LIST_DOGS_ITEM_DELETED:

                if(updatedValue instanceof Integer) {

                    deleteDog((int) updatedValue);

                } else if (updatedValue instanceof Dog) {

                    for(Dog d : list) {

                        if(d.getName().equals(((Dog) updatedValue).getName())) {

                            deleteDog(d);
                            break;
                        }
                    }

                }
                break;

            case REPO_LIST_DOGS_ITEM_CHANGED:

                if(updatedValue instanceof Dog) {

                    updateDog((Dog) updatedValue);
                }
                break;
        }
    }
}