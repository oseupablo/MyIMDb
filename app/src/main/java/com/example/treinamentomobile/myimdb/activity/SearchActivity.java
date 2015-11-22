package com.example.treinamentomobile.myimdb.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
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
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
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
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /**
     * Define the interval for data service fetching
     */
//    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long ONE_HOUR = 10 * 1000;
    private static final long TWO_SECONDS = 2000;

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
    TextView noInternet;

    @ViewById
    TextView noResults;

    /**
     * To get reference of a menu item, you should annotate
     * the MenuItem attribute with @OptionsMenuItem and after
     * override onCreateOptionsMenu
     */
    @OptionsMenuItem
    MenuItem menuSearch;

    /**
     * If a class is not a standard Android component(such as
     * an Activity or a Service) you can use @EBean annotation
     * to enhance it. Then use @Bean to inject them to another class
     * or Android component
     */
    @Bean
    public ListShowAdapter adapter;

    private SearchView searchView;
    private boolean searching;
    private List<ShowInfo> mShows;

    /**
     * The method init is executed after all views
     * got referenced. In that moment, all declared view
     * and assigned with @ViewById can be accessed and used.
     */
    @AfterViews
    public void init() {
        noResults.setVisibility(View.GONE);
        noInternet.setVisibility(View.GONE);
        searching = false;
        mShows = null;
        adapter.setSa(this);
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
            } else {
                if(isThereAnyDataOnDB())
                    fetchDataFromDB(false);
                else
                    ShowIntentService_.intent(this).fetchAndSaveShows().start();
            }
        } else if (isThereAnyDataOnDB()) {
            if (isUpdateTime(ONE_HOUR)) {
                fetchDataFromDB(true);
            } else {
                fetchDataFromDB(false);
            }
        } else {
            noDataToFetch();
        }
    }

    /**
     * Hide all views from the layout in case
     * it is not possible get any data to
     * populate the view
     */
    private void noDataToFetch() {
        showsList.setEmptyView(noInternet);
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


    @Receiver(actions = {ShowIntentService.ACTION_SEARCH_DONE})
    public void onSearchSuccess(Intent intent) {
        int showId = intent.getIntExtra(ShowIntentService.SHOW_ID, 0);
        adapter.setResults(fetchSearchResultFromDB(showId));
        loading.setVisibility(View.GONE);
        searching = false;
    }

    private ShowInfo fetchSearchResultFromDB(int id) {
        ShowInfo showInfo = new ShowInfo();
        showInfo = showInfo.getById(id);

        return showInfo;
    }

    @Receiver(actions = {ShowIntentService.ACTION_SEARCH_FAIL})
    public void onSearchFailed() {
        loading.setVisibility(View.GONE);
        List<ShowInfo> empty = new ArrayList<>();
        adapter.setResults(empty);
        showsList.setEmptyView(noResults);
        searching = false;
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

    /**
     * Handles list item click event using @ItemClick
     * annotation. Can receive the position if it were
     * needed.
     * Starts a new activity based on ShowInfo ID from
     * item clicked.
     * @param position
     */
    @ItemClick(R.id.shows_list)
    public void onListItemClick(int position) {
        ShowInfo showInfo = adapter.getItem(position);
        int id = showInfo.get_Id();
        MainActivity_.intent(this).showId(id).start();
    }

    /**
     * Get the searchView reference from menu and
     * set the query text listener to this activity.
     * @param menu
     * @return True if the menu should be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);

        searchView = (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(this);

        return b;
    }

    /**
     * Uses a query string to filter the list view
     * @param query
     * @return true if the query was handled by the listener.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if(!searching) {
            showsList.setVisibility(View.VISIBLE);
            adapter.getFilter().filter(query);
        }
        return true;
    }

    /**
     * Listen to text changes on SearchView and automatically
     * filter the list view.
     * @param newText
     * @return true if the change was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if(!searching) {
            showsList.setVisibility(View.VISIBLE);
            adapter.getFilter().filter(newText);
        }
        return true;
    }

    public void startSearching() {
        this.searching = true;
        loading.setVisibility(View.VISIBLE);
    }
}