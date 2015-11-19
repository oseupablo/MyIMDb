package com.example.treinamentomobile.myimdb.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phsil on 11/18/15.
 * This is a generic BaseAdapter
 * Can be used by any list which uses an adapter with
 * this format. Must implement getView method. Uses
 * android annotation to enable @Bean injection in non
 * standard android components and to handle some
 * dependencies.
 */
@EBean
public abstract class AABaseAdapter<T> extends BaseAdapter {

    /**
     * Injection of the root context
     */
    @RootContext
    Context context;

    private List<T> list;

    @Override
    public int getCount() {
        return getList().size();
    }

    @Override
    public T getItem(int i) {
        return getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public List<T> getList() {
        if (list == null) {
            list = new ArrayList<T>();
        }
        return list;
    }

    /**
     *
     * @param newList
     */
    public void setList(List<T> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    /**
     * @UiThread annotation guarantees that
     * this method will be called from Main thread
     * despite any background tasks were executing
     */
    @Override
    @UiThread
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}