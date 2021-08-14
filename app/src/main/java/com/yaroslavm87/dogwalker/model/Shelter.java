package com.yaroslavm87.dogwalker.model;

import java.util.ArrayList;
import java.util.Objects;

public class Shelter {

    private String id, name, address;
    private ArrayList<String> dogIds;

    public Shelter() {}

    {
        dogIds = new ArrayList<>(25);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getDogIds() {
        return dogIds;
    }

    public void setDogIds(ArrayList<String> dogIds) {
        this.dogIds = dogIds;
    }

    public void addDogIdFromList(String dogId) {
        dogIds.add(dogId);
    }

    public void removeDogIdFromList(String dogId) {
        dogIds.remove(dogId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return Objects.equals(getId(), shelter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
