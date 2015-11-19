package com.example.treinamentomobile.myimdb.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treinamentomobile.myimdb.R;
import com.example.treinamentomobile.myimdb.model.ShowInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EBean;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by treinamentomobile on 11/18/15.
 */
@EBean
public class ListShowAdapter extends AABaseAdapter<ShowInfo> {

    private ViewHolder holder;
    private ShowInfo item;

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

    static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
