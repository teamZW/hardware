package com.hardware.sample.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hardware.sample.R;

import java.util.ArrayList;


public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CustomBean> mListData;


    public CustomAdapter(Context mContext, ArrayList<CustomBean> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public CustomBean getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_custom, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomBean user = mListData.get(position);
        holder.name.setText(user.getName());
        holder.info.setText(user.getInfo());

        return convertView;
    }

    private class ViewHolder {
        TextView name; //名称
        TextView info; //info
    }

    public void refresh() {
        //this.mListData = mListData;
        notifyDataSetChanged();
    }
}
