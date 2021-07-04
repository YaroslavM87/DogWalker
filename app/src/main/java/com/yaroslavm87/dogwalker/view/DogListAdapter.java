package com.yaroslavm87.dogwalker.view;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.Tools;
import com.yaroslavm87.dogwalker.model.Dog;

public class DogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnViewHolderItemClickListener {
        void onViewHolderItemClick(View view, int position, Dog dog);
    }

    class DogListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView dogName;
        TextView dogLastTimeWalk;

        DogListViewHolder(@NonNull View itemView) {
            super(itemView);
            //Log.d(LOG_TAG, "RVAdapter.MyViewHolder() instance just created");

            dogName = itemView.findViewById(R.id.dog_list_name);
            dogLastTimeWalk = itemView.findViewById(R.id.dog_list_description);

            itemView.setOnClickListener(this);
        }

        // TODO: it suggested to implement onClick in onCreateViewHolder - check
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
    private Context ctx;
    private final String LOG_TAG;
    private int animation_type = 0;


    {
        viewHolderLayout = R.layout.dog_list_view_holder;
        LOG_TAG = "myLogs";
    }

    public DogListAdapter(ArrayList<Dog> dogList, Context context) {
        //Log.d(LOG_TAG, "RVAdapter() constructor call");
        this.dogList = dogList;
        ctx = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d(LOG_TAG, "RVAdapter.onCreateViewHolder() call");
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(viewHolderLayout, parent, false);
        vh = new DogListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DogListViewHolder) {
            //Log.d(LOG_TAG, "RVAdapter.onBindViewHolder() call");
            DogListViewHolder originalHolder = (DogListViewHolder) holder;

            Dog dog = dogList.get(position);
//            Tools.displayImageRound(ctx, view.image, p.image);
            originalHolder.dogName.setText(Tools.capitalize(dog.getName()));
            String content = dog.getLastTimeWalk() == 0L ?
                    ctx.getResources().getString(R.string.dog_did_not_walk) :
                    Tools.parseMillsToDate(dog.getLastTimeWalk(), "dd MMMM yyyy");
            originalHolder.dogLastTimeWalk.setText(content);

//            Tools.setColorTextToViewsDependingOnLastTimeWalk(
//                    ctx,
//                    dog.getLastTimeWalk(),
//                    //originalHolder.dogName,
//                    originalHolder.dogLastTimeWalk
//            );

            setAnimation(originalHolder.itemView, position);
        }



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

    public void clearDogList() {
        Log.d(LOG_TAG, "DogListAdapter.clearDogList() call");
        dogList.clear();
    }

    public void setAnimationType(int type) {
        animation_type = type;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }



    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}