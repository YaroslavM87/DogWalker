package com.yaroslavm87.dogwalker.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.Objects;

public class FragmentDogList extends Fragment implements DogListAdapter.OnViewHolderItemClickListener, View.OnClickListener {

    private AppViewModel appViewModel;
    private RecyclerView dogListView;
    private DogListAdapter dogListAdapter;
    private Dialog dialog;
    private EditText etvDialogContentName, etvDialogContentDescription;
    private FloatingActionButton btnAddDog;
    private Button btnDialogCancel, btnDialogSubmit;
    private ImageButton btnSortByName, btnSortByDate;
    private OnDogListItemClickListener onDogListItemClickListener;
    private final int animationType;
    private final String LOG_TAG;

    {
        animationType = ItemAnimation.FADE_IN;
        LOG_TAG = "myLogs";
    }

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dog_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVariables();
        initViewComponents(view);
        subscribeForLiveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onResume() call");
        dogListAdapter.notifyDataSetChanged();
        setToolbar();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onPause() call");
        unsubscribeFromLiveData();
    }

    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()) {

            case "add":
                showCustomDialog();
                break;

            case "sortByName":
                appViewModel.sortName();
                break;

            case "sortByDate":
                appViewModel.sortDate();
                break;

            case "cancel":
                dialog.dismiss();
                break;

            case "submit":
                String name = etvDialogContentName.getText().toString().trim();
                String description = etvDialogContentDescription.getText().toString().trim();
                appViewModel.addNewDog(name, description);
                break;
        }
    }

    @Override
    public void onViewHolderItemClick(View view, int position, Dog dog) {
        // TODO: move this logics to appViewModel
        if(appViewModel.getChosenIndexOfDogFromListLive().getValue() != null) {
//            if(position == (int) appViewModel.getChosenIndexOfDogFromListLive().getValue())
//                return;
            appViewModel.setCurrentChosenDog(dog);
            appViewModel.setCurrentIndexOfChosenDog(position);
            onDogListItemClickListener.onDogListItemClick(FragmentEvents.DOG_LIST_ITEM_CLICKED);
        }
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewComponents(View view) {
        initRecyclerView(view);
        btnAddDog = view.findViewById(R.id.btn_dog_list_add_dog);
        btnAddDog.setOnClickListener(this);
        btnSortByName = view.findViewById(R.id.ibtn_dog_list_sort_name);
        btnSortByName.setOnClickListener(this);
        btnSortByDate= view.findViewById(R.id.ibtn_dog_list_sort_date);
        btnSortByDate.setOnClickListener(this);
    }

    private void initRecyclerView(View view) {
        dogListView = (RecyclerView) view.findViewById(R.id.dog_list_view);
        dogListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dogListAdapter = new DogListAdapter(appViewModel.getDogListReference(), this);
        dogListAdapter.setOnViewHolderItemClickListener(this);
        dogListAdapter.setAnimationType(animationType);
        dogListView.setAdapter(dogListAdapter);
    }

    private void setToolbar() {
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        ActionBar actionBar = Objects.requireNonNull(act.getSupportActionBar());
        actionBar.setTitle(R.string.dog_list_header);
        actionBar.setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(act, R.color.status_action_bar);
    }

    private void subscribeForLiveData() {
        appViewModel.getListOfDogsLive().observe(
                getViewLifecycleOwner(), (dogList) ->
                        dogListAdapter.notifyDataSetChanged());

        appViewModel.getInsertedDogIndexLive().observe(
            getViewLifecycleOwner(),(index) -> {
                    dogListAdapter.notifyItemInserted(index);
                    if(dialog != null) {
                        if(dialog.isShowing())
                        dialog.dismiss();
                    }
                });
        appViewModel.getChangedDogIndexLive().observe(
                getViewLifecycleOwner(),(index) -> dogListAdapter.notifyItemChanged(index));
        appViewModel.getDeletedDogIndexLive().observe(
                getViewLifecycleOwner(),(index) -> dogListAdapter.notifyItemRemoved(index));
//        appViewModel.getModelErrorMessageLive().observe(
//                getViewLifecycleOwner(), (message) ->
//                {
//                    Log.d(LOG_TAG, "FragmentDogList.subscribeViewElements().getModelErrorMessageLive() call");
//                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//                }
//        );
    }

    private void unsubscribeFromLiveData() {
        appViewModel.getListOfDogsLive().removeObservers(getViewLifecycleOwner());
        appViewModel.getInsertedDogIndexLive().removeObservers(getViewLifecycleOwner());
        appViewModel.getChangedDogIndexLive().removeObservers(getViewLifecycleOwner());
        appViewModel.getDeletedDogIndexLive().removeObservers(getViewLifecycleOwner());
    }

    private void showCustomDialog() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dog_list_dialog_new_dog);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        etvDialogContentName = dialog.findViewById(R.id.etv_dog_list_dialog_content_name);
        etvDialogContentDescription = dialog.findViewById(R.id.etv_dog_list_dialog_content_description);
        btnDialogCancel = dialog.findViewById(R.id.btn_dog_list_dialog_cancel);
        btnDialogCancel.setOnClickListener(this);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dog_list_dialog_submit);
        btnDialogSubmit.setOnClickListener(this);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onStart() call");
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