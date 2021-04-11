package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.DatabaseAdapter;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public enum EntitiesComEnv {

    INSTANCE;

    private final ListOfDogs listOfDogs;
    private DatabaseAdapter repository;
    private Publisher publisher;
    private final String LOG_TAG = "myLogs";


    {
        this.listOfDogs = new ListOfDogs(new ArrayList<>());
        this.publisher = Publisher.INSTANCE;
    }

    EntitiesComEnv() {

        Log.d(LOG_TAG, "EntitiesComEnv() constructor call");

        this.listOfDogs.setPublisher(publisher);
    }

    public ArrayList<Dog> getListOfDogs() {

        if(this.listOfDogs.getList().isEmpty()) {

            Log.d(LOG_TAG, "EntitiesComEnv.getListOfDogs() call");

            loadListOfDogsFromRepo();
        }

        return this.listOfDogs.getList();
    }

    public void createDog(String name) {

        Dog newDog = new Dog(name);

        newDog.setPublisher(this.publisher);

        this.listOfDogs.addDog(newDog);

        this.repository.add(newDog);
    }

    public Dog getDog(int dogId) {

        return this.listOfDogs.getDog(dogId);
    }

    public void deleteDog(int index){

        Dog dog = this.listOfDogs.deleteDog(index);

        this.repository.delete(dog);
    }

    public void setRepository(DatabaseAdapter repository) {

        Log.d(LOG_TAG, "EntitiesComEnv.setRepository() call");

        this.repository = repository;
    }

    public  void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void loadListOfDogsFromRepo() {

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
    }
}