package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.Repository;
import com.yaroslavm87.dogwalker.repository.SQLiteDbAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum EntitiesCommonEnvironment implements Model {

    INSTANCE;

    private final ListOfDogs listOfDogs;
    private Repository<ArrayList<Dog>> repository;
    private final Publisher publisher;
    private final String LOG_TAG = "myLogs";

    {
        this.listOfDogs = new ListOfDogs(new ArrayList<>());
        this.publisher = Publisher.INSTANCE;
        this.listOfDogs.setPublisher(this.publisher);
    }

    EntitiesCommonEnvironment() {

        Log.d(LOG_TAG, "EntitiesComEnv() constructor call");

    }

    public ArrayList<Dog> getListOfDogs() {

        if(this.listOfDogs.getList().isEmpty()) {

            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");

            loadListOfDogsFromRepo();
        }

        return this.listOfDogs.getList();
    }

    public void createDog(String name) {

        Log.d(LOG_TAG, "EntitiesComEnv.createDog() call");

        Dog newDog = new Dog(name);

        newDog.setPublisher(this.publisher);

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

    public Dog getDog(int index) {

        Log.d(LOG_TAG, "EntitiesComEnv.getDog() call");

        return this.listOfDogs.getDog(index);
    }

    public void deleteDog(int index) {

        Log.d(LOG_TAG, "EntitiesComEnv.deleteDog() call");

        //Dog dog = this.listOfDogs.deleteDog(index);

        new Thread(() -> {

            try{

                this.repository.delete(this.listOfDogs.getDog(index));

            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.deleteDog() EXCEPTION: " + e);
            }
        }).start();
    }

    EntitiesCommonEnvironment setListOfDogs(ArrayList<Dog> listOfDogs) {

        Log.d(LOG_TAG, "EntitiesComEnv.setListOfDogs() call");

        this.listOfDogs.setList(listOfDogs);

        return this;
    }

    void setRepository(Repository<ArrayList<Dog>> repository) {

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

                ArrayList<Dog> tmpList = repository.read();

                for(Dog d : tmpList) {

                    d.setPublisher(publisher);
                }

                this.listOfDogs.setList(tmpList);

                tmpList = null;


            } catch (Exception e) {

                Log.d(LOG_TAG, "EntitiesComEnv.loadListOfDogsFromRepo() EXCEPTION: " + e);

            }

        }).start();

    }

    void subscribeModelForEvents(Event... events) {

        Log.d(LOG_TAG, "EntitiesComEnv.subscribeModelForEvents() call");

        this.publisher.subscribeForEvent(this.listOfDogs, Objects.requireNonNull(events));
    }


}