package com.yaroslavm87.dogwalker.Model;

import com.yaroslavm87.dogwalker.Notifications.CmdPassValToSubscriber;
import com.yaroslavm87.dogwalker.Notifications.Observable;
import com.yaroslavm87.dogwalker.Notifications.Subscriber;

public class CommandExecutor {

    public static void execute(
            Observable observable,
            Subscriber subscriber,
            CmdPassValToSubscriber command
    ) {
        command.execute(observable, subscriber);
    }
}