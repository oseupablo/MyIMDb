package com.example.treinamentomobile.myimdb.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Created by treinamentomobile on 11/17/15.
 */
public class MyGsonHttpMessageConverter extends GsonHttpMessageConverter {

    public MyGsonHttpMessageConverter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        setGson(gson);
    }
}

