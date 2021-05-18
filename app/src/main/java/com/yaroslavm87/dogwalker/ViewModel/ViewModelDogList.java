package com.yaroslavm87.dogwalker.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.Model;
import com.yaroslavm87.dogwalker.model.ModelBuilder;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;

public class ViewModelDogList extends androidx.lifecycle.AndroidViewModel implements Subscriber {

    private final Model model;
    private final Publisher publisher;
    private final MutableLiveData<ArrayList<Dog>> listOfDogsLive;
    private final MutableLiveData<Integer> insertedDogIndexLive;
    private final MutableLiveData<Integer> deletedDogIndexLive;


    {
        this.publisher = Publisher.INSTANCE;
        this.listOfDogsLive = new MutableLiveData<>();
        this.insertedDogIndexLive = new MutableLiveData<>();
        this.deletedDogIndexLive = new MutableLiveData<>();

    }

    public ViewModelDogList(Application application) {
        super(application);

        this.model = ModelBuilder.getModelInstance(application);

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
        //Dog dog;
        //updatedValue.getClass().getTypeParameters();

        switch(event) {

            case LIST_DOGS_CHANGED:
                //TODO: дополнить проверкой `updatedValue`
                if(updatedValue instanceof ArrayList) {

                    if(((ArrayList) updatedValue).get(0) instanceof Dog) {

                        list = (ArrayList<Dog>) updatedValue;

                        this.listOfDogsLive.postValue(list);
                    }
                }
                break;

            case LIST_DOGS_ITEM_ADDED:
                if(updatedValue instanceof Integer) {
                    int index = (int) updatedValue;
                    this.insertedDogIndexLive.postValue(index);
                }
                break;

            case LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Integer) {
                    int index = (int) updatedValue;
                    this.deletedDogIndexLive.postValue(index);
                }
                break;
        }
    }

    public LiveData<ArrayList<Dog>> getListOfDogsLive() {
        return this.listOfDogsLive;
    }

    public LiveData<Integer> getInsertedDogIndexLive() {
        return this.insertedDogIndexLive;
    }

    public LiveData<Integer> getDeletedDogIndexLive() {
        return this.deletedDogIndexLive;
    }


    public void addNewDog(String dogName) {
        this.model.createDog(dogName);
    }

    public void deleteDog(int dogIndex) {
        this.model.deleteDog(dogIndex);
    }
}
