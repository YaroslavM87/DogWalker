package com.yaroslavm87.dogwalker.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.viewModel.Functions;

import java.util.ArrayList;

public class WalkRecordsListAdapter extends RecyclerView.Adapter<WalkRecordsListAdapter.WalkRecordListViewHolder> {

    class WalkRecordListViewHolder extends RecyclerView.ViewHolder {

        TextView walkRecordTime;

        WalkRecordListViewHolder(@NonNull View itemView) {
            super(itemView);
            //Log.d(LOG_TAG, "WalkRecordsAdapter.WalkRecordListViewHolder() instance just created");
            walkRecordTime = itemView.findViewById(R.id.view_holder_walk_records_list_timestamp);
        }

    }

    private ArrayList<WalkRecord> walkRecordsList;
    private final int viewHolderLayout;
    private final String LOG_TAG;

    {
        viewHolderLayout = R.layout.view_holder_walk_records_list;
        LOG_TAG = "myLogs";
    }

    public WalkRecordsListAdapter(ArrayList<WalkRecord> walkRecordsList) {
        //Log.d(LOG_TAG, "WalkRecordsAdapter() constructor call");
        this.walkRecordsList = walkRecordsList;
    }

    @NonNull
    @Override
    public WalkRecordsListAdapter.WalkRecordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d(LOG_TAG, "WalkRecordsAdapter.onCreateViewHolder() call");
        return new WalkRecordsListAdapter.WalkRecordListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(viewHolderLayout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WalkRecordsListAdapter.WalkRecordListViewHolder holder, int position) {
        //Log.d(LOG_TAG, "WalkRecordsAdapter.onBindViewHolder() call");
        WalkRecord walkRecord = walkRecordsList.get(position);
        holder.walkRecordTime.setText(Functions.parseMillsToDate(walkRecord.getTimestamp(), "dd MMMM yyyy"));
    }

    @Override
    public int getItemCount() {
        //Log.d(LOG_TAG, "WalkRecordsAdapter.getItemCount() call");
        return walkRecordsList.size();
    }

    public void setWalkRecordsList(ArrayList<WalkRecord> list) {
        Log.d(LOG_TAG, "WalkRecordsListAdapter.setWalkRecordsList() call");
        walkRecordsList = list;
    }

    public void clearWalkRecordList() {
        walkRecordsList.clear();

    }
}