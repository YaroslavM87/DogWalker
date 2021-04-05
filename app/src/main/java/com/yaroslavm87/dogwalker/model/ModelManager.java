package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ModelManager<T extends List<Dog>> {

    private final ListOfDogs listOfDogs;
    private Repository<T> repository;
    private Publisher publisher;


    public ModelManager(Publisher publisher) {

        this.publisher = Objects.requireNonNull(publisher);

        this.listOfDogs = new ListOfDogs(new LinkedList<>());

        this.listOfDogs.setPublisher(publisher);

        try{
            List<Dog> tmpList = repository.read();

            for(Dog d : tmpList) {

                d.setPublisher(publisher);
            }

            this.listOfDogs.setList(tmpList);

        } catch (Exception e) {
        }
    }

    public List<Dog> getListOfDogs() {
        return this.listOfDogs.getList();
    }

    public void createDog(String name) {

        Dog newDog = new Dog(
                this.listOfDogs.getList().size(),
                name
        );

        newDog.setPublisher(publisher);

        this.listOfDogs.addDog(newDog);

        repository.update();
    }

    public Dog getDog(int dogId) {

        return this.listOfDogs.getDog(dogId);
    }

    public void deleteDog(int index){

        this.listOfDogs.deleteDog(index);

        repository.delete();
    }

    public void setRepository(Repository<T> repository) {
        this.repository = repository;
    }

    public  void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}