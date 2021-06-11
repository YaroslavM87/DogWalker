package com.yaroslavm87.dogwalker.viewModel;

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
import java.util.Objects;

public class ViewModelDogList extends androidx.lifecycle.AndroidViewModel implements Subscriber {

    private Model model;
    private Publisher PUBLISHER;
    private MutableLiveData<ArrayList<Dog>> listOfDogsLive;
    private MutableLiveData<Integer> insertedDogIndexLive, changedDogIndexLive, deletedDogIndexLive, chosenIndexOfDogFromListLive;
    private MutableLiveData<Dog> chosenDogFromListLive;
    private String LOG_TAG;

    public ViewModelDogList(Application application) {
        super(application);
        initEntities(application);
        setEntities();

        //TODO: find another solution to sort the list
//        new Thread(() -> {
//
//            try{
//                Thread.sleep(5000);
//
//            } catch(InterruptedException e) {
//                Log.d(LOG_TAG, "ViewModelDogList constructor call -> " + e);
//            }
//
//            sortTime();
//
//        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        PUBLISHER.cancelSubscription(this, Event.values());
    }

    @Override
    public void receiveUpdate(Event event, Object updatedValue) {

        switch(event) {

            case MODEL_LIST_DOGS_ITEM_ADDED:
                if(updatedValue instanceof Integer) {
                    Log.d(LOG_TAG, "ViewModelDogList.receiveUpdate().dogAdded at index = " + (int) updatedValue);
                    insertedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_CHANGED:
                if(updatedValue instanceof Integer) {
                    changedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Integer) {
                    deletedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_ERROR:
                if(updatedValue instanceof Long) {
                    //TODO: add message handling
//                    deletedDogIndexLive.postValue((int) updatedValue);
                }
                break;
        }
    }

    public void addNewDog(String dogName) {
        model.createDog(dogName);
    }

    public void deleteDog() {
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.deleteDog(chosenIndexOfDogFromListLive.getValue());
            resetDogBufferVariables();
        }
    }

    public void walkDog() {
        Log.d(LOG_TAG, "ViewModelDogList.receiveUpdate().dogAdded at index = " + chosenIndexOfDogFromListLive.getValue());

        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.walkDog(chosenIndexOfDogFromListLive.getValue());
            resetDogBufferVariables();
        }
    }

    public void sortName() {

        model.getListOfDogs().sort(
                (dog1, dog2) -> dog1.getName().compareTo(dog2.getName())

        );

        listOfDogsLive.postValue(model.getListOfDogs());
    }

    public void sortTime() {

        model.getListOfDogs().sort(
                (dog1, dog2) -> (int) (dog1.getLastTimeWalk() - dog2.getLastTimeWalk())
        );

        listOfDogsLive.postValue(model.getListOfDogs());
    }

    public void setCurrentChosenDog(Dog dog) {
        //Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + dog.getName());
        chosenDogFromListLive.postValue(dog);
    }

    public void setCurrentIndexOfChosenDog(int index) {
        //Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + index);
        chosenIndexOfDogFromListLive.postValue(index);
    }

    public LiveData<ArrayList<Dog>> getListOfDogsLive() {
        return listOfDogsLive;
    }

    public LiveData<Integer> getInsertedDogIndexLive() {
        return insertedDogIndexLive;
    }

    public LiveData<Integer> getChangedDogIndexLive() {
        return changedDogIndexLive;
    }

    public LiveData<Integer> getDeletedDogIndexLive() {
        return deletedDogIndexLive;
    }

    public LiveData<Dog> getChosenDogFromListLive() {
        return chosenDogFromListLive;
    }

    public LiveData<Integer> getChosenIndexOfDogFromListLive() {
        return chosenIndexOfDogFromListLive;
    }

    private void initEntities(Application application) {
        PUBLISHER = Publisher.INSTANCE;

        model = ModelBuilder.getModelInstance(application);

        listOfDogsLive = new MutableLiveData<>();
        insertedDogIndexLive = new MutableLiveData<>();
        changedDogIndexLive = new MutableLiveData<>();
        deletedDogIndexLive = new MutableLiveData<>();
        chosenDogFromListLive = new MutableLiveData<>();
        chosenIndexOfDogFromListLive = new MutableLiveData<>();
        LOG_TAG = "myLogs";
    }

    private void setEntities() {
        PUBLISHER.subscribeForEvent(
                this,
                Event.MODEL_LIST_DOGS_ITEM_ADDED,
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                Event.MODEL_ERROR
        );

        listOfDogsLive.setValue(model.getListOfDogs());

        resetDogBufferVariables();
    }

    private void resetDogBufferVariables() {
        chosenDogFromListLive.postValue(null);
        chosenIndexOfDogFromListLive.postValue(-1);
    }
}
