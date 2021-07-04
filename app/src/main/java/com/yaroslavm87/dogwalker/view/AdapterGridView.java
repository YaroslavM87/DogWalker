package com.yaroslavm87.dogwalker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.yaroslavm87.dogwalker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterGridView  extends BaseAdapter {

    private int[] setOfDays;
    private Integer[] walkDays;
    private LayoutInflater layoutInflater;
    private Context context;

    {
        setOfDays = new int[0];
        walkDays = new Integer[0];
    }

    public AdapterGridView(Context ctx) {
        this.context = ctx;
        layoutInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return setOfDays.length;
    }

    @Override
    public Object getItem(int position) {
        return setOfDays[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.walk_records_calendar_item, null);
            holder = new ViewHolder();
            holder.tvDay = (TextView) convertView.findViewById(R.id.calendar_day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int monthDay = setOfDays[position];
        if(monthDay > 0 && monthDay <= 31) {
            holder.tvDay.setText(String.valueOf(monthDay));
            if(Arrays.stream(walkDays).anyMatch((i) -> (i == monthDay))
            ) {
                holder.tvDay.setBackground(AppCompatResources.getDrawable(context, R.drawable.bgr_calendar_day_focused));
            }
        } else {
            holder.tvDay.setBackground(AppCompatResources.getDrawable(context, R.drawable.bgr_calendar_day_empty));
        }

        return convertView;
    }

    public void setPayload(WalkRecordListItem item) {
        setOfDays = item.getSetOfDays();
        walkDays = item.getWalkDays();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvDay;
    }
}
