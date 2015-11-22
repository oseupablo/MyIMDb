package com.example.treinamentomobile.myimdb.service;

import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.example.treinamentomobile.myimdb.connection.RestConnection;
import com.example.treinamentomobile.myimdb.model.SearchShow;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.example.treinamentomobile.myimdb.util.MyPrefs_;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.support.app.AbstractIntentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by treinamentomobile on 11/18/15.
 */
@EIntentService
public class ShowIntentService extends AbstractIntentService {

    public static final String ACTION_SHOW_INFO_SAVE_DONE = "ACTION_SHOW_INFO_SAVE_DONE";
    public static final String ACTION_EPISODES_SAVE_DONE = "ACTION_EPISODES_SAVE_DONE";
    public static final String ACTION_CAST_SAVE_DONE = "ACTION_CAST_SAVE_DONE";
    public static final String ACTION_SHOW_LIST_SAVE_DONE = "ACTION_SHOW_LIST_SAVE_DONE";
    public static final String ACTION_SEARCH_DONE = "ACTION_SEARCH_DONE";
    public static final String ACTION_SHOW_LIST_SAVE_FAIL = "ACTION_SHOW_LIST_SAVE_FAIL";
    public static final String ACTION_SEARCH_FAIL = "ACTION_SEARCH_FAIL";
    public static final String SHOW_ID = "com.example.treinamentomobile.myimdb.service.show_id";

    @RestService
    RestConnection connection;

    @Pref
    MyPrefs_ prefs;


    public ShowIntentService() {
        super("ShowIntentService");
    }

    @ServiceAction
    public void fetchAndSaveShows() {
        final List<ShowInfo> shows = connection.getShows();

        if(saveShows(shows)) {
            sendBroadcast(ACTION_SHOW_LIST_SAVE_DONE);
            prefs.lastUpdate().put(System.currentTimeMillis());
        } else {
            sendBroadcast(ACTION_SHOW_LIST_SAVE_FAIL);
        }
    }

    @ServiceAction
    public void searchShow(String name) {
        final List<SearchShow> results = connection.getShowResults(name);
        final List<ShowInfo> shows = getShowsFromSearchedShows(results);

        if(saveShows(shows)) {
            int id = shows.get(0).get_Id();
            sendBroadcast(ACTION_SEARCH_DONE, id);
        } else {
            sendBroadcast(ACTION_SEARCH_FAIL);
        }

    }

    private List<ShowInfo> getShowsFromSearchedShows(List<SearchShow> results) {
        List<ShowInfo> shows = new ArrayList<>();

        for (SearchShow result : results) {
            shows.add(result.getShow());
        }

        return shows;
    }


    private void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action, int id) {
        Intent intent = new Intent(action);
        intent.putExtra(SHOW_ID, id);
        sendBroadcast(intent);
    }

    private boolean saveShows(List<ShowInfo> shows) {
        if(shows.size() == 0)
            return false;

        ActiveAndroid.beginTransaction();
        try {
            for (ShowInfo show : shows) {
                show.setMedium_image(show.getImage().getMedium());
                show.setS_rating(String.valueOf(show.getRating().getAverage()));
                show.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ActiveAndroid.setTransactionSuccessful();
        }
        ActiveAndroid.endTransaction();

        return true;
    }
}