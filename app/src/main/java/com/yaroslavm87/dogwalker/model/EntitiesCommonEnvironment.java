package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;
import java.util.Objects;

public enum EntitiesCommonEnvironment implements Model {

    INSTANCE;

    public final ListOfDogs LIST_OF_DOGS;
    private Repository repository;
    private final Publisher PUBLISHER;

    private final long TIME_TO_REST_AFTER_WALK;
    private final String LOG_TAG;

    {
        LIST_OF_DOGS = new ListOfDogs(new ArrayList<>());
        PUBLISHER = Publisher.INSTANCE;
        TIME_TO_REST_AFTER_WALK = 0L;
        LOG_TAG = "myLogs";
    }

    EntitiesCommonEnvironment() {
        Log.d(LOG_TAG, "EntitiesComEnv() constructor call");
    }

    //TODO: разделить на два метода - проверка списка и запрос в БД если пустой, и возврат списка
    public ArrayList<Dog> getListOfDogs() {
//
//        if(LIST_OF_DOGS.getList().isEmpty()) {
//
//            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");
//
//            startRepoLoadingListOfDogs();
//        }

        return LIST_OF_DOGS.getList();
    }

    public void createDog(String name) {

        Log.d(LOG_TAG, "EntitiesComEnv.createDog() call");

        Dog newDog = new Dog();
        newDog.setName(name);

        if(LIST_OF_DOGS.getList().contains(newDog)) {

            Log.d(LOG_TAG, "ListOfDogs contains dog " + name);

            //TODO: add reference to res with text message
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_ERROR,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_ERROR,
                            "error message"
                    )
            );

            return;
        }

        new Thread(() -> {

            try{

                Log.d(LOG_TAG, "EntitiesComEnv.createDog().repository.add(newDog) call");
                repository.add(newDog);

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.createDog() EXCEPTION: " + e);
            }
        }).start();
    }

    public void walkDog(int index) {

        Log.d(LOG_TAG, "EntitiesComEnv.walkDog() call");

        Dog updatedDog = Dog.getCopy(LIST_OF_DOGS.getDog(index));

        long currentTime = System.currentTimeMillis();
        long timeDelta = currentTime - updatedDog.getLastTimeWalk();

        if(timeDelta == 0 || timeDelta >= TIME_TO_REST_AFTER_WALK) {

            updatedDog.setLastTimeWalk(currentTime);

            new Thread(() -> {

                try{

                    Log.d(LOG_TAG, "EntitiesComEnv.walkDog().repository.update(updatedDog) call");
                    repository.update(updatedDog);

                } catch (Exception e) {

                    Log.d(LOG_TAG, "EntitiesComEnv.createDog() EXCEPTION: " + e);
                }
            }).start();

        } else {

            long timeUntilNextWalk = TIME_TO_REST_AFTER_WALK - timeDelta;

            //TODO: add reference to res with text message
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_ERROR,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_ERROR,
                            timeUntilNextWalk
                    )
            );
        }
    }

    public void deleteDog(int index) {

        Log.d(LOG_TAG, "EntitiesComEnv.deleteDog() call");

        new Thread(() -> {

            try{

                this.repository.delete(LIST_OF_DOGS.getDog(index));

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.deleteDog() EXCEPTION: " + e);
            }
        }).start();
    }

    void setRepository(Repository repository) {

        Log.d(LOG_TAG, "EntitiesComEnv.setRepository() call");

        this.repository = repository;

    }

    void startRepoLoadingListOfDogs() {

        new Thread(() -> {

            try{

                repository.read();

                Log.d(LOG_TAG, "EntitiesComEnv.startRepoLoadingListOfDogs() call");

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.startRepoLoadingListOfDogs() EXCEPTION: " + e);

            }

        }).start();
    }

    void subscribeModelForEvents(Event... events) {

        Log.d(LOG_TAG, "EntitiesComEnv.subscribeModelForEvents() call");

        PUBLISHER.subscribeForEvent(LIST_OF_DOGS, Objects.requireNonNull(events));
    }
}