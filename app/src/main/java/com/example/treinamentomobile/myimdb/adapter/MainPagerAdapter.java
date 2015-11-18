package com.example.treinamentomobile.myimdb.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.treinamentomobile.myimdb.fragment.ShowInfoFragment_;

/**
 * Created by treinamentomobile on 11/17/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 1;
    private static final CharSequence[] TITLES = {"Show Info", "Episodes"};
    private int showId;

    public MainPagerAdapter(FragmentManager fm, int showId){
        super(fm);
        this.showId = showId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ShowInfoFragment_.builder().showId(showId).build();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
