package com.yaroslavm87.dogwalker.notifications;

@FunctionalInterface
public interface CmdPassValToSubscriber {

    void execute(Observable observable, Subscriber subscriber);
}