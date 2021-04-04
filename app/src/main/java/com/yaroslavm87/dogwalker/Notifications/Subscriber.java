package com.yaroslavm87.dogwalker.Notifications;

public interface Subscriber {

    void receiveUpdate(Object updatedValue);
}