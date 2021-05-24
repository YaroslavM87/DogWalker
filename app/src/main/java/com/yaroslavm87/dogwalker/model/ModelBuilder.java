package com.yaroslavm87.dogwalker.model;

import android.content.Context;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.repository.FirebaseDb;
import com.yaroslavm87.dogwalker.repository.SQLiteDbAdapter;

public class ModelBuilder {

    public static Model getModelInstance(Context context) {

        EntitiesCommonEnvironment model = EntitiesCommonEnvironment.INSTANCE;

        //model.setRepository(new SQLiteDbAdapter(context));

        model.setRepository(new FirebaseDb());

        model.subscribeModelForEvents(
                Event.REPO_LIST_DOGS_CHANGED,
                Event.REPO_LIST_DOGS_ITEM_ADDED,
                Event.REPO_LIST_DOGS_ITEM_DELETED
        );

        return model;
    }
}
