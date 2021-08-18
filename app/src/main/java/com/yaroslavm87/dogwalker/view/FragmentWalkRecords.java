package com.yaroslavm87.dogwalker.view;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FragmentWalkRecords extends Fragment {

    private AppViewModel appViewModel;
    private RecyclerView walkRecordsListView;
    //private WalkRecordsListAdapter walkRecordsListAdapter;
    private WalkRecordListAdapterSectioned walkRecordListAdapterSectioned;

    private TextView walkRecordsListHeader;
    private String LOG_TAG = "myLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walk_records_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onViewCreated() call");
        initVariables();
        initViewElements(view);
        setToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onResume() call");
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewElements(View view) {
        initRecyclerView(view);
    }

    private void setToolbar() {
        ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar());
        actionBar.setTitle(R.string.walk_list_header);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView(View view) {
        walkRecordsListView = (RecyclerView) view.findViewById(R.id.walk_records_list_view);
        walkRecordsListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<WalkRecord> wrList = Objects.requireNonNull(appViewModel.getWalkTimestampsLive().getValue());

        long[] walkTimestamps = wrList
                .stream()
                .distinct().collect(Collectors.toList())
                .stream().mapToLong(WalkRecord::getTimestamp)
                .filter(ts -> {
                    long threeMonthEarlier = Tools.getMomentOfStartMonth(
                            wrList.get(wrList.size() - 1).getTimestamp() - (86400000L * 90)
                    );
                    Log.d(LOG_TAG, Tools.parseMillsToDate(threeMonthEarlier, "dd MMMM yyyy"));
                    return ts >= threeMonthEarlier;
                })
                .toArray();

        ArrayList<WalkRecordListItem> itemList = Tools.generateWalkCalendar(walkTimestamps);

        Tools.printCalendar(itemList);

        walkRecordListAdapterSectioned = new WalkRecordListAdapterSectioned(
                requireContext(),
                itemList
        );
        walkRecordsListView.setAdapter(walkRecordListAdapterSectioned);

//        LinkedList<WalkRecord> list = appViewModel.getWalkTimestampsLive().getValue();
//
//        if(list != null) {
//            long[] walkTimestamps = new long[list.size()];
//            int counter = list.size() - 1;
//            for(int i = 0; i < list.size(); i++) {
//                walkTimestamps[i] = list.get(counter).getTimestamp();
//                --counter;
//            }
//            ArrayList<WalkRecordListItem> itemList = Tools.generateWalkCalendar(walkTimestamps);
//
//            Tools.printCalendar(itemList);
//
//            walkRecordListAdapterSectioned = new WalkRecordListAdapterSectioned(
//                    requireContext(),
//                    itemList
//            );
//            walkRecordsListView.setAdapter(walkRecordListAdapterSectioned);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onStart() call");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onPause() call");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onStop() call");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onDestroy() call");
    }
}