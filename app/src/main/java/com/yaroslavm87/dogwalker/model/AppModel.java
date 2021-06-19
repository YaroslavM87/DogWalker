package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.RepoOperations;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;

public enum AppModel implements Model {

    INSTANCE;

    public final ListOfDogs LIST_OF_DOGS;
    public final ListOfWalkRecords LIST_OF_WALK_RECORDS_FOR_DOG;
    private Repository repository;
    private final Publisher PUBLISHER;
    private final long TIME_TO_REST_AFTER_WALK;
    private final String LOG_TAG;

    private int count = 0;

    {
        LIST_OF_DOGS = new ListOfDogs();
        LIST_OF_WALK_RECORDS_FOR_DOG = new ListOfWalkRecords();
        PUBLISHER = Publisher.INSTANCE;
        TIME_TO_REST_AFTER_WALK = 0L;
        LOG_TAG = "myLogs";
    }

    AppModel() {
        Log.d(LOG_TAG, "Model() constructor call");
    }

    //TODO: разделить на два метода - проверка списка и запрос в БД если пустой, и возврат списка
    public ArrayList<Dog> getRefOfListDogs() {
//
//        if(LIST_OF_DOGS.getList().isEmpty()) {
//
//            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");
//
//            startRepoLoadingListOfDogs();
//        }
        LIST_OF_DOGS.getList().clear();
        startRepoLoadingListOfDogs();
        return LIST_OF_DOGS.getList();
    }

    public ArrayList<WalkRecord> getRefOfListWalkRecordsForDog(Dog dog) {
//
//        if(LIST_OF_DOGS.getList().isEmpty()) {
//
//            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");
//
//            startRepoLoadingListOfDogs();
//        }
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "----------- GET REF OF WALKS LIST -----------");
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "Model.getRefOfListWalkRecordsForDog() call");

        //if(LIST_OF_WALK_RECORDS_FOR_DOG.getList().size() != 0)

        // TODO: check if duplicating method can be removed from rvadapter
        LIST_OF_WALK_RECORDS_FOR_DOG.clearList();

        startRepoLoadingListOfWalkRecordsForDog(dog);
        return LIST_OF_WALK_RECORDS_FOR_DOG.getList();
    }

    public void createDog(String name) {
        Log.d(LOG_TAG, "Model.createDog() call");
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
                Log.d(LOG_TAG, "Model.createDog().repository.add(newDog) call");
                repository.add(RepoOperations.CREATE_DOG, newDog);

            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.createDog() EXCEPTION: " + e);
            }
        }).start();
    }

    public void walkDog(int index) {
        Log.d(LOG_TAG, "Model.walkDog() call");

        Dog updatedDog = Dog.getCopy(LIST_OF_DOGS.getDog(index));
        long currentTime = System.currentTimeMillis();
        long timeDelta = currentTime - updatedDog.getLastTimeWalk();

        if(timeDelta == 0 || timeDelta >= TIME_TO_REST_AFTER_WALK) {
            updatedDog.setLastTimeWalk(currentTime);

            new Thread(() -> {
                try{
                    Log.d(LOG_TAG, "Model.walkDog().repository.update(updatedDog) call");
                    repository.update(RepoOperations.CREATE_RECORD_OF_DOG_WALK, updatedDog);

                } catch (Exception e) {
                    Log.d(LOG_TAG, "Model.createDog() EXCEPTION: " + e);
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
        Log.d(LOG_TAG, "Model.deleteDog() call");
        new Thread(() -> {
            try{
                this.repository.delete(RepoOperations.DELETE_DOG, LIST_OF_DOGS.getDog(index));

            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.deleteDog() EXCEPTION: " + e);
            }
        }).start();
    }

    void setRepository(Repository repository) {
        Log.d(LOG_TAG, "Model.setRepository() call");
        this.repository = repository;
    }

    void startRepoLoadingListOfDogs() {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.startRepoLoadingListOfDogs() call");
                repository.read(RepoOperations.READ_LIST_OF_DOGS, null);

            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.startRepoLoadingListOfDogs() EXCEPTION: " + e);
            }
        }).start();
    }

    void startRepoLoadingListOfWalkRecordsForDog(Dog dog) {
        //if (count++ < 1) {
            new Thread(() -> {
                try {
                    Log.d(LOG_TAG, "Model.startRepoLoadingListOfWalkRecordsForDog() call");
                    repository.read(RepoOperations.READ_LIST_OF_WALKS_FOR_DOG, dog);

                } catch (Exception e) {
                    Log.d(LOG_TAG, "Model.startRepoLoadingListOfWalkRecordsForDog() EXCEPTION: " + e);
                }
            }).start();
       // }
    }

//    void subscribeModelForEvents(Event... events) {
//        Log.d(LOG_TAG, "Model.subscribeModelForEvents() call");
//        PUBLISHER.subscribeForEvent(LIST_OF_DOGS, Objects.requireNonNull(events));
//    }
}