package com.yaroslavm87.dogwalker;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;

import com.yaroslavm87.dogwalker.model.Dog;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextView1;
        TextView myTextView2;

        MyViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(this);

            myTextView1 = itemView.findViewById(R.id.dogId);
            myTextView2 = itemView.findViewById(R.id.dogName);

            Log.d(LOG_TAG, "RVAdapter.MyViewHolder() instance just created");
        }

        @Override
        public void onClick(View v) {

            if (onEntryClickListener != null) {

                onEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    private ArrayList<Dog> dogList;
    private OnEntryClickListener onEntryClickListener;
    private int layout;
    private final String LOG_TAG = "myLogs";


    public RVAdapter(ArrayList<Dog> dogList, int layout) {

        Log.d(LOG_TAG, "RVAdapter() constructor call");

        this.dogList = dogList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(LOG_TAG, "RVAdapter.onCreateViewHolder() call");

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.MyViewHolder holder, int position) {

        Log.d(LOG_TAG, "RVAdapter.onBindViewHolder() call");

        Dog dog = dogList.get(position);

        holder.myTextView1.setText(String.valueOf(dog.getId()));
        holder.myTextView2.setText(dog.getName());
    }

    @Override
    public int getItemCount() {

        Log.d(LOG_TAG, "RVAdapter.getItemCount() call");

        return dogList.size();
    }

    /*
    the following interface and the method allow to set any method declared in any class through declaring 'OnEntryClickListener' interface
    Example:

        myAdapter.setOnEntryClickListener(new MyAdapter.OnEntryClickListener() {

            @Override
            public void onEntryClick(View view, int position) {

                // some actions
            }
    */
    public interface OnEntryClickListener {

        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {

        this.onEntryClickListener = onEntryClickListener;
    }

    public void setDogList(ArrayList<Dog> dogList) {

        Log.d(LOG_TAG, "RVAdapter.setDogList() call");

        this.dogList = dogList;
    }

    public void setLayout(int layout) {

        this.layout = layout;
    }
}
