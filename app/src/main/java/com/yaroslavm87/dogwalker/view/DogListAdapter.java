package com.yaroslavm87.dogwalker.view;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.Functions;
import com.yaroslavm87.dogwalker.model.Dog;

public class DogListAdapter extends RecyclerView.Adapter<DogListAdapter.DogListViewHolder> {

    public interface OnViewHolderItemClickListener {
        void onViewHolderItemClick(View view, int position, Dog dog);
    }

    class DogListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dogName;
        TextView dogLastTimeWalk;

        DogListViewHolder(@NonNull View itemView) {
            super(itemView);
            //Log.d(LOG_TAG, "RVAdapter.MyViewHolder() instance just created");

            dogName = itemView.findViewById(R.id.view_holder_dog_list_name);
            dogLastTimeWalk = itemView.findViewById(R.id.view_holder_dog_list_last_time_walk);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (onViewHolderItemClickListener != null) {
                onViewHolderItemClickListener.onViewHolderItemClick(
                        v,
                        getLayoutPosition(),
                        dogList.get(getLayoutPosition())
                );
            }
        }
    }

    private ArrayList<Dog> dogList;
    private OnViewHolderItemClickListener onViewHolderItemClickListener;
    private final int viewHolderLayout;
    private final String LOG_TAG;

    {
        viewHolderLayout = R.layout.view_holder;
        LOG_TAG = "myLogs";
    }

    public DogListAdapter(ArrayList<Dog> dogList) {
        //Log.d(LOG_TAG, "RVAdapter() constructor call");
        this.dogList = dogList;
    }

    @NonNull
    @Override
    public DogListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d(LOG_TAG, "RVAdapter.onCreateViewHolder() call");

        return new DogListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(viewHolderLayout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DogListViewHolder holder, int position) {
        //Log.d(LOG_TAG, "RVAdapter.onBindViewHolder() call");

        Dog dog = dogList.get(position);

        holder.dogName.setText(dog.getName());
        holder.dogLastTimeWalk.setText(Functions.parseMillsToDate(dog.getLastTimeWalk()));

        Functions.setColorToViewsDependingOnLastTimeWalk(
                dog.getLastTimeWalk(),
                holder.dogName,
                holder.dogLastTimeWalk
        );
    }

    @Override
    public int getItemCount() {
        //Log.d(LOG_TAG, "RVAdapter.getItemCount() call");
        return dogList.size();
    }
    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
        // Log.d(LOG_TAG, "RVAdapter.setOnViewHolderItemClickListener() call");
        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }

    public void setDogList(ArrayList<Dog> dogList) {
        Log.d(LOG_TAG, "DogListAdapter.setDogList() call");
        this.dogList = dogList;
    }
}