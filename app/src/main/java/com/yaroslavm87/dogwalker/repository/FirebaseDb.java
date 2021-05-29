package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        addListenerToDB();
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

    void addListenerToDB() {

        ChildEventListener dogListListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildAdded() call");

                DogRepository.INSTANCE.setLastDogMovedBuffer(
                        snapshot.getValue(Dog.class),
                        Event.REPO_NEW_DOG_OBJ_AVAILABLE
                );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildChanged() call");

                DogRepository.INSTANCE.setLastDogMovedBuffer(
                        snapshot.getValue(Dog.class),
                        Event.REPO_LIST_DOGS_ITEM_CHANGED
                );
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Log.d(LOG_TAG, "FirebaseDb.addListenerToDB().onChildRemoved() call");

                DogRepository.INSTANCE.setLastDogMovedBuffer(
                        snapshot.getValue(Dog.class),
                        Event.REPO_LIST_DOGS_ITEM_DELETED
                );
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        };

        db.getReference("dog").addChildEventListener(dogListListener);

    }

    void initDb() {

        db = FirebaseDatabase.getInstance();

        db.setLogLevel(Logger.Level.DEBUG);

        Log.d(LOG_TAG, "DB ref " + db.getReference());
    }
}