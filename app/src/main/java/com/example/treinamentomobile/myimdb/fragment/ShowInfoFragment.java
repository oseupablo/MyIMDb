package com.example.treinamentomobile.myimdb.fragment;

import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.example.treinamentomobile.myimdb.R;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.util.StringUtils;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by treinamentomobile on 11/17/15.
 */
@EFragment(R.layout.fragment_show_info)
public class ShowInfoFragment extends Fragment {

    @FragmentArg
    int showId;

    @ViewById
    TextView name;

    @ViewById
    TextView year;

    @ViewById
    ImageView star;

    @ViewById
    TextView rating;

    @ViewById
    TextView genres;


    private ShowInfo mShowInfo;

    @AfterViews
    public void init() {
        fetchData();
    }

    @Background
    public void fetchData() {
        mShowInfo = new ShowInfo();
        mShowInfo = mShowInfo.getById(showId);
        populateViews();
    }

    @UiThread
    public void populateViews() {
        name.setText(mShowInfo.getName());
        year.setText(getYearString(mShowInfo.getPremiered()));
        getStarRating();
        genres.setText(getGenres());
    }

    private String getGenres() {
        String genres = "";
        List<String> genresList =  mShowInfo.getGenres();

        for (int i = 0; i < genresList.size() && i < 2; i++ ) {
            if(i < genresList.size() - 1)
                genres += genresList.get(i) + ", ";
            else
                genres += genresList.get(i);
        }

        return genres;
    }

    private void getStarRating() {
        Picasso.with(getContext())
                .load(R.drawable.star)
                .into(star);
    }

    private String getYearString(Date premiered) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(premiered);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    private ShowInfo getShowInfo() {
        return (ShowInfo) new Select()
                .from(ShowInfo.class)
                .where("id = ?", showId)
                .executeSingle();
    }

}
