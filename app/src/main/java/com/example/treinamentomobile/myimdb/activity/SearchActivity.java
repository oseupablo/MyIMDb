package com.example.treinamentomobile.myimdb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.example.treinamentomobile.myimdb.R;
import com.example.treinamentomobile.myimdb.adapter.ListShowAdapter;
import com.example.treinamentomobile.myimdb.connection.NetworkUtil;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.example.treinamentomobile.myimdb.service.ShowIntentService;
import com.example.treinamentomobile.myimdb.service.ShowIntentService_;
import com.example.treinamentomobile.myimdb.util.MyPrefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

/**
 * Created by phsil on 11/18/15.
 * Activity that implements and handles a list of Shows.
 * Performs actions to fetch data using an service and
 * from SQLite. All costly operations are made in background
 * and idle time in the app is handled by a progress bar.
 */
@EActivity(R.layout.search_activity)
@OptionsMenu(R.menu.main)
public class SearchActivity extends AppCompatActivity {

    /**
     * Define the interval for data service fetching
     */
//    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_HOUR = 10 * 1000;

    /**
     * To access shared preferences with android annotations
     * you should implement an interface and annotated it with
     *
     * @SharedPref annotation.
     */
    @Pref
    MyPrefs_ prefs;

    /**
     * To get reference of any view using an id, you should
     * annotate the view attribute with @ViewById annotation.
     * Once done, you can access and use the attribute normally.
     */
    @ViewById
    ListView showsList;

    @ViewById
    View loading;

    @ViewById
    TextView error;

    /**
     * If a class is not a standard Android component(such as
     * an Activity or a Service) you can use @EBean annotation
     * to enhance it. Then use @Bean to inject them to another class
     * or Android component
     */
    @Bean
    public ListShowAdapter adapter;

    private List<ShowInfo> mShows = null;

    /**
     * The method init is executed after all views
     * got referenced. In that moment, all declared view
     * and assigned with @ViewById can be accessed and used.
     */
    @AfterViews
    public void init() {
        fetchData();
    }

    /**
     * Fetch data to populate only the list view of shows.
     * There are three possible cases:
     * 1) The device has internet connection: in this case will be checked
     * if is the time of update data from the server. If were the case, then
     * a service will be started to do so. In case is not the time to update,
     * will be checked if already there are data stored in DB, if so, a method
     * to fetch data from DB will be called. If none of the cases are true, then
     * method error will be called and only will be shown an text on the screen
     * saying that the device has no internet connection.
     * 2) The device has no internet connection: in such case, will be checked if
     * there are any data stored in DB, if so, then will be checked if the time
     * to update already arrived and if this is the case, the data stored will
     * be fetched, but a alert dialog will be prompted alerting that the data
     * could be outdated. If were not time to update, then the data will be
     * fetched and the dialog will be not prompted.
     * 3) There is no internet and no data stored in DB: a text will be showed in
     * the screen saying that there is no internet connection.
     */
    private void fetchData() {
        if (NetworkUtil.getConnectivityStatusReal(getApplicationContext())) {
            if (isUpdateTime(ONE_HOUR)) {
                ShowIntentService_.intent(this).fetchAndSaveShows().start();
                dataIsPresent();
            } else if (isThereAnyDataOnDB()) {
                fetchDataFromDB(false);
                dataIsPresent();
            } else {
                error();
            }
        } else if (isThereAnyDataOnDB()) {
            if (isUpdateTime(ONE_HOUR)) {
                fetchDataFromDB(true);
                dataIsPresent();
            } else {
                fetchDataFromDB(false);
                dataIsPresent();
            }
        } else {
            error();
        }
    }

    /**
     * Sets error view visibility to gone
     */
    private void dataIsPresent() {
        error.setVisibility(View.GONE);
    }

    /**
     * Hide all views from the layout in case
     * it is not possible get any data to
     * populate the view
     */
    private void error() {
        showsList.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }


    /**
     * Listens to the action annotated with @Receiver annotation
     * In this case, listen to when all data for the show list
     * were fetched from the server through of ShowIntentService.
     * Once receive the action, fetch the updated data from DB
     */
    @Receiver(actions = {ShowIntentService.ACTION_SHOW_LIST_SAVE_DONE})
    public void fillViews() {
        fetchDataFromDB(false);
    }


    /**
     * Executes an all query to the showinfo table
     * Shows an alert dialog if showDialog params is true.
     * Simply uses @Background annotation to perform in
     * background because this can be a costly
     * operation and then can not be processed on
     * UIThread.
     *
     * @param showDialog
     */
    @Background
    public void fetchDataFromDB(boolean showDialog) {
        mShows = new Select()
                .from(ShowInfo.class)
                .execute();

        if (mShows != null) {
            if (showDialog)
                showWarningDialog();
            adapter.setList(mShows);
            setListAdapter(adapter);
        }
    }

    /**
     * The AlertDialog must be called from UIThread(Main Thread)
     * that's why its must be assigned with @UIThread from Android
     * Annotations
     */
    @UiThread
    public void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are without internet connection, despite that, " +
                "we are getting saved data in your device that might not be update and " +
                "some resources maybe not work properly!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Utility to set the adapter for the showsList and
     * once the adapter is assigned to the list, the
     * progress bar can be disabled.
     *
     * @param adapter
     */
    @UiThread
    public void setListAdapter(ListShowAdapter adapter) {
        showsList.setAdapter(adapter);
        loading.setVisibility(View.GONE);
    }


    /**
     * Verify if there is any data in showinfo table on DB
     *
     * @return true if there is any data on DB
     */
    private boolean isThereAnyDataOnDB() {
        int count = new Select()
                .from(ShowInfo.class)
                .count();

        return count > 0;
    }

    /**
     * Calculates if it is time to update the data
     * from the server based in an interval time
     *
     * @param interval
     * @return true if is time to update
     */
    private boolean isUpdateTime(long interval) {
        long lastUpdate = prefs.lastUpdate().getOr(0L);
        long intervalTime = System.currentTimeMillis() - interval;

        return lastUpdate < intervalTime;
    }

}