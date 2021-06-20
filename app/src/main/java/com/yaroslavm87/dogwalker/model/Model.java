package com.yaroslavm87.dogwalker.model;

import java.util.ArrayList;

public interface Model {

    ArrayList<Dog> getReferenceDogs();

    ArrayList<WalkRecord> getReferenceWalkRecords(Dog dog);

    void createDog(String name);

    void walkDog(int dogIndex);

    void deleteDog(int index);
}
