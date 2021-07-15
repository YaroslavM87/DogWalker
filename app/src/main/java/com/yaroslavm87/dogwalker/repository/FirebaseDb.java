package com.yaroslavm87.dogwalker.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.notifications.Event;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseDb extends DataSource {

    private FirebaseDatabase db;
    private FirebaseStorage cs;
    private final String DOGS_NODE;
    private final String WALK_RECORDS_NODE;
    private final String REMOVED_DOGS_NODE;
    private final String PROFILE_IMAGE_BUCKET;

    private ChildEventListener dogsNodeEventListener;
    private ChildEventListener walkRecordsNodeEventListener;

    private final String LOG_TAG;

    {
        DOGS_NODE = "dogWalker/dogs1";
        WALK_RECORDS_NODE = "dogWalker/walkRecords1";
        REMOVED_DOGS_NODE = "dogWalker/removedDogs1";
        PROFILE_IMAGE_BUCKET = "dogWalker/profileImages1";
        LOG_TAG = "myLogs";
    }

    public FirebaseDb() {
        super(DataSource.Type.REMOTE_STORAGE);
        initDb();
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

            case UPDATE_DOG_DESCRIPTION:
                if(value instanceof Dog) {
                    updateDogDescription((Dog) value);
                }
                break;

            case UPDATE_DOG_IMAGE:
                if(value instanceof Dog) {
                    updateDogImage((Dog) value);
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
        db.setPersistenceEnabled(true);
        cs = FirebaseStorage.getInstance();
        db.setLogLevel(Logger.Level.DEBUG);
        Log.d(LOG_TAG, "DB ref " + db.getReference());
    }

    private void readListOfDogs() {
        Query lastTimeWalkQuery = db.getReference(DOGS_NODE)
                .orderByChild("name");
        if(dogsNodeEventListener == null) {
            dogsNodeEventListener = lastTimeWalkQuery.addChildEventListener(createDogNodeEventListener());
        }
    }

    private void readListOfWalkRecordsForDog(Dog dog) {
        Query listOfWalksForDogQuery = db.getReference(WALK_RECORDS_NODE).child(dog.getId())
                .orderByChild("timestamp");

        if(walkRecordsNodeEventListener != null) {
            listOfWalksForDogQuery.removeEventListener(walkRecordsNodeEventListener);
        }

        listOfWalksForDogQuery.addChildEventListener(createWalkRecordsNodeEventListener());
    }

    private void createDog(Dog dog) {
        String key = db.getReference().child(DOGS_NODE).push().getKey();
        if(key != null) {
            dog.setId(key);
            db.getReference().child(DOGS_NODE).child(key).setValue(dog);
        }
    }

    private void createRecordOfDogWalk(Dog dog) {
        DatabaseReference dogs = db.getReference().child(DOGS_NODE);
        DatabaseReference walkRecords = db.getReference().child(WALK_RECORDS_NODE);

        String key = db.getReference().child(WALK_RECORDS_NODE).child(dog.getId()).push().getKey();

        if(key != null) {
            Map<String, Object> walkRecordsNewChild = new HashMap<>();
            walkRecordsNewChild.put("id", key);
            walkRecordsNewChild.put("dogId", dog.getId());
            walkRecordsNewChild.put("timestamp", dog.getLastTimeWalk());
            walkRecords.child(dog.getId()).child(key).updateChildren(walkRecordsNewChild);

            Map<String, Object> dogChildUpdates = new HashMap<>();
            dogChildUpdates.put("lastTimeWalk" , dog.getLastTimeWalk());
            dogChildUpdates.put("lastWalkRecordId", key);
            dogs.child(dog.getId()).updateChildren(dogChildUpdates);
        }
    }

    private void updateDogDescription(Dog dog) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("description" , dog.getDescription());
        db.getReference().child(DOGS_NODE).child(dog.getId()).updateChildren(childUpdates);
    }

    private void updateDogImage(Dog dog) {
        final StorageReference ref = cs.getReference().child(PROFILE_IMAGE_BUCKET).child(dog.getId());

        Uri uri = Uri.fromFile(new File(dog.getImageUri()));
        UploadTask uploadTask = ref.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return ref.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String absUri = downloadUri.toString();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("imageUri" , absUri);
                db.getReference().child(DOGS_NODE).child(dog.getId()).updateChildren(childUpdates);

            } else {
                // Handle failures
                // ...
            }
        });
    }

    private void deleteDog(Dog dog) {
        db.getReference().child(DOGS_NODE).child(dog.getId()).setValue(null);
    }

    private void addDogToListOfRemovedDogs(Dog dog) {
        db.getReference().child(REMOVED_DOGS_NODE).child(dog.getId()).setValue(dog);
    }

    private ChildEventListener createDogNodeEventListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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

    private ChildEventListener createWalkRecordsNodeEventListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                WalkRecord walkRecord = snapshot.getValue(WalkRecord.class);

                DogRepository.INSTANCE.notifyDataChanged(
                        Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
                        (subscriber) -> subscriber.receiveUpdate(
                                Event.REPO_NEW_WALK_RECORD_OBJ_AVAILABLE,
                                walkRecord
                        )
                );
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            //TODO: dispatch error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        };
    }
}