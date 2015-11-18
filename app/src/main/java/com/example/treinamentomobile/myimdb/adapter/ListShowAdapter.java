package com.example.treinamentomobile.myimdb.adapter;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final View loading;

        if(convertView == null) {
            convertView = View.inflate(context, R.layout.show_list_row, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon_image);
            holder.name = (TextView) convertView.findViewById(R.id.name_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        loading = convertView.findViewById(R.id.loading);

        ShowInfo item = getItem(position);
        loading.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(item.getMedium_image())
                .into(holder.icon, new Callback() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        holder.name.setText(item.getName());

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
