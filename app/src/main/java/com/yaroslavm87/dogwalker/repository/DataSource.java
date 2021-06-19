package com.yaroslavm87.dogwalker.repository;

public abstract class DataSource {

    public enum Type {
        LOCAL_STORAGE,
        REMOTE_STORAGE
    }

    private final Type dataSourceType;

    public DataSource(Type dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public abstract void read(RepoOperations operation, Object value);

    public abstract void add(RepoOperations operation, Object value);

    public abstract void update(RepoOperations operation, Object value);

    public abstract void delete(RepoOperations operation, Object value);

    public Type getType() {
        return this.dataSourceType;
    }
}