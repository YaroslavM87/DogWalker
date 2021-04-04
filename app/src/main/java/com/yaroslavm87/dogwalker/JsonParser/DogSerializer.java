package com.yaroslavm87.dogwalker.JsonParser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yaroslavm87.dogwalker.Model.Dog;

import java.lang.reflect.Type;

public class DogSerializer implements JsonSerializer<Dog> {

    @Override
    public JsonElement serialize(
            Dog src,
            Type typeOfSrc,
            JsonSerializationContext context
    ) {
        JsonObject result = new JsonObject();

        result.addProperty("_id", src.getId());
        result.addProperty("name", src.getName());
        result.addProperty("imageResId", src.getImageResId());

        return result;
    }
}