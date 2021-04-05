package com.yaroslavm87.dogwalker.repository;

import com.yaroslavm87.dogwalker.model.Dog;

import java.util.List;

public interface Repository <T extends List<Dog>>  {

    T read();

    void create();

    void update();

    void delete();
}