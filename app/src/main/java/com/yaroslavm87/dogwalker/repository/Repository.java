package com.yaroslavm87.dogwalker.repository;

import com.yaroslavm87.dogwalker.model.Dog;

public interface Repository {

    void read(RepoOperations operation, Object value);

    void add(RepoOperations operation, Object value);

    void update(RepoOperations operation, Object value);

    void delete(RepoOperations operation, Object value);
}