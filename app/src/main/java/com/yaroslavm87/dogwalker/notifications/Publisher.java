package com.yaroslavm87.dogwalker.notifications;

import com.yaroslavm87.dogwalker.commands.CommandExecutor;
import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public enum Publisher {

    INSTANCE;

    private final EnumMap<Event, List<Subscriber>> listsOfSubscribers;

    Publisher() {
        listsOfSubscribers = new EnumMap<>(Event.class);
    }

    public void subscribeForEvent(Event event, Subscriber... subscribers) {

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        for (Subscriber s : subscribers) {

            if(!list.contains(s)) {

                list.add(s);
            }
        }
    }

    public void subscribeForEvent(Subscriber subscriber, Event... events) {

        for (Event e : events) {

            subscribeForEvent(e, subscriber);
        }
    }

    public void cancelSubscription(Subscriber subscriber, Event event) {

        if(!listsOfSubscribers.containsKey(event)) return;

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        if (!list.isEmpty()) {

            list.remove(subscriber);

            if (list.isEmpty()) {

                listsOfSubscribers.remove(event);
            }
        }
    }

    public void cancelSubscription(Subscriber subscriber, Event[] events) {

        for(Event e : events) {

            if(listsOfSubscribers.containsKey(e)) {

                cancelSubscription(subscriber, e);
            }
        }
    }

    public void makeSubscribersReceiveUpdate(Event event, PassValToSubscriber command) {

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        if (list != null) {

            for (Subscriber subscriber : list) {

                if (subscriber != null) {

                    CommandExecutor.execute(subscriber,command);
                }
            }
        }
    }

    private List<Subscriber> getAppropriateListOfSubscribers(Event event) {

        if (listsOfSubscribers.containsKey(event)) {

            return listsOfSubscribers.get(event);

        } else {

            List<Subscriber> list = new LinkedList<>();

            listsOfSubscribers.put(event, list);

            return list;
        }
    }
}