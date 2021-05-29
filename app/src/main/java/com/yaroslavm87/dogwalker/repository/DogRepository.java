package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

public enum DogRepository implements Repository, Observable {

    INSTANCE;

    private final DataSource<Dog> remoteStorage;
    private final Publisher publisher;
    private final String LOG_TAG;
    // TODO: убрать буффер
    private Dog lastDogMovedBuffer;


    {
        this.remoteStorage = new FirebaseDb();
        this.publisher = Publisher.INSTANCE;
        this.LOG_TAG = "myLogs";
    }

    @Override
    public void read() {
        remoteStorage.read();
    }

    @Override
    public void add(Dog dog) {
        remoteStorage.add(dog);
    }

    @Override
    public void update(Dog dog) {

    }

    @Override
    public void delete(Dog dog) {
        remoteStorage.delete(dog);
    }

    @Override
    public PassValToSubscriber prepareCommandForUpdate(Event event) {

        return getAppropriateCommand(event);
    }

    private PassValToSubscriber getAppropriateCommand(Event event) {

        switch (event) {

//            case MODEL_LIST_DOGS_CHANGED:
//                return (observable, subscriber) -> subscriber.receiveUpdate(event, this.read());

            case REPO_NEW_DOG_OBJ_AVAILABLE:
            case REPO_LIST_DOGS_ITEM_DELETED:

                Log.d(LOG_TAG, "SQLiteDbAdapter.getAppropriateCommand() call");

                return (subscriber) -> subscriber.receiveUpdate(event, this.getLastDogMovedBuffer());

            default:
                return null;
        }
    }

    public void setLastDogMovedBuffer(Dog dog, Event event) {

        this.lastDogMovedBuffer = dog;

        this.publisher.notifyEventHappened(this, event);
    }

    private Dog getLastDogMovedBuffer() {

        Dog result = this.lastDogMovedBuffer;

        this.lastDogMovedBuffer = null;

        return result;
    }
}
