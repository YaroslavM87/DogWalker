package com.yaroslavm87.dogwalker.notifications;

public interface Subscriber {

    void receiveUpdate(Event event, Object updatedValue);
}