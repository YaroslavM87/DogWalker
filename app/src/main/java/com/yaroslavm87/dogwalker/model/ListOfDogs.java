package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ListOfDogs implements Observable, Subscriber {

    public final ArrayList<Dog> LIST_OF_DOGS;
    private final Publisher PUBLISHER;
    private final String LOG_TAG;

    {
        LIST_OF_DOGS = new ArrayList<>();
        PUBLISHER = Publisher.INSTANCE;
        this.LOG_TAG = "myLogs";
    }

    public ListOfDogs() {
        PUBLISHER.subscribeForEvent(
                this,
                Event.REPO_NEW_DOG_OBJ_AVAILABLE,
                Event.REPO_LIST_DOGS_ITEM_CHANGED,
                Event.REPO_LIST_DOGS_ITEM_DELETED
        );
    }

    public ArrayList<Dog> getList() {
        return LIST_OF_DOGS;
    }

    void addDog(Dog dog) {
        if(LIST_OF_DOGS.add(Objects.requireNonNull(dog))) {
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_LIST_DOGS_ITEM_ADDED,
                    (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_ADDED, LIST_OF_DOGS.size() - 1)
            );
        }
    }

    public void updateDog(Dog updatedDog) {
        Optional<Dog> optDog = LIST_OF_DOGS
                .stream()
                .filter(d -> d.getName().equals(updatedDog.getName()))
                .findAny();

        if(optDog.isPresent() && optDog.get().getId().equals(updatedDog.getId())) {
            int indexOfDogToReplace = LIST_OF_DOGS.indexOf(optDog.get());

            LIST_OF_DOGS.set(indexOfDogToReplace, updatedDog);

            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                            indexOfDogToReplace
                    )
            );
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                            updatedDog
                    )
            );
        }

//        for(Dog d : LIST_OF_DOGS) {
//
//            if(d.getName().equals(updatedDog.getName())) {
//                int indexOfDogToReplace = LIST_OF_DOGS.indexOf(d);
//
//                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> index = " + indexOfDogToReplace);
//                LIST_OF_DOGS.set(indexOfDogToReplace, updatedDog);
//
//                Log.d(LOG_TAG, "ListOfDogs.updateDog() call -> name = " + LIST_OF_DOGS.get(indexOfDogToReplace).getName());
//                PUBLISHER.makeSubscribersReceiveUpdate(
//                        Event.MODEL_LIST_DOGS_ITEM_CHANGED,
//                        (subscriber) -> subscriber.receiveUpdate(
//                                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
//                                indexOfDogToReplace
//                        )
//                );
//                PUBLISHER.makeSubscribersReceiveUpdate(
//                        Event.MODEL_LIST_DOGS_ITEM_CHANGED,
//                        (subscriber) -> subscriber.receiveUpdate(
//                                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
//                                updatedDog
//                        )
//                );
//                break;
//            }
//        }
    }

    void deleteDog(int index) {
        if(index < 0 || index >= LIST_OF_DOGS.size()) {
            return;
        }
        LIST_OF_DOGS.remove(index);
        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                (subscriber) -> subscriber.receiveUpdate(Event.MODEL_LIST_DOGS_ITEM_DELETED, index)
        );
    }

    void deleteDog(Dog d) {
        if (d != null) {
            Log.d(LOG_TAG, String.valueOf(LIST_OF_DOGS.contains(d)));
            deleteDog(LIST_OF_DOGS.indexOf(d));
        }
    }

    Dog getDog(int index) {
        if(LIST_OF_DOGS.size() > index) {
            return LIST_OF_DOGS.get(index);
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

            case REPO_LIST_DOGS_ITEM_CHANGED:
                if(updatedValue instanceof Dog) {
                    updateDog((Dog) updatedValue);
                }
                break;

            case REPO_LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Integer) {
                    deleteDog((int) updatedValue);

                } else if (updatedValue instanceof Dog) {
                    for(Dog d : LIST_OF_DOGS) {
                        if(d.getName().equals(((Dog) updatedValue).getName())) {
                            deleteDog(d);
                            break;
                        }
                    }
                }
                break;
        }
    }
}