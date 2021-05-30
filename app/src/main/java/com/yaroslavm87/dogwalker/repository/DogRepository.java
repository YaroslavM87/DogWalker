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
        remoteStorage.update(dog);
    }

    @Override
    public void delete(Dog dog) {
        remoteStorage.delete(dog);
    }

    void notifyDataChanged(Event event, PassValToSubscriber command) {

        publisher.makeSubscribersReceiveUpdate(
                event,
                command
        );
    }
}
