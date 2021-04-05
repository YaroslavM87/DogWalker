package com.yaroslavm87.dogwalker.notifications;

public interface Observable {

    CmdPassValToSubscriber prepareCommandForUpdate(Event event);
}