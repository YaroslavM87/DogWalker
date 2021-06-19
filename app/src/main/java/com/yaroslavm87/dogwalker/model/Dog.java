package com.yaroslavm87.dogwalker.model;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Dog implements Cloneable{

    // TODO: prohibit changes on dog id
    private String id;
    private String name;
    private String description;
    private String imageUri;
    private String lastWalkRecordId;
    private long lastTimeWalk;
    private String shelterId;


    public Dog() {
    }

    public Dog(
            String id,
            String name,
            String description,
            String imageUri,
            String lastWalkRecordId,
            long lastTimeWalk,
            String shelterId
    ) {
        setId(id);
        setName(name);
        setDescription(description);
        setImageUri(imageUri);
        setLastWalkRecordId(lastWalkRecordId);
        setLastTimeWalk(lastTimeWalk);
        setShelterId(shelterId);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getLastWalkRecordId() {
        return lastWalkRecordId;
    }

    public long getLastTimeWalk() {
        return lastTimeWalk;
    }

    public String getShelterId() {
        return shelterId;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name).toLowerCase();
    }

    public void setDescription(String description) {
        this.description = description;
        //this.description = Objects.requireNonNull(description);
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
        //this.imageUri = Objects.requireNonNull(imageUri);
    }

    public void setLastWalkRecordId(String lastWalkRecordId) {
        this.lastWalkRecordId = lastWalkRecordId;
        //this.lastWalkRecordId = Objects.requireNonNull(lastWalkRecordId);
    }

    public void setLastTimeWalk(long lastTimeWalk) {
        if(lastTimeWalk < 0) throw new IllegalArgumentException();
        this.lastTimeWalk = lastTimeWalk;
    }

    public void setShelterId(String shelterId) {
        this.shelterId = shelterId;
        //this.shelterId = Objects.requireNonNull(shelterId);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("imageUri", imageUri);
        result.put("lastWalkRecordId", lastWalkRecordId);
        result.put("lastTimeWalk", lastTimeWalk);
        result.put("shelterId", shelterId);
        return result;
    }

    // TODO: rewrite to check equality by id and name
    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Dog) {

            return this.name.equals(((Dog) obj).getName());
        }

        return false;
    }

    // TODO: rewrite to generate hash by id and name
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

    @Override
    public String toString() {
        return "Dog{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", lastWalkRecordId='" + lastWalkRecordId + '\'' +
                ", lastTimeWalk=" + lastTimeWalk +
                ", shelterId='" + shelterId + '\'' +
                '}';
    }

    // TODO: replace with clone()
    /*
     copy of dog is made to avoid controversy between
     dog's values in model's listOfDogs and those in database,
     as all changes to dog's values go through database updates
    */
    public static Dog getCopy(Dog dogToCopy) {
        return new Dog(
                dogToCopy.getId(),
                dogToCopy.getName(),
                dogToCopy.getDescription(),
                dogToCopy.getImageUri(),
                dogToCopy.getLastWalkRecordId(),
                dogToCopy.getLastTimeWalk(),
                dogToCopy.getShelterId()
        );
    }
}