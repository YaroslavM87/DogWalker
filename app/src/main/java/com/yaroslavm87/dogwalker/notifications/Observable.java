package com.yaroslavm87.dogwalker.notifications;

import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;

public interface Observable {

    PassValToSubscriber prepareCommandForUpdate(Event event);
}