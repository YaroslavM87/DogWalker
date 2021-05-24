package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Publisher;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDb implements Repository<ArrayList<Dog>>, Observable {

    private FirebaseDatabase db;
    private final Publisher publisher;

    private final String LOG_TAG;

    {
        db = FirebaseDatabase.getInstance();
        this.publisher = Publisher.INSTANCE;
        this.LOG_TAG = "myLogs";
    }

    @Override
    public ArrayList<Dog> read() {

        ArrayList<Dog> dogList = new ArrayList<>();

        String name = db.getReference().child("Dog").get().toString();
        Log.d(LOG_TAG, "FirebaseDb.read() = " + name);


        ChildEventListener dogListListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Dog dog = snapshot.getValue(Dog.class);

                dogList.add(dog);

                Log.d(LOG_TAG, "FirebaseDb.read().onChildAdded() call");

                //db.getReference().push().removeValue();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        db.getReference().addChildEventListener(dogListListener);

        //db.getReference().removeEventListener(dogListListener);

        Log.d(LOG_TAG, "FirebaseDb.read() call");


        return dogList;
    }

    @Override
    public void add(Dog dog) {
        FirebaseDatabase.getInstance().getReference().push().setValue(dog);
       // db.getReference().push().setValue(dog);

    }

    @Override
    public void update(Dog dog) {

    }

    @Override
    public void delete(Dog dog) {
        FirebaseDatabase.getInstance().getReference().push().removeValue();
    }

    @Override
    public PassValToSubscriber prepareCommandForUpdate(Event event) {








        return null;
    }
}
