package com.yaroslavm87.dogwalker.commands;

import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

@FunctionalInterface
public interface PassValToSubscriber {

    void execute(Subscriber subscriber);
}