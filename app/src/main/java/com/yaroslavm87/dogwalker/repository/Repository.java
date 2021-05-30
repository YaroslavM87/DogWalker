package com.yaroslavm87.dogwalker.repository;

import com.yaroslavm87.dogwalker.model.Dog;

public interface Repository {

    void read();

    void add(Dog dog);

    void update(Dog dog);

    void delete(Dog dog);
}