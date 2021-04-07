package com.yaroslavm87.dogwalker.commands;

import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

public class CommandExecutor {

    public static void execute(
            Observable observable,
            Subscriber subscriber,
            PassValToSubscriber command
    ) {
        command.execute(observable, subscriber);
    }
}