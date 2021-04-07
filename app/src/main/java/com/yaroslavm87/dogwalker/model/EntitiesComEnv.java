package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class EntitiesComEnv {

    private final ListOfDogs listOfDogs;
    private Repository repository;
    private Publisher publisher;


    public EntitiesComEnv(Publisher publisher) {

        this.publisher = Objects.requireNonNull(publisher);

        this.listOfDogs = new ListOfDogs(new ArrayList<>());

        this.listOfDogs.setPublisher(publisher);

        try{
            ArrayList<Dog> tmpList = repository.read();

            for(Dog d : tmpList) {

                d.setPublisher(publisher);
            }

            this.listOfDogs.setList(tmpList);

        } catch (Exception e) {
        }
    }

    public ArrayList<Dog> getListOfDogs() {
        return this.listOfDogs.getList();
    }

    public void createDog(String name) {

        Dog newDog = new Dog(name);

        newDog.setPublisher(publisher);

        this.listOfDogs.addDog(newDog);

        repository.add(newDog);
    }

    public Dog getDog(int dogId) {

        return this.listOfDogs.getDog(dogId);
    }

    public void deleteDog(int index){

        Dog dog = this.listOfDogs.deleteDog(index);

        repository.delete(dog);
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public  void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}