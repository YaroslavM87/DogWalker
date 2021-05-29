package com.yaroslavm87.dogwalker.View;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewDogName;
        TextView textViewDogLastTimeWalk;

        MyViewHolder(@NonNull View itemView) {

            super(itemView);

            //Log.d(LOG_TAG, "RVAdapter.MyViewHolder() instance just created");

            itemView.setOnClickListener(this);

            textViewDogName = itemView.findViewById(R.id.dogName);
            textViewDogLastTimeWalk = itemView.findViewById(R.id.dogLastTimeWalk);
        }

        @Override
        public void onClick(View v) {

            if (onViewHolderItemClickListener != null) {

                onViewHolderItemClickListener.onViewHolderItemClick(this.getLayoutPosition());
            }
        }
    }

    private ArrayList<Dog> dogList;
    private OnViewHolderItemClickListener onViewHolderItemClickListener;
    private int layout;
    private final String LOG_TAG = "myLogs";


    public RVAdapter(ArrayList<Dog> dogList, int layout) {

        //Log.d(LOG_TAG, "RVAdapter() constructor call");

        this.dogList = dogList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Log.d(LOG_TAG, "RVAdapter.onCreateViewHolder() call");

        return new MyViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.MyViewHolder holder, int position) {

        //Log.d(LOG_TAG, "RVAdapter.onBindViewHolder() call");

        Dog dog = dogList.get(position);

        holder.textViewDogName.setText(dog.getName());

        String date;

        if(dog.getLastTimeWalk() == 0L) {

            date = "Еще не гулял(а)";

        } else {

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy в hh:mm", Locale.ENGLISH);


            date = formatter.format(dog.getLastTimeWalk());
            //date = new Date(dog.getLastTimeWalk()).toString();
        }

        holder.textViewDogLastTimeWalk.setText(date);
    }

    @Override
    public int getItemCount() {

        //Log.d(LOG_TAG, "RVAdapter.getItemCount() call");

        return dogList.size();
    }

    public interface OnViewHolderItemClickListener {

        void onViewHolderItemClick(int position);
    }

    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {

        Log.d(LOG_TAG, "RVAdapter.setOnViewHolderItemClickListener() call");

        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }

    public void setDogList(ArrayList<Dog> dogList) {

        //Log.d(LOG_TAG, "RVAdapter.setDogList() call");

        this.dogList = dogList;
    }

    public void setLayout(int layout) {

        this.layout = layout;
    }
}