package com.yaroslavm87.dogwalker.notifications;

import com.yaroslavm87.dogwalker.commands.CommandExecutor;
import com.yaroslavm87.dogwalker.commands.PassValToSubscriber;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public boolean cancelSubscription(Subscriber subscriber, Event event) {

        if(!listsOfSubscribers.containsKey(event)) {

            return false;
        }

        List<Subscriber> list = getAppropriateListOfSubscribers(event);

        if (!list.isEmpty()) {

            boolean result = list.remove(subscriber);

            if (list.isEmpty()) {

                this.listsOfSubscribers.remove(event);
            }

            return result;

        } else {

            return false;
        }
    }

    public void cancelSubscription(Subscriber subscriber, Event[] events) {

        for(Event e : events) {

            if(listsOfSubscribers.containsKey(e)) {

                cancelSubscription(subscriber, e);
            }
        }
    }

    public void notifyEventHappened(Observable observable, Event event) {

        notifySubscribers(
                observable,
                getAppropriateListOfSubscribers(event),
                event);
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

    private void notifySubscribers(Observable observable, List<Subscriber> list, Event event) {

        if (list != null) {

            for (Subscriber subscriber : list) {

                if (subscriber != null) {

                    CommandExecutor.execute(
                            subscriber,
                            observable.prepareCommandForUpdate(event)
                    );
                }
            }
        }
    }
}