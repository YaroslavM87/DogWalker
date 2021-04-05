package com.yaroslavm87.dogwalker.model;

import com.yaroslavm87.dogwalker.notifications.CmdPassValToSubscriber;
import com.yaroslavm87.dogwalker.notifications.Observable;
import com.yaroslavm87.dogwalker.notifications.Subscriber;

public class CommandExecutor {

    public static void execute(
            Observable observable,
            Subscriber subscriber,
            CmdPassValToSubscriber command
    ) {
        command.execute(observable, subscriber);
    }
}