package com.yaroslavm87.dogwalker.view;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.model.Dog;

public class FragmentDogList extends Fragment implements DogListAdapter.OnViewHolderItemClickListener, View.OnClickListener {

    private AppViewModel appViewModel;
    private RecyclerView dogListView;
    private DogListAdapter dogListAdapter;
    private Button addDogButton;
    private EditText dogNameEditText;
    private OnDogListItemClickListener onDogListItemClickListener;
    private int animation_type = ItemAnimation.FADE_IN;
    private String LOG_TAG;

    // Log.d(LOG_TAG, "FragmentDogList *** call -> ");

    public interface OnDogListItemClickListener {
        void onDogListItemClick(FragmentDogList.FragmentEvents event);
    }

    public enum FragmentEvents{
        ADD_DOG_CALL,
        DOG_LIST_ITEM_CLICKED
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnDogListItemClickListener) {
            onDogListItemClickListener = (OnDogListItemClickListener) context;

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
    public void onClick(View v) {

        switch (v.getTag().toString()) {

            case "addDogButton":
                String s = dogNameEditText.getText().toString();
                if(s.toCharArray().length > 1) {
                    appViewModel.addNewDog(s);
                    dogNameEditText.setText("");
                }

                onDogListItemClickListener.onDogListItemClick(FragmentEvents.ADD_DOG_CALL);
                break;
        }
    }

    @Override
    public void onViewHolderItemClick(View view, int position, Dog dog) {

        // TODO: move this logics to appViewModel
        if(appViewModel.getChosenIndexOfDogFromListLive().getValue() != null) {
            if(position == (int) appViewModel.getChosenIndexOfDogFromListLive().getValue())
                return;

            appViewModel.setCurrentChosenDog(dog);
            appViewModel.setCurrentIndexOfChosenDog(position);
            onDogListItemClickListener.onDogListItemClick(FragmentEvents.DOG_LIST_ITEM_CLICKED);
        }
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        LOG_TAG = "myLogs";
    }

    private void initViewElements(View view) {

        dogListView = (RecyclerView) view.findViewById(R.id.dog_list_view);
        dogListView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dogListAdapter = new DogListAdapter(appViewModel.getDogListReference());
        dogListAdapter.setOnViewHolderItemClickListener(this);
        dogListAdapter.setAnimationType(animation_type);

        dogListView.setAdapter(dogListAdapter);
//        dogListView.addItemDecoration(new DividerItemDecoration(
//                view.getContext(),
//                DividerItemDecoration.VERTICAL
//                )
//        );
//        dogListView.setItemAnimator(new DefaultItemAnimator());

        dogNameEditText = view.findViewById(R.id.dog_list_dog_name_edit_text);

        addDogButton = view.findViewById(R.id.dog_list_add_dog_button);
        addDogButton.setTag("addDogButton");
        addDogButton.setOnClickListener(this);
    }

    private void subscribeViewElements() {

//        appViewModel.getListOfDogsLive().observe(
//                getViewLifecycleOwner(),(dogList) -> {
//                    dogListAdapter.setDogList(dogList);
//                    dogListAdapter.notifyDataSetChanged();
//                }
//        );

        appViewModel.getInsertedDogIndexLive().observe(
        getViewLifecycleOwner(),(index) -> {
                    Log.d(LOG_TAG, "FragmentDogList.subscribeViewElements().getInsertedDogIndexLive() call");
                    //dogListAdapter.notifyItemInserted(index);
                    dogListAdapter.notifyDataSetChanged();
                });

        appViewModel.getChangedDogIndexLive().observe(
                getViewLifecycleOwner(),(index) -> dogListAdapter.notifyItemChanged(index));

        appViewModel.getDeletedDogIndexLive().observe(
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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onStart() call");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onResume() call");
        dogListAdapter.notifyDataSetChanged();
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