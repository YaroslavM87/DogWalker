package com.yaroslavm87.dogwalker.view;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.ViewModelDogList;
import com.yaroslavm87.dogwalker.model.Dog;

import java.util.ArrayList;

public class FragmentDogList extends Fragment implements DogListAdapter.OnViewHolderItemClickListener {

    private ViewModelDogList viewModelDogList;
    private RecyclerView dogListView;
    private DogListAdapter dogListAdapter;
    private FragmentDogList.OnFragmentViewClickListener onFragmentViewClickListener;
    private String LOG_TAG;

    // Log.d(LOG_TAG, "FragmentDogList *** call -> ");

    public interface OnFragmentViewClickListener {
        void onFragmentViewClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentDogList.OnFragmentViewClickListener) {
            onFragmentViewClickListener = (FragmentDogList.OnFragmentViewClickListener) context;

        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentDogList.OnFragmentViewClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d(LOG_TAG, "FragmentDogList.onCreateView() call");
        return inflater.inflate(R.layout.fragment_dog_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVariables();
        initViewElements(requireView());
        subscribeViewElements();
    }

    @Override
    public void onViewHolderItemClick(View view, int position, Dog dog) {
        viewModelDogList.setCurrentChosenDog(dog);
        viewModelDogList.setCurrentIndexOfChosenDog(position);
        onFragmentViewClickListener.onFragmentViewClick();
    }

    private void initVariables() {
        viewModelDogList = new ViewModelProvider(requireActivity()).get(ViewModelDogList.class);
        LOG_TAG = "myLogs";
    }

    private void initViewElements(View view) {

        dogListView = (RecyclerView) view.findViewById(R.id.dog_list_view);
        dogListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dogListAdapter = new DogListAdapter(new ArrayList<>());
        dogListAdapter.setOnViewHolderItemClickListener(this);

        dogListView.setAdapter(dogListAdapter);
        dogListView.addItemDecoration(new DividerItemDecoration(
                view.getContext(),
                DividerItemDecoration.VERTICAL
                )
        );
        dogListView.setItemAnimator(new DefaultItemAnimator());
    }

    private void subscribeViewElements() {

        viewModelDogList.getListOfDogsLive().observe(
                getViewLifecycleOwner(),(dogList) -> {
                    dogListAdapter.setDogList(dogList);
                    dogListAdapter.notifyDataSetChanged();
                }
        );

        viewModelDogList.getInsertedDogIndexLive().observe(
        getViewLifecycleOwner(),(index) -> {
            dogListAdapter.notifyItemInserted(index);
            Log.d(LOG_TAG, "FragmentDogList.notifyItemInserted() call");
                });

        viewModelDogList.getChangedDogIndexLive().observe(
                getViewLifecycleOwner(),(index) -> dogListAdapter.notifyItemChanged(index));

        viewModelDogList.getDeletedDogIndexLive().observe(
                getViewLifecycleOwner(),(index) -> dogListAdapter.notifyItemRemoved(index));
    }


//    void receiveListOfDogs(ArrayList<Dog> dogList){
//        dogListAdapter.setDogList(dogList);
//        dogListAdapter.notifyDataSetChanged();
//    }
//
//    void receiveIndexOfDogInsertedInList(int dogIndex) {
//        dogListAdapter.notifyItemInserted(dogIndex);
//    }
//
//    void receiveIndexOfDogInListChanged(int dogIndex) {
//        dogListAdapter.notifyItemChanged(dogIndex);
//    }
//
//    void receiveIndexOfDogInListDeleted(int dogIndex) {
//        dogListAdapter.notifyItemRemoved(dogIndex);
//    }

}