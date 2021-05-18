package com.yaroslavm87.dogwalker.model;

import java.util.ArrayList;

public interface Model {

    ArrayList<Dog> getListOfDogs();

    void createDog(String name);

    Dog getDog(int dogId);

    void deleteDog(int index);
}
