package com.example.treinamentomobile.myimdb.connection;

import com.activeandroid.serializer.TypeSerializer;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by phsil on 17/11/15.
 */
public class ListSerializer extends TypeSerializer {
    private static final Gson gson = new Gson();

    @Override
    public Class<?> getDeserializedType() {
        return List.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public Object serialize(Object data) {
        if(data == null) return null;
        final String json = gson.toJson(data);
        return json;
    }

    @Override
    public Object deserialize(Object data) {
        if(data == null) return null;
        final List<String> listItems = gson.fromJson(data.toString(), List.class);
        return listItems;
    }
}
