package com.yaroslavm87.dogwalker.model;

import android.content.Context;

import com.yaroslavm87.dogwalker.notifications.Event;
import com.yaroslavm87.dogwalker.repository.DogRepository;
import com.yaroslavm87.dogwalker.repository.FirebaseDb;
import com.yaroslavm87.dogwalker.repository.SQLiteDbAdapter;

public class ModelBuilder {

    public static Model getModelInstance(Context context) {

        EntitiesCommonEnvironment model = EntitiesCommonEnvironment.INSTANCE;

        //model.setRepository(new SQLiteDbAdapter(context));

        model.setRepository(DogRepository.INSTANCE);

        model.subscribeModelForEvents(
                Event.REPO_NEW_DOG_OBJ_AVAILABLE,
                Event.REPO_LIST_DOGS_ITEM_DELETED
        );

        return model;
    }
}
