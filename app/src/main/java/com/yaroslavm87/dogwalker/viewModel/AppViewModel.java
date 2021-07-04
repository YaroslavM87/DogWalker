package com.yaroslavm87.dogwalker.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.Model;
import com.yaroslavm87.dogwalker.model.ModelBuilder;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class AppViewModel extends androidx.lifecycle.AndroidViewModel implements Subscriber {

    private Model model;
    private Publisher PUBLISHER;
    private MutableLiveData<ArrayList<Dog>> listOfDogsLive;

    // private MutableLiveData<WalkRecord> insertedWalkRecordLive;

    private MutableLiveData<Integer> insertedDogIndexLive,
            changedDogIndexLive,
            deletedDogIndexLive,
            chosenIndexOfDogFromListLive;
    private MutableLiveData<Dog> chosenDogFromListLive, changedDogLive;

    private LinkedList<WalkRecord> refListOfWalkTimestamps;
    private MutableLiveData<LinkedList<WalkRecord>> walkTimestampsLive;

    private MutableLiveData<String> modelErrorMessageLive;
    private String LOG_TAG;

    public AppViewModel(Application application) {
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
                    Log.d(LOG_TAG, "AppsViewModel.receiveUpdate().dogAdded at index = " + (int) updatedValue);
                    insertedDogIndexLive.setValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_CHANGED:
                if(updatedValue instanceof Integer) {
                    changedDogIndexLive.setValue((int) updatedValue);

                } else if (updatedValue instanceof Dog) {
                    changedDogLive.setValue((Dog) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_DELETED:
                if(updatedValue instanceof Integer) {
                    deletedDogIndexLive.setValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_WALK_RECORDS_ITEM_ADDED:
                if (updatedValue instanceof WalkRecord) {
                    Log.d(LOG_TAG, "AppViewModel.receiveUpdate().walkRecordAdded WalkRecord = " + updatedValue);
                }
                if(refListOfWalkTimestamps != null && refListOfWalkTimestamps.size() > 0) {
                    walkTimestampsLive.setValue(refListOfWalkTimestamps);
                }
                break;

            case MODEL_MESSAGE:
                if(updatedValue instanceof String) {
                    Log.d(LOG_TAG, "AppViewModel.receiveUpdate().MODEL_ERROR");
                    modelErrorMessageLive.setValue((String) updatedValue);
                }
                break;
        }
    }

    public ArrayList<Dog> getDogListReference() {
        return model.getReferenceDogs();
    }

    public void setCurrentChosenDog(Dog dog) {
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "----------- SET CURRENT DOG = " + dog.getName() + " -----------");
        Log.d(LOG_TAG, "--");
        Log.d(LOG_TAG, "AppsViewModel.setCurrentChosenDog()");
        model.dispatchWalkRecordsFor(dog);
        chosenDogFromListLive.setValue(dog);
    }

    public void setCurrentIndexOfChosenDog(int index) {
        //Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + index);
        chosenIndexOfDogFromListLive.postValue(index);
    }

    public void addNewDog(String dogName, String description) {
        model.createDog(dogName, description);
    }

    public void deleteDog() {
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.deleteDog(chosenIndexOfDogFromListLive.getValue());
            resetDogBufferVariables();
        }
    }

    public void updateDogDescription(String updatedDescription) {
        Log.d(LOG_TAG, "AppsViewModel.updateDogDescription() index = " + chosenIndexOfDogFromListLive.getValue());
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.updateDogDescription(chosenIndexOfDogFromListLive.getValue(), updatedDescription);
        }
    }

    public void walkDog() {
        Log.d(LOG_TAG, "AppsViewModel.walkDog() index = " + chosenIndexOfDogFromListLive.getValue());
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.walkDog(chosenIndexOfDogFromListLive.getValue());
        }
    }

    public void sortName() {
        model.getReferenceDogs().sort(
                (dog1, dog2) -> dog1.getName().compareTo(dog2.getName())

        );

        listOfDogsLive.postValue(model.getReferenceDogs());
    }

    public void sortTime() {
        model.getReferenceDogs().sort(
                (dog1, dog2) -> (int) (dog1.getLastTimeWalk() - dog2.getLastTimeWalk())
        );

        listOfDogsLive.postValue(model.getReferenceDogs());
    }



    // for FragmentDogList
    public LiveData<Integer> getChosenIndexOfDogFromListLive() {
        return chosenIndexOfDogFromListLive;
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

    // for FragmentDogInfo
    public LiveData<Dog> getChangedDogLive() {
        return changedDogLive;
    }
    public LiveData<LinkedList<WalkRecord>> getWalkTimestampsLive() {
        return walkTimestampsLive;
    }
//        public LiveData<WalkRecord> getInsertedWalkRecordLive() {
//        return insertedWalkRecordLive;
//    }

    // for FragmentDogInfo and FragmentWalkRecords
    public LiveData<Dog> getChosenDogFromListLive() {
        return chosenDogFromListLive;
    }


    public LiveData<String> getModelErrorMessageLive() {
        return modelErrorMessageLive;
    }

    private void initEntities(Application application) {
        PUBLISHER = Publisher.INSTANCE;
        model = ModelBuilder.getModelInstance(application);
        refListOfWalkTimestamps = model.getReferenceWalkRecords();

        listOfDogsLive = new MutableLiveData<>();
        insertedDogIndexLive = new MutableLiveData<>();
        changedDogIndexLive = new MutableLiveData<>();
        deletedDogIndexLive = new MutableLiveData<>();

        //insertedWalkRecordLive = new MutableLiveData<>();
        chosenDogFromListLive = new MutableLiveData<>();

        // for FragmentDogInfo to keep dog data actual
        changedDogLive = new MutableLiveData<>();

        // for FragmentDogInfo to get updated list of 5 last walks
        walkTimestampsLive = new MutableLiveData<>();

        chosenIndexOfDogFromListLive = new MutableLiveData<>();
        modelErrorMessageLive = new MutableLiveData<>();

        LOG_TAG = "myLogs";
    }

    private void setEntities() {
        PUBLISHER.subscribeForEvent(
                this,
                Event.MODEL_LIST_DOGS_ITEM_ADDED,
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                Event.MODEL_LIST_WALK_RECORDS_ITEM_ADDED,
                Event.MODEL_MESSAGE
        );
        resetDogBufferVariables();
    }

    public void resetDogBufferVariables() {
        chosenDogFromListLive.postValue(null);
        chosenIndexOfDogFromListLive.postValue(-1);
    }
}
