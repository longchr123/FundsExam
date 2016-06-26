package com.example.administrator.jijin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.jijin.R;
import com.example.administrator.jijin.bean.ProgressItem;

import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class ChapterListAdapter extends BaseAdapter {
    private List<ProgressItem> list;
    private Context context;
    private boolean isXiTi;

    public ChapterListAdapter(List<ProgressItem> list,Context context,boolean isXiTi){
        this.list=list;
        this.context=context;
        this.isXiTi=isXiTi;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            if (isXiTi) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_xiti_list, null);
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_cuoti_list, null);
            }
            vh = new ViewHolder();
            vh.tv = (TextView) convertView.findViewById(R.id.tv);
            vh.pb = (ProgressBar) convertView.findViewById(R.id.pb_xiti);
            vh.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(list.get(position).getTitle());
        if (isXiTi) {
            vh.pb.setMax(list.get(position).getMax());
            vh.pb.setProgress(list.get(position).getCurrent());
            vh.tv_progress.setText(list.get(position).getCurrent() + "/" + list.get(position).getMax());
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv;
        ProgressBar pb;
        TextView tv_progress;
    }
}
