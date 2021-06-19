package com.yaroslavm87.dogwalker.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class WalkRecord {

    private String id;
    private String dogId;
    private long timestamp;
    private String userId;

    public WalkRecord() {
    }

    public WalkRecord(
            String id,
            String dogId,
            long timestamp,
            String userId
    ) {
        setId(id);
        setDogId(dogId);
        setTimestamp(timestamp);
        setUserId(userId);
    }

    public String getId() {
        return id;
    }

    public String getDogId() {
        return dogId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public void setDogId(String dogId) {
        this.dogId = Objects.requireNonNull(dogId);
    }

    public void setTimestamp(long timestamp) {
        if(timestamp < 0) throw new IllegalArgumentException();
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "WalkRecord{" +
                "id='" + id + '\'' +
                ", dogId='" + dogId + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }

    // TODO: replace with clone()
    /*
     copy of walkRecords is made to avoid controversy between
     walkRecord's values in model's listOfWalkRecords and those in database,
     as all changes to dog's values go through database updates
    */
//    public static WalkRecord getCopy(WalkRecord walkRecordToCopy) {
//        return new WalkRecord(
//                walkRecordToCopy.getId(),
//                walkRecordToCopy.getDogId(),
//                walkRecordToCopy.getTimeStamp(),
//                walkRecordToCopy.getUserId()
//        );
//    }
}
