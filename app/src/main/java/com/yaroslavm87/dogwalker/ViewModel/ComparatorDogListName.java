package com.yaroslavm87.dogwalker.ViewModel;

import com.yaroslavm87.dogwalker.model.Dog;
import java.util.Comparator;

public class ComparatorDogListName implements Comparator<Dog> {

    @Override
    public int compare(Dog dog1, Dog dog2) {
        return dog1.getName().compareTo(dog2.getName());
    }
}
