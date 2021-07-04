package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

public enum DogRepository implements Repository, Observable {

    INSTANCE;

    private final DataSource remoteStorage;
    private final Publisher publisher;
    private final String LOG_TAG;

    {
        remoteStorage = initRemoteStorage();
        publisher = Publisher.INSTANCE;
        LOG_TAG = "myLogs";
    }

    @NonNull
    private DataSource initRemoteStorage() {
        final DataSource remoteStorage;
        remoteStorage = new FirebaseDb();
        return remoteStorage;
    }

    @Override
    public void read(RepoOperations operation, Object value) {
        remoteStorage.read(operation, value);
    }

    @Override
    public void add(RepoOperations operation, Object value) {
        remoteStorage.add(operation, value);
    }

    @Override
    public void update(RepoOperations operation, Object value) {
        remoteStorage.update(operation, value);
    }

    @Override
    public void delete(RepoOperations operation, Object value) {
        remoteStorage.delete(operation, value);
    }

    void notifyDataChanged(Event event, PassValToSubscriber command) {
        publisher.makeSubscribersReceiveUpdate(
                event,
                command
        );
    }
}
