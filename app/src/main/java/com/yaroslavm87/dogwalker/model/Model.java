package com.yaroslavm87.dogwalker.model;

import java.util.ArrayList;
import java.util.LinkedList;

public interface Model {

    ArrayList<Shelter> getReferenceShelter();

    void createShelter(String name, String description);

    ArrayList<Dog> getReferenceDogs();

    void dispatchDogsFor(String shelterId);

    void createDog(String name, String description, String shelterId);

    void updateDogDescription(int dogIndex, String updatedDescription);

    void updateDogImage(int dogIndex, String path);

    void walkDog(int dogIndex);

    void deleteDog(int index);

    LinkedList<WalkRecord> getReferenceWalkRecords();

    void dispatchWalkRecordsFor(Dog dog);
}