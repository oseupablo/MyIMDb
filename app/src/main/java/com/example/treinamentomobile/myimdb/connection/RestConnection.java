package com.example.treinamentomobile.myimdb.connection;

import com.example.treinamentomobile.myimdb.model.ShowInfo;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.List;

/**
 * Created by treinamentomobile on 11/17/15.
 */
@Rest(rootUrl = "http://api.tvmaze.com", converters = MyGsonHttpMessageConverter.class)
public interface RestConnection {

    @Get("/shows/{id}")
    ShowInfo getInfo(long id);

    @Get("/shows")
    List<ShowInfo> getShows();

    @Get("/search/shows?q=:{name}")
    List<ShowInfo> getShowResults(String name);

}
