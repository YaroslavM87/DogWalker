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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.WalkRecord;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.viewModel.Tools;

import java.util.LinkedList;
import java.util.Objects;

public class FragmentDogInfo extends Fragment implements View.OnClickListener {

    private AppViewModel appViewModel;
//    private ImageView imgDogPic;
    private TextView tvDogName, tvDogLastWalk, tvDogDescription, tvRecentWalks, tvDialogHeader;
    private EditText etvDialogContent;
    private Button btnDialogCancel, btnDialogSubmit;
    private FloatingActionButton btnWalkDog;
    private ImageButton ibtnCreateDescription, btnSeeWalkRecords;
    private Dialog dialog;
    private OnDogInfoItemClickListener onDogInfoItemClickListener;
    private LinkedList<String> listRecentWalks;
    private final byte MAX_LIST_SIZE;
    private final String LOG_TAG;
    
    {
        MAX_LIST_SIZE = 5;
        LOG_TAG = "myLogs";
    }

    public interface OnDogInfoItemClickListener {
        void onDogInfoItemClick(FragmentDogInfo.FragmentEvents event);
    }

    public enum FragmentEvents{
        WALK_CALL,
        SEE_WALK_RECORDS_CALL,
        REMOVE_FROM_LIST_CALL
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentDogInfo.OnDogInfoItemClickListener) {
            onDogInfoItemClickListener = (FragmentDogInfo.OnDogInfoItemClickListener) context;

        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentDogList.OnDogListItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dog_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "----------- FRAGMENT DOG INFO onViewCreated -----------");
        initVariables();
        initViewComponents(requireView());
        subscribeForLiveData();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "----------------------");
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onResume() call");
        setToolbar();
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        listRecentWalks = new LinkedList<>();
    }

    private void initViewComponents(View view) {
        tvDogName = view.findViewById(R.id.tv_dog_info_dog_name);
        tvDogLastWalk = view.findViewById(R.id.tv_dog_info_last_walk);
        tvDogDescription = view.findViewById(R.id.tv_dog_info_dog_description);
        tvRecentWalks = view.findViewById(R.id.tv_dog_info_recent_walks);
        ibtnCreateDescription = (view.findViewById(R.id.ibtn_dog_description_create));
        ibtnCreateDescription.setOnClickListener(this);
        btnWalkDog = view.findViewById(R.id.btn_dog_info_walk_dog);
        btnWalkDog.setOnClickListener(this);
        btnSeeWalkRecords = view.findViewById(R.id.ibtn_dog_see_walks_list);
        btnSeeWalkRecords.setOnClickListener(this);
//        removeDogFromList = view.findViewById(R.id.dog_info_button_remove_dog);
        //removeDogFromList.setOnClickListener(this);

        Dog dog = appViewModel.getChosenDogFromListLive().getValue();
        if(dog != null) {
            tvDogName.setText(Tools.capitalize(dog.getName()));
            String content = dog.getLastTimeWalk() == 0L ?
                    requireContext().getResources().getString(R.string.dog_did_not_walk) :
                    Tools.parseMillsToDate(dog.getLastTimeWalk(), "dd MMMM yyyy");
            tvDogLastWalk.setText(content);
            tvDogDescription.setText(dog.getDescription());
        }
    }

    private void setToolbar() {
        ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar());
        actionBar.setTitle(R.string.dog_info_header);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void subscribeForLiveData() {
        appViewModel.getWalkTimestampsLive().observe(
                getViewLifecycleOwner(), (walkRecords) -> {
                    fillWalkRecordsList(walkRecords);
                    setTvRecentWalks();
                }
        );

//                    if(walkRecord != null) {
//                        Log.d(LOG_TAG, "FragmentDogInfo.getInsertedWalkRecordLive " + walkRecord.getDogId());
//                        String strLastWalk = Tools.parseMillsToDate(
//                                walkRecord.getTimestamp(),
//                                "dd MMMM yyyy");
//
//                        if(!isInWalkList(strLastWalk)) {
//                            addRecentWalkToList(strLastWalk);
//
//                            StringBuilder result = new StringBuilder();
//                            for(String s : listRecentWalks) {
//                                result.append(s).append("\n");
//                            }
//                            tvRecentWalks.setText(result.toString());
//                        }
//                    }
//                }
//        );
        appViewModel.getChangedDogLive().observe(
                getViewLifecycleOwner(), (dog) -> {
                    Dog chosenDog = appViewModel.getChosenDogFromListLive().getValue();
                    assert dog != null;
                    assert chosenDog != null;
                    if(chosenDog.equals(dog)) {
                        tvDogLastWalk.setText(Tools.parseMillsToDate(dog.getLastTimeWalk(), "dd MMMM yyyy"));
                        tvDogDescription.setText(dog.getDescription());
                    }
                }
        );
    }

    private void fillWalkRecordsList(LinkedList<WalkRecord> walkRecords) {
        listRecentWalks.clear();
        for(WalkRecord wr : walkRecords) {

            if(listRecentWalks.size() >= MAX_LIST_SIZE) break;

            if(wr != null) {
                Log.d(LOG_TAG, "FragmentDogInfo.getRecentWalkTimestampsLive " + wr.getDogId());

                String strLastWalk = Tools.parseMillsToDate(
                        wr.getTimestamp(),
                        "dd MMMM yyyy");

                if(!isInWalkList(strLastWalk)) {
                    listRecentWalks.addLast(strLastWalk);
                }
            }
        }
    }

    private boolean isInWalkList(String walkRecord) {
        if(listRecentWalks.size() > 0 && listRecentWalks.peekFirst() != null) {
            return listRecentWalks.peekLast().equals(walkRecord);
        } else return false;
    }

    private void setTvRecentWalks() {
        if(listRecentWalks.size() == 0) {
            tvRecentWalks.setText(R.string.dog_have_no_walk_records);
            return;
        }        StringBuilder result = new StringBuilder();
        for(String s : listRecentWalks) {
            result.append(s).append("\n");
        }
        tvRecentWalks.setText(result.toString());
    }

    @Override
    public void onClick(View v) {

            switch(v.getTag().toString()) {

                case "walk":
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- WALK DOG BUTTON -----------");
                    Log.d(LOG_TAG, "--");
                    onDogInfoItemClickListener.onDogInfoItemClick(FragmentEvents.WALK_CALL);
                    break;

                case "seeWalks":
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- SEE WALKS BUTTON -----------");
                    Log.d(LOG_TAG, "--");
                    if(Objects.requireNonNull(appViewModel.getChosenDogFromListLive().getValue()).getLastTimeWalk() > 1000L) {
                        onDogInfoItemClickListener.onDogInfoItemClick(FragmentEvents.SEE_WALK_RECORDS_CALL);
                    }
                    break;

                case "removeFromList":
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- REMOVE BUTTON -----------");
                    Log.d(LOG_TAG, "--");
                    onDogInfoItemClickListener.onDogInfoItemClick(FragmentEvents.REMOVE_FROM_LIST_CALL);
                    break;

                case "createDescription":
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- CREATE DSCR -----------");
                    Log.d(LOG_TAG, "--");
                    showCustomDialog();
                    break;

                case "cancel":
                    dialog.dismiss();
                    break;

                case "submit":
                    String review = etvDialogContent.getText().toString().trim();
                    if (review.equals("")) {
                        Toast.makeText(getContext(), R.string.dialog_warning_description_is_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        //appViewModel.updateDescription();
                        dialog.dismiss();
                        Toast.makeText(getContext(), R.string.dog_info_dialog_success, Toast.LENGTH_SHORT).show();
                        appViewModel.updateDogDescription(review);
                        //tvDogDescription.setText(review);
                    }
                    break;
        }
    }

    private void showCustomDialog() {
        dialog = new Dialog(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dog_info_dialog_about);
        dialog.setCancelable(true);

        tvDialogHeader = dialog.findViewById(R.id.tv_dog_info_dialog_about_header);
        tvDialogHeader.setText(Tools.capitalize(Objects.requireNonNull(appViewModel.getChosenDogFromListLive().getValue()).getName()));
        etvDialogContent = dialog.findViewById(R.id.etv_dog_info_dialog_about_content);
        etvDialogContent.setText(Objects.requireNonNull(appViewModel.getChosenDogFromListLive().getValue()).getDescription());
        btnDialogCancel = dialog.findViewById(R.id.btn_dog_info_dialog_cancel);
        btnDialogSubmit = dialog.findViewById(R.id.btn_dog_info_dialog_submit);
        btnDialogCancel.setOnClickListener(this);
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
        appViewModel.resetDogBufferVariables();
    }
}