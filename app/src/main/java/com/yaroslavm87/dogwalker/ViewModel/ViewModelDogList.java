package com.yaroslavm87.dogwalker.ViewModel;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
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
    private final Publisher PUBLISHER;
    private final MutableLiveData<ArrayList<Dog>> listOfDogsLive;
    private final MutableLiveData<Integer> insertedDogIndexLive, changedDogIndexLive, deletedDogIndexLive;

    private int chosenDogFromList_index;
    private String chosenDogFromList_name;

    private final String LOG_TAG;


    {
        PUBLISHER = Publisher.INSTANCE;
        listOfDogsLive = new MutableLiveData<>();
        insertedDogIndexLive = new MutableLiveData<>();
        changedDogIndexLive = new MutableLiveData<>();
        deletedDogIndexLive = new MutableLiveData<>();
        chosenDogFromList_index = -1;
        LOG_TAG = "myLogs";
    }

    public ViewModelDogList(Application application) {
        super(application);

        model = ModelBuilder.getModelInstance(application);

        PUBLISHER.subscribeForEvent(
                this,
                Event.MODEL_LIST_DOGS_ITEM_ADDED,
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                Event.MODEL_ERROR
        );

        //TODO: make receiving the list through a callback
        listOfDogsLive.setValue(model.getListOfDogs());
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


    public void addNewDog(String dogName) {
        model.createDog(dogName);
    }

    public void deleteDog() {

        if(chosenDogFromList_index > -1) {

            model.deleteDog(chosenDogFromList_index);

            chosenDogFromList_index = -1;
            chosenDogFromList_name = null;
        }
    }

    public void walkDog() {

        if(this.chosenDogFromList_index > -1) {

            model.walkDog(chosenDogFromList_index);

            chosenDogFromList_index = -1;
            chosenDogFromList_name = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortName() {

        model.getListOfDogs().sort(
                (dog1, dog2) -> dog1.getName().compareTo(dog2.getName())

        );

        listOfDogsLive.postValue(model.getListOfDogs());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortTime() {

        model.getListOfDogs().sort(
                (dog1, dog2) -> (int) (dog1.getLastTimeWalk() - dog2.getLastTimeWalk())
        );

        listOfDogsLive.postValue(model.getListOfDogs());
    }

    //TODO:
    public void setCurrentChosenDogByIndex(int index) {

        Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + index);

        this.chosenDogFromList_index = index;
        
        this.chosenDogFromList_name = model.getListOfDogs().get(index).getName();
    }
}
