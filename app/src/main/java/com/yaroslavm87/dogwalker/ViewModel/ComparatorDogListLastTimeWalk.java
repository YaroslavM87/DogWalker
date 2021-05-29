package com.yaroslavm87.dogwalker.ViewModel;

import com.yaroslavm87.dogwalker.model.Dog;
import java.util.Comparator;

public class ComparatorDogListLastTimeWalk  implements Comparator<Dog> {

    @Override
    public int compare(Dog dog1, Dog dog2) {

        return (int) (dog1.getLastTimeWalk() - dog2.getLastTimeWalk());
    }
}