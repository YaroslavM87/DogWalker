package com.yaroslavm87.dogwalker.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.Shelter;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.ArrayList;

public class ShelterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnHolderItemClickListener {
        void onHolderItemClick(View view, int position, Shelter shelter);
    }

    class ShelterListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvShelterName;
        TextView tvShelterAddress;

        ShelterListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShelterName = itemView.findViewById(R.id.tv_shelter_list_name);
            tvShelterAddress = itemView.findViewById(R.id.tv_shelter_list_address);

            itemView.setOnClickListener(this);
        }

        // TODO: it suggested to implement onClick in onCreateViewHolder - check
        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onHolderItemClick(
                        v,
                        getLayoutPosition(),
                        shelterList.get(getLayoutPosition())
                );
            }
        }
    }

    private ArrayList<Shelter> shelterList;
    private OnHolderItemClickListener onClickListener;
    private final int viewHolderLayout;
    private final String LOG_TAG;
    private int animation_type = 0;
    private int lastPosition = -1;
    private boolean on_attach = true;

    {
        viewHolderLayout = R.layout.shelter_list_view_holder;
        LOG_TAG = "myLogs";
    }

    public ShelterListAdapter(ArrayList<Shelter> shelterList) {
        this.shelterList = shelterList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(viewHolderLayout, parent, false);
        vh = new ShelterListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ShelterListViewHolder) {
            ShelterListViewHolder originalHolder = (ShelterListViewHolder) holder;
            Shelter shelter = shelterList.get(position);
            originalHolder.tvShelterName.setText(Tools.capitalize(shelter.getName()));
            //originalHolder.tvShelterAddress.setText(Tools.capitalize(shelter.getAddress()));
            setAnimation(originalHolder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return shelterList.size();
    }

    public void setOnViewHolderItemClickListener(OnHolderItemClickListener listener) {
        onClickListener = listener;
    }

    public void setAnimationType(int type) {
        animation_type = type;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(rv);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}