package com.hardware.wifi.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hardware.wifi.R;

import java.util.ArrayList;


public class WifiAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<BindSmartBean> mListData;


    public WifiAdapter(Context mContext, ArrayList<BindSmartBean> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public BindSmartBean getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BindSmartBean bean = mListData.get(position);
        holder.name.setText(bean.getSSID());

        return convertView;
    }

    private class ViewHolder {
        TextView name; //名称
    }

    public void refresh() {
        //this.mListData = mListData;
        notifyDataSetChanged();
    }
}
