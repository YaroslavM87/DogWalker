package com.yaroslavm87.dogwalker.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yaroslavm87.dogwalker.R;

import java.util.ArrayList;

public class AdapterRecyclerViewSectioned extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_GRID = 1;
    private final int VIEW_TITLE = 0;
    private String[] month;


    private ArrayList<WalkRecordListItem> items;

    private Context ctx;
//    private OnItemClickListener mOnItemClickListener;

//    public interface OnItemClickListener {
//        void onItemClick(View view, SectionImage obj, int position);
//    }

//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mOnItemClickListener = mItemClickListener;
//    }

    public AdapterRecyclerViewSectioned(Context context, ArrayList<WalkRecordListItem> items) {
        this.ctx = context;
        this.items = items;
        month = ctx.getResources().getStringArray(R.array.month);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        GridView gvMonth;
        final AdapterGridView adapterGridView;
        OriginalViewHolder(View v) {
            super(v);
            gvMonth = (GridView) v.findViewById(R.id.walk_records_gridview_view);
            adapterGridView = new AdapterGridView(ctx);
            gvMonth.setAdapter(adapterGridView);
        }

        public void passPayloadToAdapter(WalkRecordListItem item) {
            adapterGridView.setPayload(item);
            //adapterGridView.notifyDataSetChanged();
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonthTitle;
        SectionViewHolder(View v) {
            super(v);
            tvMonthTitle = (TextView) v.findViewById(R.id.month_title);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_GRID) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.walk_records_grid_view, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.walk_records_month_title, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.passPayloadToAdapter(items.get(position));
        }

        else {
            SectionViewHolder view = (SectionViewHolder) holder;
            String content = month[items.get(position).getMonthTitle() - 1]
                    + " "
                    + items.get(position).getYearTitle();
            view.tvMonthTitle.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isTitle() ? VIEW_TITLE : VIEW_GRID;
    }
}
