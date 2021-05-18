package com.yaroslavm87.dogwalker.model;

import android.content.Context;

import com.yaroslavm87.dogwalker.repository.DatabaseAdapter;
import java.util.ArrayList;

public class ModelBuilder {

    public static Model getModelInstance(Context context) {

        return EntitiesCommonEnvironment.INSTANCE.setRepository(new DatabaseAdapter(context));
    }
}
