package com.yaroslavm87.dogwalker.model;

import androidx.annotation.NonNull;

import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.Objects;

public class WalkRecord {

    private String id;
    private String dogId;
    private long timestamp;
    //private String userId;

    public WalkRecord() {
    }

    public WalkRecord(
            String id,
            String dogId,
            long timestamp
            //String userId
    ) {
        setId(id);
        setDogId(dogId);
        setTimestamp(timestamp);
        //setUserId(userId);
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

//    public String getUserId() {
//        return userId;
//    }

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

//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

    @NonNull
    @Override
    public String toString() {
        return "WalkRecord{" +
                "id='" + id + '\'' +
                ", dogId='" + dogId + '\'' +
                ", timestamp=" + timestamp +
                //", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalkRecord that = (WalkRecord) o;
        return Tools.getMomentOfStartDay(getTimestamp()) == Tools.getMomentOfStartDay(that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Tools.getMomentOfStartDay(getTimestamp()));
    }
}
