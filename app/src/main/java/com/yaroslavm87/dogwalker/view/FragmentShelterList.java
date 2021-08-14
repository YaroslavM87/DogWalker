package com.yaroslavm87.dogwalker.view;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.Shelter;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.Objects;

public class FragmentShelterList
        extends Fragment
        implements ShelterListAdapter.OnHolderItemClickListener, View.OnClickListener {

    private AppViewModel appViewModel;
    private ShelterListAdapter shelterListAdapter;
    private Dialog dialog;
    private EditText etvDialogContentName, etvDialogContentAddress;
    private OnComponentClickListener onComponentClickListener;
    private final int animationType;
    private final String LOG_TAG;

    {
        animationType = ItemAnimation.RIGHT_LEFT;
        LOG_TAG = "myLogs";
    }

    public interface OnComponentClickListener {
        void onComponentClick(Events event);
    }

    public enum Events {
        ADD_SHELTER_CALL,
        SHELTER_LIST_ITEM_CLICKED
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnComponentClickListener) {
            onComponentClickListener = (OnComponentClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_shelter_list, container, false);
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
        shelterListAdapter.notifyDataSetChanged();
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

            case "cancel":
                dialog.dismiss();
                break;

            case "submit":
                String name = etvDialogContentName.getText().toString().trim();
                String address = etvDialogContentAddress.getText().toString().trim();
                appViewModel.addNewShelter(name, address);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onHolderItemClick(View view, int position, Shelter shelter) {
        // TODO: move this logics to appViewModel
        appViewModel.setCurrentShelterId(shelter.getId());
        onComponentClickListener.onComponentClick(Events.SHELTER_LIST_ITEM_CLICKED);
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewComponents(View view) {
        initRecyclerView(view);
        view.findViewById(R.id.btn_shelter_list_add).setOnClickListener(this);
        //btnAddShelter.setOnClickListener(this);
    }

    private void initRecyclerView(View view) {
        RecyclerView shelterListView = (RecyclerView) view.findViewById(R.id.rv_shelter_list);
        shelterListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shelterListAdapter = new ShelterListAdapter(appViewModel.getShelterListReference());
        shelterListAdapter.setOnViewHolderItemClickListener(this);
        shelterListAdapter.setAnimationType(animationType);
        shelterListView.setAdapter(shelterListAdapter);
    }

    private void setToolbar() {
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        ActionBar actionBar = Objects.requireNonNull(act.getSupportActionBar());
        actionBar.setTitle(R.string.shelter_list_header);
        actionBar.setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(act, R.color.status_action_bar);
    }

    private void subscribeForLiveData() {
        appViewModel.getListOfSheltersLive().observe(
                getViewLifecycleOwner(),
                (shelterList) -> shelterListAdapter.notifyDataSetChanged()
        );
        appViewModel.getInsertedShelterIndexLive().observe(
            getViewLifecycleOwner(),
            (index) -> {
                shelterListAdapter.notifyItemInserted(index);
                if(dialog != null && dialog.isShowing()) dialog.dismiss();
            }
        );

//        appViewModel.getChangedShelterIndexLive().observe(
//                getViewLifecycleOwner(),(index) -> shelterListAdapter.notifyItemChanged(index));
//
//        appViewModel.getDeletedShelterIndexLive().observe(
//                getViewLifecycleOwner(),(index) -> shelterListAdapter.notifyItemRemoved(index));

        appViewModel.getModelMessageLive().observe(
            getViewLifecycleOwner(),
            (msg) -> {
                if(!msg.equals("")) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void unsubscribeFromLiveData() {
        appViewModel.getListOfSheltersLive().removeObservers(getViewLifecycleOwner());
        appViewModel.getInsertedShelterIndexLive().removeObservers(getViewLifecycleOwner());
//        appViewModel.getChangedShelterIndexLive().removeObservers(getViewLifecycleOwner());
//        appViewModel.getDeletedShelterIndexLive().removeObservers(getViewLifecycleOwner());
        appViewModel.getModelMessageLive().removeObservers(getViewLifecycleOwner());
    }

    private void showCustomDialog() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shelter_list_dialog_new_shelter);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        etvDialogContentName = dialog.findViewById(R.id.etv_shelter_list_dialog_content_name);
        etvDialogContentAddress = dialog.findViewById(R.id.etv_shelter_list_dialog_content_address);
        dialog.findViewById(R.id.btn_shelter_list_dialog_cancel).setOnClickListener(this);
        dialog.findViewById(R.id.btn_shelter_list_dialog_submit).setOnClickListener(this);

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