package com.yaroslavm87.dogwalker.model;

import java.util.ArrayList;
import java.util.LinkedList;

public interface Model {

    ArrayList<Dog> getReferenceDogs();

    LinkedList<WalkRecord> getReferenceWalkRecords();

    void dispatchWalkRecordsFor(Dog dog);

    void createDog(String name, String description);

    void updateDogDescription(int dogIndex, String updatedDescription);

    void walkDog(int dogIndex);

    void deleteDog(int index);
}
