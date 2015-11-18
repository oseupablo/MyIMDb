package com.example.treinamentomobile.myimdb.service;

import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.example.treinamentomobile.myimdb.connection.RestConnection;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.example.treinamentomobile.myimdb.util.MyPrefs_;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.support.app.AbstractIntentService;

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

    @RestService
    RestConnection connection;

    @Pref
    MyPrefs_ prefs;


    public ShowIntentService() {
        super("ShowIntentService");
    }

    @ServiceAction
    void fetchAndSaveShows() {
        final List<ShowInfo> shows = connection.getShows();
        ActiveAndroid.beginTransaction();
        try {
            for (ShowInfo show : shows) {
                show.setMedium_image(show.getImage().getMedium());
                show.setS_rating(String.valueOf(show.getRating().getAverage()));
                show.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.setTransactionSuccessful();
        }
        ActiveAndroid.endTransaction();

        Intent intent = new Intent(ACTION_SHOW_LIST_SAVE_DONE);
        sendBroadcast(intent);
        prefs.lastUpdate().put(System.currentTimeMillis());
    }
}
