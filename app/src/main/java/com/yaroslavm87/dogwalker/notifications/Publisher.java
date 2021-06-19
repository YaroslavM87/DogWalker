package com.yaroslavm87.dogwalker.notifications;

import android.util.Log;
import com.yaroslavm87.dogwalker.commands.CommandExecutor;
import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public enum Publisher {

    INSTANCE;

    private final EnumMap<Event, List<Subscriber>> LIST_OF_SUBSCRIBERS;
    private final String LOG_TAG;

    {
        LIST_OF_SUBSCRIBERS = new EnumMap<>(Event.class);
        LOG_TAG = "myLogs";
    }


    Publisher() {
    }

    public synchronized void subscribeForEvent(Event event, Subscriber... subscribers) {

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        for (Subscriber s : subscribers) {
            //Log.d(LOG_TAG, "Publisher.subscribeForEvent() event = " + event);

            if(!list.contains(s)) {

                list.add(s);
                //Log.d(LOG_TAG, "------------------------- list of subscribers length = " + list.size());
                //Log.d(LOG_TAG, "------------------------- subscriber = " + s.toString());


            }
        }
    }

    public void subscribeForEvent(Subscriber subscriber, Event... events) {

        for (Event e : events) {

            subscribeForEvent(e, subscriber);
        }
    }

    public void cancelSubscription(Subscriber subscriber, Event event) {

        if(!LIST_OF_SUBSCRIBERS.containsKey(event)) return;

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        if (!list.isEmpty()) {

            list.remove(subscriber);

            if (list.isEmpty()) {

                LIST_OF_SUBSCRIBERS.remove(event);
            }
        }
    }

    public void cancelSubscription(Subscriber subscriber, Event[] events) {

        for(Event e : events) {

            if(LIST_OF_SUBSCRIBERS.containsKey(e)) {

                cancelSubscription(subscriber, e);
            }
        }
    }

    public synchronized void makeSubscribersReceiveUpdate(Event event, PassValToSubscriber command) {

        List<Subscriber> list = getAppropriateListOfSubscribers(event);
        Log.d(LOG_TAG, "Publisher.makeSubscribersReceiveUpdate().listSize = " + list.size());

        for (Subscriber subscriber : list) {

            if (subscriber != null) {

                CommandExecutor.execute(subscriber,command);
            }
        }
    }

    private List<Subscriber> getAppropriateListOfSubscribers(Event event) {

        if (LIST_OF_SUBSCRIBERS.containsKey(event)) {

            return LIST_OF_SUBSCRIBERS.get(event);

        } else {

            List<Subscriber> list = new LinkedList<>();

            LIST_OF_SUBSCRIBERS.put(event, list);

            return list;
        }
    }
}