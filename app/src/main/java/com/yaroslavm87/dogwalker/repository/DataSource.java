package com.yaroslavm87.dogwalker.repository;

public abstract class DataSource<T> {

    public enum Type {
        LOCAL_STORAGE,
        REMOTE_STORAGE
    }

    private final Type dataSourceType;

    public DataSource(Type dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public abstract void read();

    public abstract void add(T dataToAdd);

    public abstract void update(T updatedData);

    public abstract void delete(T dataToDelete);

    public Type getType() {
        return this.dataSourceType;
    }
}