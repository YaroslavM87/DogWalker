package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;
import java.util.Objects;

public enum EntitiesCommonEnvironment implements Model {

    INSTANCE;

    private final ListOfDogs LIST_OF_DOGS;
    private Repository repository;
    private final Publisher publisher;
    private final long TIME_TO_REST_AFTER_WALK;

    private final String LOG_TAG = "myLogs";

    {
        this.LIST_OF_DOGS = new ListOfDogs(new ArrayList<>());
        this.publisher = Publisher.INSTANCE;
        this.LIST_OF_DOGS.setPublisher(this.publisher);
        TIME_TO_REST_AFTER_WALK = 0L;
    }

    EntitiesCommonEnvironment() {

        Log.d(LOG_TAG, "EntitiesComEnv() constructor call");
    }

    public ArrayList<Dog> getListOfDogs() {

        if(this.LIST_OF_DOGS.getList().isEmpty()) {

            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");

            loadListOfDogsFromRepo();
        }

        return this.LIST_OF_DOGS.getList();
    }

    public void createDog(String name) {

        Log.d(LOG_TAG, "EntitiesComEnv.createDog() call");

        Dog newDog = new Dog();
        newDog.setName(name);

        //newDog.setPublisher(this.publisher);

        //newDog.setId(this.listOfDogs.getList().size() + 1);

        //this.listOfDogs.addDog(newDog);

        new Thread(() -> {

            try{

                this.repository.add(newDog);
                Log.d(LOG_TAG, "EntitiesComEnv.createDog().repository.add(newDog) call");

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.createDog() EXCEPTION: " + e);
            }
        }).start();
    }

//    public Dog getDog(int index) {
//
//        Log.d(LOG_TAG, "EntitiesComEnv.getDog() call");
//
//        return this.LIST_OF_DOGS.getDog(index);
//    }

    public void walkDog(int index) {

        Log.d(LOG_TAG, "EntitiesComEnv.walkDog() call");

        Dog updatedDog = Dog.getCopy(this.LIST_OF_DOGS.getDog(index));

        long currentTime = System.currentTimeMillis();
        long timeDelta = currentTime - updatedDog.getLastTimeWalk();

        if(timeDelta == 0 || timeDelta >= TIME_TO_REST_AFTER_WALK) {

            updatedDog.setLastTimeWalk(currentTime);

            new Thread(() -> {

                try{
                    Log.d(LOG_TAG, "EntitiesComEnv.walkDog().repository.update(updatedDog) call");

                    this.repository.update(updatedDog);

                } catch (Exception e) {

                    Log.d(LOG_TAG, "EntitiesComEnv.createDog() EXCEPTION: " + e);
                }
            }).start();

        } else {

            long timeUntilNextWalk = TIME_TO_REST_AFTER_WALK - timeDelta;

            publisher.makeSubscribersReceiveUpdate(
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

        //Dog dog = this.listOfDogs.deleteDog(index);

        new Thread(() -> {

            try{

                this.repository.delete(this.LIST_OF_DOGS.getDog(index));

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.deleteDog() EXCEPTION: " + e);
            }
        }).start();
    }

//    EntitiesCommonEnvironment setListOfDogs(ArrayList<Dog> listOfDogs) {
//
//        Log.d(LOG_TAG, "EntitiesComEnv.setListOfDogs() call");
//
//        this.LIST_OF_DOGS.setList(listOfDogs);
//
//        return this;
//    }

    void setRepository(Repository repository) {

        Log.d(LOG_TAG, "EntitiesComEnv.setRepository() call");

        this.repository = repository;

    }

//    EntitiesComEnv setPublisher(Publisher publisher) {
//
//        Log.d(LOG_TAG, "EntitiesComEnv.setPublisher() call");
//
//        this.publisher = publisher;
//
//        return this;
//    }

    void loadListOfDogsFromRepo() {

        new Thread(() -> {

            try{

                Log.d(LOG_TAG, "EntitiesComEnv.loadListOfDogsFromRepo() call");

                repository.read();

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.loadListOfDogsFromRepo() EXCEPTION: " + e);

            }

        }).start();

    }

    void subscribeModelForEvents(Event... events) {

        Log.d(LOG_TAG, "EntitiesComEnv.subscribeModelForEvents() call");

        this.publisher.subscribeForEvent(this.LIST_OF_DOGS, Objects.requireNonNull(events));
    }


}