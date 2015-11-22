package com.example.treinamentomobile.myimdb.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treinamentomobile.myimdb.R;
import com.example.treinamentomobile.myimdb.activity.SearchActivity;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.example.treinamentomobile.myimdb.service.ShowIntentService;
import com.example.treinamentomobile.myimdb.service.ShowIntentService_;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Receiver;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by treinamentomobile on 11/18/15.
 */
@EBean
public class ListShowAdapter extends AABaseAdapter<ShowInfo> implements Filterable {

    private ViewHolder holder;
    private ShowInfo item;
    private ModelFilter filter;
    private List<ShowInfo> allModelItemsArray;
    private List<ShowInfo> filteredModelItemsArray;
    private SearchActivity sa;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.show_list_row, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
            holder.name = (TextView) convertView.findViewById(R.id.name_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        item = getItem(position);
        bindViews();

        return convertView;
    }

    private void bindViews() {
        Picasso.with(context)
                .load(item.getMedium_image())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.icon);
        holder.name.setText(item.getName());
    }

    @Override
    public void setList(List<ShowInfo> newList) {
        super.setList(newList);
        allModelItemsArray = new ArrayList<>();
        allModelItemsArray.addAll(newList);
        filteredModelItemsArray = new ArrayList<>();
        filteredModelItemsArray.addAll(allModelItemsArray);
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }


    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0) {
                ArrayList<ShowInfo> filteredItems = new ArrayList<>();

                for(int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    ShowInfo showInfo = allModelItemsArray.get(i);
                    if(showInfo.getName().toLowerCase().contains(constraint)){
                        filteredItems.add(showInfo);
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            } else {
                synchronized (this) {
                    results.values = allModelItemsArray;
                    results.count = allModelItemsArray.size();
                }
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredModelItemsArray = (ArrayList<ShowInfo>) results.values;
            if(filteredModelItemsArray.size() > 0) {
                setResults(filteredModelItemsArray);
            } else {
                sa.startSearching();
                ShowIntentService_.intent(context).searchShow(constraint.toString()).start();
            }
        }
    }

    public void setResults(List<ShowInfo> shows) {
        notifyDataSetChanged();
        getList().clear();
        for (int i = 0, l = shows.size(); i < l; i++)
            getList().add(shows.get(i));
        notifyDataSetInvalidated();
    }

    public void setResults(ShowInfo show) {
        notifyDataSetChanged();
        getList().clear();
        getList().add(show);
        notifyDataSetInvalidated();
    }

    public void setSa(SearchActivity sa) {
        this.sa = sa;
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
