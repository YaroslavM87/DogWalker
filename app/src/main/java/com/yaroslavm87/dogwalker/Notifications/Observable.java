package com.yaroslavm87.dogwalker.Notifications;

public interface Observable {

    CmdPassValToSubscriber prepareCommandForUpdate(Event event);
}