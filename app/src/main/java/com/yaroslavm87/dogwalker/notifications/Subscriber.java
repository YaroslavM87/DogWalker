package com.yaroslavm87.dogwalker.notifications;

public interface Subscriber {

    void receiveUpdate(Object updatedValue);
}