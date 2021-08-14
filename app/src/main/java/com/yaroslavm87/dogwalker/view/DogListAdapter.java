package com.yaroslavm87.dogwalker.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.ArrayList;

public class DogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnViewHolderItemClickListener {
        void onViewHolderItemClick(View view, int position, Dog dog);
    }

    class DogListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircularImageView profilePic;
        TextView dogName;
        TextView dogLastTimeWalk;

        DogListViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.civ_dog_list_profile_icon);
            dogName = itemView.findViewById(R.id.tv_dog_list_name);
            dogLastTimeWalk = itemView.findViewById(R.id.tv_dog_list_description);

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
    private Fragment fragment;
    private final String LOG_TAG;
    private int animation_type = 0;
    private int lastPosition = -1;
    private boolean on_attach = true;

    {
        viewHolderLayout = R.layout.dog_list_view_holder;
        LOG_TAG = "myLogs";
    }

    public DogListAdapter(ArrayList<Dog> dogList, Fragment fragment) {
        this.dogList = dogList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(viewHolderLayout, parent, false);
        vh = new DogListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DogListViewHolder) {
            DogListViewHolder originalHolder = (DogListViewHolder) holder;

            Dog dog = dogList.get(position);
            originalHolder.dogName.setText(Tools.capitalize(dog.getName()));
            String content = dog.getLastTimeWalk() == 0L ?
                    fragment.getResources().getString(R.string.dog_did_not_walk) :
                    Tools.parseMillsToDate(dog.getLastTimeWalk(), "dd MMMM yyyy");
            originalHolder.dogLastTimeWalk.setText(content);

            Tools.loadImageWithGlide(fragment, dog, originalHolder.profilePic, R.drawable.profile_pic_placeholder);

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
        return dogList.size();
    }
    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
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

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}