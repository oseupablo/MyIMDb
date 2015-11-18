package com.example.treinamentomobile.myimdb.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.example.treinamentomobile.myimdb.R;
import com.example.treinamentomobile.myimdb.adapter.MainPagerAdapter;
import com.example.treinamentomobile.myimdb.connection.NetworkUtil;
import com.example.treinamentomobile.myimdb.connection.RestConnection;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.io.IOException;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    final private static String LOG = "MainActivity";
    final private static boolean LOG_STATUS = true;
    final private static long SHOW_ID = 2705;

    @Extra
    int showId;

    @ViewById
    ViewPager pager;

    @ViewById
    TabLayout tabs;

    @ViewById
    TextView error;

    @ViewById
    ImageView backdrop;

    @ViewById
    Toolbar toolbar;

    @ViewById
    AppBarLayout appbar;

    @ViewById(R.id.collapsing_toolbar)
    CollapsingToolbarLayout ct;

    MainPagerAdapter adapter;

    @RestService
    RestConnection connection;

    private ShowInfo mShowInfo;

    @AfterViews
    public void init() {
        if(NetworkUtil.getConnectivityStatusReal(getApplication())) {
            error.setVisibility(View.GONE);
            fetchData();
        }
        else if(hasPersitedData()) {
            error.setVisibility(View.GONE);
            setupViewPager();
        }
        else {
            error.setVisibility(View.VISIBLE);
            pager.setVisibility(View.GONE);
            appbar.setVisibility(View.GONE);
        }
    }

    private boolean hasPersitedData() {
        mShowInfo = new ShowInfo();
        mShowInfo = mShowInfo.getById((int) SHOW_ID);
        return mShowInfo  != null;
    }

    @Background
    public void fetchData() {
        mShowInfo = connection.getInfo(SHOW_ID);
        if (LOG_STATUS)
            Log.d(LOG, "showInfo: {name: " + mShowInfo.getName() + "}");

        String medium_image = mShowInfo.getImage().getMedium();
        String s_rating = String.valueOf(mShowInfo.getRating().getAverage());

        ActiveAndroid.beginTransaction();
        try {
            mShowInfo.setMedium_image(medium_image);
            mShowInfo.setS_rating(s_rating);
            mShowInfo.save();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.setTransactionSuccessful();
        }
        ActiveAndroid.endTransaction();

        setupViewPager();
    }

    @UiThread
    void setupViewPager() {
        setSupportActionBar(toolbar);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), mShowInfo.get_Id());
        pager.setAdapter(adapter);
        tabs.setTabsFromPagerAdapter(adapter);
        tabs.setupWithViewPager(pager);

        Picasso.with(getApplicationContext())
                .load(R.drawable.narcos_big)
                .into(backdrop);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (ct.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(ct)) {
                    loadShowLogo();
                }
                else {
                    toolbar.setLogo(null);
                }
            }
        });

        setTitle("");
    }

    @Background
    public void loadShowLogo() {
        try{
            int size = android.R.dimen.app_icon_size;
            Bitmap logo = Picasso.with(this)
                    .load(R.mipmap.narcos_logo)
                    .resizeDimen(size, size)
                    .get();
            setLogo(logo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void setLogo(Bitmap logo) {
        toolbar.setLogo(new BitmapDrawable(getResources(), logo));
    }
}
