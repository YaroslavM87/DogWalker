package com.yaroslavm87.dogwalker.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.EntitiesComEnv;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends androidx.lifecycle.ViewModel implements Subscriber {

    private EntitiesComEnv model;
    private final Publisher publisher;
    private MutableLiveData<ArrayList<Dog>> listOfDogsLive;
    private MutableLiveData<Dog> addedDogLive;
    private MutableLiveData<Integer> deletedDogId;

    {
        this.model = EntitiesComEnv.INSTANCE;
        this.publisher = Publisher.INSTANCE;
        this.listOfDogsLive = new MutableLiveData<>();
    }

    public MyViewModel() {

        this.publisher.subscribeForEvent(
                this,
                Event.LIST_DOGS_CHANGED,
                Event.LIST_DOGS_ITEM_ADDED,
                Event.LIST_DOGS_ITEM_DELETED
        );

        listOfDogsLive.setValue(this.model.getListOfDogs());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.publisher.cancelSubscription(this, Event.values());
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {

        ArrayList<Dog> list;
        Dog dog;
        updatedValue.getClass().getTypeParameters();

        switch(event) {

            case LIST_DOGS_CHANGED:
                //TODO: дополнить проверкой `updatedValue`
                if(updatedValue instanceof ArrayList) {

                    if(((ArrayList) updatedValue).get(0) instanceof Dog) {

                        list = (ArrayList<Dog>) updatedValue;

                        listOfDogsLive.setValue(list);
                    }


                }
                break;

            case LIST_DOGS_ITEM_ADDED:
                if(updatedValue instanceof Dog) {
                    dog = (Dog) updatedValue;
                    addedDogLive.setValue(dog);
                }
                break;

            case LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Dog) {
                    dog = (Dog) updatedValue;
                    deletedDogId.setValue((int) dog.getId());
                }
                break;
        }
    }

    public LiveData<ArrayList<Dog>> getListOfDogsLive() {
        return this.listOfDogsLive;
    }

    public LiveData<Dog> getAddedDogLive() {
        return this.addedDogLive;
    }

    public LiveData<Integer> getDeletedDogId() {
        return this.deletedDogId;
    }

    public void addNewDog(String dogName) {
        model.createDog(dogName);
    }

    public void deleteDog(int dogIndex) {
        model.deleteDog(dogIndex);
    }
}
