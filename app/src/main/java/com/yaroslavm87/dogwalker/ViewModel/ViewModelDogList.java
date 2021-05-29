package com.yaroslavm87.dogwalker.ViewModel;

import android.app.Application;
import android.util.Log;

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

    private int chosenDogFromList_index;
    private String chosenDogFromList_name;

    private final String LOG_TAG;


    {
        this.publisher = Publisher.INSTANCE;
        this.listOfDogsLive = new MutableLiveData<>();
        this.insertedDogIndexLive = new MutableLiveData<>();
        this.deletedDogIndexLive = new MutableLiveData<>();
        this.chosenDogFromList_index = -1;
        this.LOG_TAG = "myLogs";
    }

    public ViewModelDogList(Application application) {
        super(application);

        this.model = ModelBuilder.getModelInstance(application);

        this.publisher.subscribeForEvent(
                this,
                Event.MODEL_LIST_DOGS_CHANGED,
                Event.MODEL_LIST_DOGS_ITEM_ADDED,
                Event.MODEL_LIST_DOGS_ITEM_DELETED
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

            case MODEL_LIST_DOGS_CHANGED:
//                //TODO: дополнить проверкой `updatedValue`
                if(updatedValue instanceof ArrayList) {

                    if(((ArrayList) updatedValue).get(0) instanceof Dog) {

                        list = (ArrayList<Dog>) updatedValue;

                        this.listOfDogsLive.postValue(list);
                    }
                }
                break;

            case MODEL_LIST_DOGS_ITEM_ADDED:

                if(updatedValue instanceof Integer) {

                    this.insertedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_DELETED:

                if(updatedValue instanceof Integer) {

                    this.deletedDogIndexLive.postValue((int) updatedValue);
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

    public void deleteDog() {

        if(this.chosenDogFromList_index > -1) {

            this.model.deleteDog(this.chosenDogFromList_index);

            this.chosenDogFromList_index = -1;
            this.chosenDogFromList_name = null;
        }
    }

    public void setCurrentChosenDogByIndex(int index) {

        Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + index);

        this.chosenDogFromList_index = index;
        
        this.chosenDogFromList_name = this.model.getListOfDogs().get(index).getName();
    }
}
