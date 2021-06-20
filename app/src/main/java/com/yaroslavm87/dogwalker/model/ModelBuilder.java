package com.yaroslavm87.dogwalker.model;

import android.content.Context;

import com.yaroslavm87.dogwalker.repository.DogRepository;

public class ModelBuilder {

    private static boolean initialized;

    {
        initialized = false;
    }

    public static Model getModelInstance(Context context) {

        AppModel model = AppModel.INSTANCE;

        //model.setRepository(new SQLiteDbAdapter(context));

        if(!initialized) {
            model.setRepo(DogRepository.INSTANCE);

//            model.subscribeModelForEvents(
//                    Event.REPO_NEW_DOG_OBJ_AVAILABLE,
//                    Event.REPO_LIST_DOGS_ITEM_CHANGED,
//                    Event.REPO_LIST_DOGS_ITEM_DELETED
//            );

            //model.startRepoLoadingListOfDogs();

            initialized = true;
        }

        return model;
    }
}
