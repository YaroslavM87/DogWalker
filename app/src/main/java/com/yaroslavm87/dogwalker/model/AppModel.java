package com.yaroslavm87.dogwalker.model;

import android.util.Log;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.notifications.Publisher;
import com.yaroslavm87.dogwalker.repository.RepoOperations;
import com.yaroslavm87.dogwalker.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedList;

public enum AppModel implements Model {

    INSTANCE;

    public final ListOfDogs LIST_OF_DOGS;
    public final ListOfWalkRecords LIST_OF_WALK_RECORDS_FOR_DOG;
    private Repository repository;
    private final Publisher PUBLISHER;
    private final long TIME_TO_REST_AFTER_WALK;
    private final String LOG_TAG;

    {
        LIST_OF_DOGS = new ListOfDogs();
        LIST_OF_WALK_RECORDS_FOR_DOG = new ListOfWalkRecords();
        PUBLISHER = Publisher.INSTANCE;
        TIME_TO_REST_AFTER_WALK = 60000L;
        LOG_TAG = "myLogs";
    }

    AppModel() {}

    public ArrayList<Dog> getReferenceDogs() {
        if(!isListOfDogsLoaded()) repoReadDogs();
        return LIST_OF_DOGS.getList();
    }

    private boolean isListOfDogsLoaded() {
        if(LIST_OF_DOGS.getList() != null) {
            return LIST_OF_DOGS.getList().size() > 0;
        } else return false;
    }

    public LinkedList<WalkRecord> getReferenceWalkRecords() {
        return LIST_OF_WALK_RECORDS_FOR_DOG.getList();
    }

    public void dispatchWalkRecordsFor(Dog dog) {
        if(!isListOfWalkRecordsLoaded(dog)) {
            LIST_OF_WALK_RECORDS_FOR_DOG.clearList();
            repoReadWalkRecords(dog);
        }
    }

    private boolean isListOfWalkRecordsLoaded(Dog dog) {
        if(LIST_OF_WALK_RECORDS_FOR_DOG.getList() != null) {
            return LIST_OF_WALK_RECORDS_FOR_DOG.getList().size() > 0
                    && LIST_OF_WALK_RECORDS_FOR_DOG.getList().get(0).getDogId().equals(dog.getId());
        } else return false;
    }

    public void createDog(String name, String description) {
        if(name.equals("")) {
            String errorMsg = "Напишите имя питомца!";
            dispatchMessage(errorMsg);
            return;
        }
        Dog newDog = new Dog();
        if(!description.equals("")) {
            newDog.setDescription(description);
        }
        newDog.setName(name);
        if(LIST_OF_DOGS.getList().contains(newDog)) {
            Log.d(LOG_TAG, "ListOfDogs contains dog " + name);
            String errorMsg = "Питомец с такой кличкой уже есть в списке!";
            dispatchMessage(errorMsg);

        } else {
            repoAddDog(newDog);
            dispatchMessage("Питомец добален!");
        }
    }

    public void updateDogDescription(int index, String updatedDescription) {
        Log.d(LOG_TAG, "Model.updateDogDescription() call");
        Dog updatedDog = Dog.getCopy(LIST_OF_DOGS.getDog(index));
        updatedDog.setDescription(updatedDescription);
        repoUpdateDogDecription(updatedDog);
    }

    public void updateDogImage(int index, String imageUri) {
        Log.d(LOG_TAG, "Model.updateDogDescription() call");
        Dog updatedDog = Dog.getCopy(LIST_OF_DOGS.getDog(index));
        updatedDog.setImageUri(imageUri);
        repoUpdateDogImage(updatedDog);
    }

    public void walkDog(int index) {
        Log.d(LOG_TAG, "Model.walkDog() call");

        Dog updatedDog = Dog.getCopy(LIST_OF_DOGS.getDog(index));
        long currentTime = System.currentTimeMillis();
        long timeDelta = currentTime - updatedDog.getLastTimeWalk();

        if(timeDelta == 0 || timeDelta >= TIME_TO_REST_AFTER_WALK) {

            updatedDog.setLastTimeWalk(currentTime);

            repoAddWalkRecord(updatedDog);

        } else {
            long timeUntilNextWalk = TIME_TO_REST_AFTER_WALK - timeDelta;

            //TODO: add reference to res with text message
            PUBLISHER.makeSubscribersReceiveUpdate(
                    Event.MODEL_MESSAGE,
                    (subscriber) -> subscriber.receiveUpdate(
                            Event.MODEL_MESSAGE,
                            timeUntilNextWalk
                    )
            );
        }
    }

    public void deleteDog(int index) {
        Log.d(LOG_TAG, "Model.deleteDog() call");
        Dog dogToDelete = LIST_OF_DOGS.getDog(index);

        repoDeleteDog(dogToDelete);

        repoAddDogToRemoved(dogToDelete);
    }

    void setRepo(Repository repository) {
        Log.d(LOG_TAG, "Model.setRepository() call");
        this.repository = repository;
    }

    private void repoReadDogs() {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoReadDogs() call");
                repository.read(RepoOperations.READ_LIST_OF_DOGS, null);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoReadDogs() EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoReadWalkRecords(Dog dog) {
            new Thread(() -> {
                try {
                    Log.d(LOG_TAG, "Model.repoReadWalkRecords(Dog dog) call");
                    repository.read(RepoOperations.READ_LIST_OF_WALKS_FOR_DOG, dog);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Model.repoReadWalkRecords(Dog dog) EXCEPTION: " + e);
                }
            }).start();
    }

    private void repoAddDog(Dog newDog) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoAddDog(Dog newDog) call");
                repository.add(RepoOperations.CREATE_DOG, newDog);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoAddDog(Dog newDog) EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoUpdateDogDecription(Dog updatedDog) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoUpdateDogDescription(Dog updatedDog) call");
                repository.update(RepoOperations.UPDATE_DOG_DESCRIPTION, updatedDog);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoUpdateDogDescription(Dog updatedDog) EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoUpdateDogImage(Dog updatedDog) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoUpdateDogImage(Dog updatedDog) call");
                repository.update(RepoOperations.UPDATE_DOG_IMAGE, updatedDog);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoUpdateDogImage(Dog updatedDog) EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoAddWalkRecord(Dog updatedDog) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoAddWalkRecord(Dog updatedDog) call");
                repository.update(RepoOperations.CREATE_RECORD_OF_DOG_WALK, updatedDog);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoAddWalkRecord(Dog updatedDog) EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoDeleteDog(Dog dogToDelete) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoDeleteDog(Dog dogToDelete) call");
                this.repository.delete(RepoOperations.DELETE_DOG, dogToDelete);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoDeleteDog(Dog dogToDelete) EXCEPTION: " + e);
            }
        }).start();
    }

    private void repoAddDogToRemoved(Dog dogRemoved) {
        new Thread(() -> {
            try{
                Log.d(LOG_TAG, "Model.repoAddDogToRemoved(Dog dogRemoved) call");
                repository.add(RepoOperations.ADD_DOG_TO_LIST_OF_REMOVED_DOGS, dogRemoved);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Model.repoAddDogToRemoved(Dog dogRemoved) EXCEPTION: " + e);
            }
        }).start();
    }

    private void dispatchMessage(String msg) {
        PUBLISHER.makeSubscribersReceiveUpdate(
                Event.MODEL_MESSAGE,
                (subscriber) -> subscriber.receiveUpdate(
                        Event.MODEL_MESSAGE,
                        msg
                )
        );
    }

//    void subscribeModelForEvents(Event... events) {
//        Log.d(LOG_TAG, "Model.subscribeModelForEvents() call");
//        PUBLISHER.subscribeForEvent(LIST_OF_DOGS, Objects.requireNonNull(events));
//    }
}