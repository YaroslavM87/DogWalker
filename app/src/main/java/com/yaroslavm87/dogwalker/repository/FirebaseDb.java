package com.yaroslavm87.dogwalker.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.notifications.Event;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDb extends DataSource {

    private FirebaseDatabase db;
    private final String DOGS_NODE;
    private final String WALK_RECORDS_NODE;
    private final String REMOVED_DOGS_NODE;


    private ChildEventListener dogsNodeEventListener;

    private final String LOG_TAG;

    {
        DOGS_NODE = "dogWalker/dogs";
        WALK_RECORDS_NODE = "dogWalker/walkRecords";
        REMOVED_DOGS_NODE= "dogWalker/removedDogs";
        LOG_TAG = "myLogs";
    }

    public FirebaseDb() {
        super(DataSource.Type.REMOTE_STORAGE);
        //TODO: init db in new thread
        initDb();
        //new Thread(this::initDb).start();
    }

    @Override
    public void read(RepoOperations operation, Object value) {

        switch(operation) {

            case READ_LIST_OF_DOGS:
                readListOfDogs();
                break;

            case READ_LIST_OF_WALKS_FOR_DOG:
                if(value instanceof Dog) {
                    readListOfWalkRecordsForDog((Dog) value);
                }
                break;
        }
    }

    @Override
    public void add(RepoOperations operation, Object value) {
        switch(operation) {

            case CREATE_DOG:
                if(value instanceof Dog) {
                    createDog((Dog) value);
                }
                break;

            case ADD_DOG_TO_LIST_OF_REMOVED_DOGS:
                if(value instanceof Dog) {
                    addDogToListOfRemovedDogs((Dog) value);
                }
                break;

        }
    }

    @Override
    public void update(RepoOperations operation, Object value) {
        switch(operation) {

            case CREATE_RECORD_OF_DOG_WALK:
                if(value instanceof Dog) {
                    createRecordOfDogWalk((Dog) value);
                }
                break;
        }
    }

    @Override
    public void delete(RepoOperations operation, Object value) {
        switch(operation) {

            case DELETE_DOG:
                if(value instanceof Dog) {
                    deleteDog((Dog) value);
                }
                break;
        }
    }

    void initDb() {
        db = FirebaseDatabase.getInstance();
        db.setLogLevel(Logger.Level.DEBUG);
        Log.d(LOG_TAG, "DB ref " + db.getReference());
    }

    private void readListOfDogs() {
        Log.d(LOG_TAG, "FirebaseDb.readListOfDogs() call");
//        Query lastTimeWalkQuery = db.getReference(DOGS_NODE)
//                .orderByChild("lastTimeWalk");
        if(dogsNodeEventListener == null) {
            dogsNodeEventListener = db.getReference(DOGS_NODE).addChildEventListener(createDogNodeEventListener());
        }
    }

    private void readListOfWalkRecordsForDog(Dog dog) {
        Log.d(LOG_TAG, "FirebaseDb.readListOfWalksForDog() call");
        Query listOfWalksForDogQuery = db.getReference(WALK_RECORDS_NODE).child(dog.getId())
                .orderByChild("timestamp");

//        listOfWalksForDogQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    Log.d(LOG_TAG, "result class =" + task.getResult().getValue().getClass());
//
//                }
//            }
//        });

        listOfWalksForDogQuery.addListenerForSingleValueEvent(createWalkRecordsNodeValueListener());
        //listOfWalksForDogQuery.removeEventListener(walkRecordsNodeListener);
        //walkRecordsNodeEventListener = listOfWalksForDogQuery.addChildEventListener(createWalkRecordsNodeListener());
    }

    private void createDog(Dog dog) {
        Log.d(LOG_TAG, "FirebaseDb.createDog() call");
        String key = db.getReference().child(DOGS_NODE).push().getKey();
        if(key != null) {
            dog.setId(key);
            db.getReference().child(DOGS_NODE).child(key).setValue(dog);
        }
    }

    private void createRecordOfDogWalk(Dog dog) {
        Log.d(LOG_TAG, "FirebaseDb.createRecordOfDogWalk() call");

        DatabaseReference dogs = db.getReference().child(DOGS_NODE);
        DatabaseReference walkRecords = db.getReference().child(WALK_RECORDS_NODE);

        //String key = db.getReference().child(WALK_RECORDS_NODE).push().getKey();
        String key = db.getReference().child(WALK_RECORDS_NODE).child(dog.getId()).push().getKey();

        if(key != null) {
            walkRecords.child(dog.getId()).child(key).child("id").setValue(key);
            walkRecords.child(dog.getId()).child(key).child("timestamp").setValue(dog.getLastTimeWalk());
            walkRecords.child(dog.getId()).child(key).child("dogId").setValue(dog.getId());

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("lastTimeWalk" , dog.getLastTimeWalk());
            childUpdates.put("lastWalkRecordId", key);

            dogs.child(dog.getId()).updateChildren(childUpdates);
        }
    }

    private void deleteDog(Dog dog) {
        Log.d(LOG_TAG, "FirebaseDb.deleteDog() call");
        db.getReference().child(DOGS_NODE).child(dog.getId()).setValue(null);
    }

    private void addDogToListOfRemovedDogs(Dog dog) {
        Log.d(LOG_TAG, "FirebaseDb.addDogToListOfRemovedDogs() call");
        db.getReference().child(REMOVED_DOGS_NODE).child(dog.getId()).setValue(dog);
    }

    private ChildEventListener createDogNodeEventListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(LOG_TAG, "--");
                Log.d(LOG_TAG, "--");
                Log.d(LOG_TAG, "----------- FIREBASE: NEW DOG OBJ AVAILABLE -----------");
                Log.d(LOG_TAG, "--");
                Dog dog = snapshot.getValue(Dog.class);
                Log.d(LOG_TAG, "ChildEventListener.onChildAdded() call | dog name=" + dog.getName());

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
                Log.d(LOG_TAG, "FirebaseDb.createDogNodeListener().onChildChanged() call");
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
                Log.d(LOG_TAG, "FirebaseDb.createDogNodeListener().onChildRemoved() call");
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

            //TODO: dispatch error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        };
    }

//    private ChildEventListener createWalkRecordsNodeEventListener() {
//        return new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                WalkRecord walkRecord = snapshot.getValue(WalkRecord.class);
//                Log.d(LOG_TAG, "--");
//                Log.d(LOG_TAG, "--");
//                Log.d(LOG_TAG, "----------- NEW WALK RECORD id =" + walkRecord.getId() + "-----------");
//                Log.d(LOG_TAG, "--");
//                Log.d(LOG_TAG, "FirebaseDb.ChildWalkRecordsNodeListener().onChildAdded() call");
//
//                DogRepository.INSTANCE.notifyDataChanged(
//                        Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
//                        (subscriber) -> subscriber.receiveUpdate(
//                                Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
//                                walkRecord
//                        )
//                );
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
//
//            //TODO: dispatch error
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//
//        };
//    }

    private ValueEventListener createWalkRecordsNodeValueListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WalkRecord walkRecord = snapshot.getValue(WalkRecord.class);
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- FIREBASE: NEW WALK OBJ AVAILABLE id =" + walkRecord.getId() + "-----------");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "ValueEventListener.onDataChange().onChildAdded() call");

                    DogRepository.INSTANCE.notifyDataChanged(
                            Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
                            (subscriber) -> subscriber.receiveUpdate(
                                    Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
                                    walkRecord
                            )
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }
}