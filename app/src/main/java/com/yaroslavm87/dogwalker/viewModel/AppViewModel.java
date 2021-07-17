package com.yaroslavm87.dogwalker.viewModel;

import android.app.Application;
import android.net.Uri;

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
import java.util.Comparator;
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

    private MutableLiveData<Uri> dogProfileImageUriLive;
    private MutableLiveData<String> dogProfileImagePathLive;

    private MutableLiveData<String> modelErrorMessageLive;
    private String LOG_TAG;

    public AppViewModel(Application application) {
        super(application);
        initEntities(application);
        setEntities();
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
                if(refListOfWalkTimestamps != null && refListOfWalkTimestamps.size() > 0) {
                    walkTimestampsLive.setValue(refListOfWalkTimestamps);
                }
                break;

            case MODEL_MESSAGE:
                if(updatedValue instanceof String) {
                    modelErrorMessageLive.setValue((String) updatedValue);
                    modelErrorMessageLive.postValue("");
                }
                break;
        }
    }

    public ArrayList<Dog> getDogListReference() {
        return model.getReferenceDogs();
    }

    public void setCurrentChosenDog(Dog dog) {
        model.dispatchWalkRecordsFor(dog);
        chosenDogFromListLive.setValue(dog);
    }

    public void setCurrentIndexOfChosenDog(int index) {
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
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.updateDogDescription(chosenIndexOfDogFromListLive.getValue(), updatedDescription);
        }
    }

    public void updateDogImage(String imageUri) {
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.updateDogImage(chosenIndexOfDogFromListLive.getValue(), imageUri);
        }
    }

    public void walkDog() {
        if(Objects.requireNonNull(chosenIndexOfDogFromListLive.getValue()) > -1) {
            model.walkDog(chosenIndexOfDogFromListLive.getValue());
        }
    }

    private Comparator<Dog> nameComp;

    private final Comparator<Dog> ascNameComp = (dog1, dog2) -> dog1.getName().compareTo(dog2.getName());

    private final Comparator<Dog> descNameComp = (dog1, dog2) -> ascNameComp.compare(dog2, dog1);

    private Comparator<Dog> dateComp;

    private final Comparator<Dog> ascDateComp = (dog1, dog2) -> {
        int x = 0;
        long d = dog1.getLastTimeWalk() - dog2.getLastTimeWalk();
        if (d != 0L) x = dog1.getLastTimeWalk() - dog2.getLastTimeWalk() < 0 ? -1 : 1;
        return x;
    };

    private final Comparator<Dog> descDateComp = (dog1, dog2) -> ascDateComp.compare(dog2, dog1);

    public void sortName() {
        nameComp = (nameComp == null || nameComp == descNameComp) ? ascNameComp : descNameComp;
        model.getReferenceDogs().sort(nameComp);
        listOfDogsLive.setValue(model.getReferenceDogs());
        dateComp = descDateComp;
    }

    public void sortDate() {
        dateComp = (dateComp == null || dateComp == descDateComp) ? ascDateComp : descDateComp;
        model.getReferenceDogs().sort(dateComp);
        listOfDogsLive.setValue(model.getReferenceDogs());
        nameComp = descNameComp;
    }

    public void receiveDogProfilePicUri(Uri uri) {
        dogProfileImageUriLive.setValue(uri);
    }

    // for FragmentDogList
    public LiveData<ArrayList<Dog>> getListOfDogsLive() {
        return listOfDogsLive;
    }
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

    public LiveData<Uri> getDogProfileImageUriLive() {
        return dogProfileImageUriLive;
    }
    public LiveData<String> getDogProfileImagePathLive() {
        return dogProfileImagePathLive;
    }

//        public LiveData<WalkRecord> getInsertedWalkRecordLive() {
//        return insertedWalkRecordLive;
//    }

    // for FragmentDogInfo and FragmentWalkRecords
    public LiveData<Dog> getChosenDogFromListLive() {
        return chosenDogFromListLive;
    }

    public LiveData<String> getModelMessageLive() {
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

        // for FragmentDogInfo to get Uri of pic from gallery
        dogProfileImageUriLive = new MutableLiveData<>();
        dogProfileImagePathLive = new MutableLiveData<>();

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
