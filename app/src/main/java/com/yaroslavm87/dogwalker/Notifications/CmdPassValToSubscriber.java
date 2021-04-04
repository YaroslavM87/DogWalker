package com.yaroslavm87.dogwalker.Notifications;

@FunctionalInterface
public interface CmdPassValToSubscriber {

    void execute(Observable observable, Subscriber subscriber);
}