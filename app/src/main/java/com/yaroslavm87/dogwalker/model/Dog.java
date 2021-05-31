package com.yaroslavm87.dogwalker.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Dog implements Cloneable{

    private int _id;
    private String name;
    private int imageResId;
    private long lastTimeWalk;

    public Dog() {
    }

    public Dog(int id, String name, int imageResId, long lastTimeWalk) {
        setId(id);
        setName(name);
        setImageResId(imageResId);
        setLastTimeWalk(lastTimeWalk);
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name).toLowerCase();
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public long getLastTimeWalk() {
        return lastTimeWalk;
    }

    public void setLastTimeWalk(long lastTimeWalk) {

        if(lastTimeWalk < 0) throw new IllegalArgumentException();

        this.lastTimeWalk = lastTimeWalk;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Dog) {

            return this.name.equals(((Dog) obj).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int counter = 1;
        int result = 1;

        char[] nameAsArray = name.toCharArray();

        for(char ch : nameAsArray) {
            result += ch * prime * counter++;
        }

        return result;
    }

    // TODO: replace with clone()
    public static Dog getCopy(Dog dogToCopy) {

        return new Dog(
                dogToCopy.getId(),
                dogToCopy.getName(),
                dogToCopy.getImageResId(),
                dogToCopy.getLastTimeWalk()
        );
    }
}