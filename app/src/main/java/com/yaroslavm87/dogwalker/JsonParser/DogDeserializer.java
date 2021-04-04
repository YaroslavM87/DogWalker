package com.yaroslavm87.dogwalker.JsonParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yaroslavm87.dogwalker.Model.Dog;

import java.lang.reflect.Type;

public class DogDeserializer implements JsonDeserializer<Dog> {

    @Override
    public Dog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        JsonObject jsonObject = json.getAsJsonObject();

        Dog dog = new Dog(
                jsonObject.get("_id").getAsInt(),
                jsonObject.get("name").getAsString()
        );

        dog.setImageResId(jsonObject.get("imageResId").getAsInt());

        return dog;
    }
}
