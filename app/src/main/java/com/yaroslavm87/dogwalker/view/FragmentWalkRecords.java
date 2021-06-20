package com.yaroslavm87.dogwalker.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.viewModel.Functions;

import java.util.ArrayList;

public class FragmentWalkRecords extends Fragment {

    private AppViewModel appViewModel;
    private RecyclerView walkRecordsListView;
    private WalkRecordsListAdapter walkRecordsListAdapter;
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
        Log.d(LOG_TAG, "----------- FRAGMENT WALK RECORDS onViewCreated -----------");
        initVariables();
        initViewElements(requireView());
        subscribeViewElements();
    }

    //void clearWalkRecordList() {}

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewElements(View view) {
        walkRecordsListView = (RecyclerView) view.findViewById(R.id.walk_records_list_view);
        walkRecordsListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        walkRecordsListAdapter = new WalkRecordsListAdapter(new ArrayList<>());

        walkRecordsListView.setAdapter(walkRecordsListAdapter);


//        walkRecordsListView.addItemDecoration(new DividerItemDecoration(
//                        view.getContext(),
//                        DividerItemDecoration.VERTICAL
//                )
//        );
//        walkRecordsListView.setItemAnimator(new DefaultItemAnimator());

        walkRecordsListHeader = view.findViewById(R.id.walk_records_list_header);

    }

    private void subscribeViewElements() {

//        appViewModel.getListOfWalkRecordsLive().observe(
//                getViewLifecycleOwner(),(walkRecordsList) -> {
//                    walkRecordsListAdapter.setWalkRecordsList(walkRecordsList);
//                    walkRecordsListAdapter.notifyDataSetChanged();
//                }
//        );

        appViewModel.getChosenDogFromListLive().observe(
                getViewLifecycleOwner(),(dog) -> {

                    if(dog != null) {
                        walkRecordsListAdapter.setWalkRecordsList(appViewModel.getWalkRecordsListReference(dog));
                        walkRecordsListAdapter.notifyDataSetChanged();
                        String headerLine = "Все прогулки питомца " + Functions.capitalize(dog.getName());
                        walkRecordsListHeader.setText(headerLine);

                    } else {
                        // TODO: if fragment still visible then show error
                        walkRecordsListAdapter.clearWalkRecordList();
                        String headerLine = "Error";
                        walkRecordsListHeader.setText(headerLine);
                    }
                }
        );

        appViewModel.getInsertedWalkRecordIndexLive().observe(
                getViewLifecycleOwner(),(index) -> {
                    walkRecordsListAdapter.notifyItemInserted(index);
                }
        );
    }
}