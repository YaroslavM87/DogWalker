package com.yaroslavm87.dogwalker.notifications;

import com.yaroslavm87.dogwalker.commands.CommandExecutor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Publisher {

    private final Map<Event, List<Subscriber>> listsOfSubscribers;

    public Publisher() {

        listsOfSubscribers = new HashMap<>();
    }

    public void subscribeForEvent(Event event, Subscriber... subscribers) {

        for (Subscriber s : subscribers) {

            getAppropriateListOfSubscribers(event).add(s);
        }
    }

    public void subscribeForEvent(Subscriber subscriber, Event... events) {

        for (Event event : events) {

            getAppropriateListOfSubscribers(event).add(subscriber);
        }
    }

    public boolean cancelSubscription(Subscriber subscriber, Event event) {

        if (getAppropriateListOfSubscribers(event).size() != 0) {

            List<Subscriber> list = getAppropriateListOfSubscribers(event);

            boolean result = list.remove(subscriber);

            if (list.size() == 0) {

                listsOfSubscribers.remove(event);
            }

            return result;

        } else {

            return false;
        }
    }

    public void notifyEventHappened(Observable observable, Event event) {

        notifySubscribers(
                observable,
                getAppropriateListOfSubscribers(event),
                event);
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
                            observable,
                            subscriber,
                            observable.prepareCommandForUpdate(event)
                    );
                }
            }
        }
    }
}