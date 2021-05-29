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
    private final Publisher publisher;
    private final MutableLiveData<ArrayList<Dog>> listOfDogsLive;
    private final MutableLiveData<Integer> insertedDogIndexLive, changedDogIndexLive, deletedDogIndexLive;

    private int chosenDogFromList_index;
    private String chosenDogFromList_name;

    private final String LOG_TAG;


    {
        this.publisher = Publisher.INSTANCE;
        this.listOfDogsLive = new MutableLiveData<>();
        this.insertedDogIndexLive = new MutableLiveData<>();
        this.changedDogIndexLive = new MutableLiveData<>();
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
                Event.MODEL_LIST_DOGS_ITEM_CHANGED,
                Event.MODEL_LIST_DOGS_ITEM_DELETED,
                Event.MODEL_ERROR
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

            case MODEL_LIST_DOGS_ITEM_CHANGED:

                if(updatedValue instanceof Integer) {

                    this.changedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_LIST_DOGS_ITEM_DELETED:

                if(updatedValue instanceof Integer) {

                    this.deletedDogIndexLive.postValue((int) updatedValue);
                }
                break;

            case MODEL_ERROR:
                if(updatedValue instanceof Long) {

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

    public LiveData<Integer> getChangedDogIndexLive() {
        return this.changedDogIndexLive;
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

    public void walkDog() {

        if(this.chosenDogFromList_index > -1) {

            this.model.walkDog(this.chosenDogFromList_index);

            this.chosenDogFromList_index = -1;
            this.chosenDogFromList_name = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortName() {

        model.getListOfDogs().sort(new ComparatorDogListName());
        listOfDogsLive.postValue(model.getListOfDogs());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortTime() {

        model.getListOfDogs().sort(new ComparatorDogListLastTimeWalk());
        listOfDogsLive.postValue(model.getListOfDogs());
    }


    public void setCurrentChosenDogByIndex(int index) {

        Log.d(LOG_TAG, "ViewModelDogList.setCurrentChosenDogByIndex() call = " + index);

        this.chosenDogFromList_index = index;
        
        this.chosenDogFromList_name = this.model.getListOfDogs().get(index).getName();
    }
}
