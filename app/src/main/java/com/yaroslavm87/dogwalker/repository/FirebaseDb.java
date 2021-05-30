package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;

public class FirebaseDb extends DataSource<Dog> {

    private FirebaseDatabase db;
    private final String LOG_TAG;

    {
        this.LOG_TAG = "myLogs";
    }

    public FirebaseDb() {

        super(DataSource.Type.REMOTE_STORAGE);

        //new Thread(this::initDb).start();
    }

    @Override
    public void read() {

        Log.d(LOG_TAG, "FirebaseDb.read() call");

        //TODO: init db in new thread
        initDb();

        db.getReference("dog").addChildEventListener(createDogObjListenerForDb());
    }

    @Override
    public void add(Dog dog) {

        Log.d(LOG_TAG, "FirebaseDb.add() call");

        db.getReference().child("dog").child(dog.getName()).setValue(dog);
    }

    @Override
    public void update(Dog dog) {

        Log.d(LOG_TAG, "FirebaseDb.update() call");

        db.getReference().child("dog").child(dog.getName()).setValue(dog);
    }

    @Override
    public void delete(Dog dog) {

        Log.d(LOG_TAG, "FirebaseDb.delete() call");

        db.getReference("dog").child(dog.getName()).setValue(null);
    }

    ChildEventListener createDogObjListenerForDb() {

        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildAdded() call");

                Dog dog = snapshot.getValue(Dog.class);

                DogRepository.INSTANCE.notifyDataChanged(
                        Event.REPO_NEW_DOG_OBJ_AVAILABLE,
                        (subscriber) -> subscriber.receiveUpdate(
                                Event.REPO_NEW_DOG_OBJ_AVAILABLE,
                                dog
                        )
                );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildChanged() call");

                Dog dog = snapshot.getValue(Dog.class);

                DogRepository.INSTANCE.notifyDataChanged(
                        Event.REPO_LIST_DOGS_ITEM_CHANGED,
                        (subscriber) -> subscriber.receiveUpdate(
                                Event.REPO_LIST_DOGS_ITEM_CHANGED,
                                dog
                        )
                );
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildRemoved() call");

                Dog dog = snapshot.getValue(Dog.class);

                DogRepository.INSTANCE.notifyDataChanged(
                        Event.REPO_LIST_DOGS_ITEM_DELETED,
                        (subscriber) -> subscriber.receiveUpdate(
                                Event.REPO_LIST_DOGS_ITEM_DELETED,
                                dog
                        )
                );
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        };
    }

    void initDb() {

        db = FirebaseDatabase.getInstance();

        db.setLogLevel(Logger.Level.DEBUG);

        Log.d(LOG_TAG, "DB ref " + db.getReference());
    }
}