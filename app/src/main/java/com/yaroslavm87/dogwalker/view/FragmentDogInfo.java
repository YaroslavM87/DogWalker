package com.yaroslavm87.dogwalker.view;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
import com.yaroslavm87.dogwalker.viewModel.Functions;

public class FragmentDogInfo extends Fragment implements View.OnClickListener {

    private AppViewModel appViewModel;
    private TextView dogInfoDogName, dogInfoDogDescription, dogInfoDogLastWalk;
    private Button walkDog;
    private Button seeWalkRecords;
    private OnDogInfoItemClickListener onDogInfoItemClickListener;
    private final StringBuilder SB;
    private final String LOG_TAG;



    {
        SB = new StringBuilder();
        LOG_TAG = "myLogs";
    }

    public interface OnDogInfoItemClickListener {
        void onDogInfoItemClick(FragmentDogInfo.FragmentEvents event);
    }

    public enum FragmentEvents{
        WALK_DOG_CALL,
        SEE_WALK_RECORDS_CALL
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
        initViewElements(requireView());
        subscribeViewElements();
    }

    //void clearWalkRecordList() {}

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewElements(View view) {
        dogInfoDogName = view.findViewById(R.id.dog_info_textview_dog_name);
        dogInfoDogDescription = view.findViewById(R.id.dog_info_dog_description);
        dogInfoDogLastWalk = view.findViewById(R.id.dog_info_dog_last_walk);
        walkDog = view.findViewById(R.id.dog_info_button_walk_dog);
        walkDog.setOnClickListener(this);
        seeWalkRecords = view.findViewById(R.id.dog_info_button_see_walk_records);
        seeWalkRecords.setOnClickListener(this);
    }

    private void subscribeViewElements() {
        StringBuilder sb = new StringBuilder();

        appViewModel.getChosenDogFromListLive().observe(
                getViewLifecycleOwner(),(dog) -> {

                    if(dog != null) {
                        dogInfoDogName.setText(dog.getName());
                        dogInfoDogDescription.setText(buildDogDescription(dog));
                        dogInfoDogLastWalk.setText(
                                sb.append(getResources().getString(R.string.dog_last_walk))
                                        .append("  ")
                                .append(Functions.parseMillsToDate(dog.getLastTimeWalk(), "dd MMMM yyyy")).toString()
                        );

                    } else {
                        // TODO: hide button
                        dogInfoDogName.setText("");
                        dogInfoDogDescription.setText("");
                    }
                }
        );
    }

        @Override
    public void onClick(View v) {

            switch(v.getTag().toString()) {

            case "walkDog":
                Log.d(LOG_TAG, "--");
                Log.d(LOG_TAG, "--");
                Log.d(LOG_TAG, "--");
                Log.d(LOG_TAG, "----------- WALK DOG BUTTON -----------");
                Log.d(LOG_TAG, "--");
                appViewModel.walkDog();
                break;

                case "seeWalkRecords":
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "--");
                    Log.d(LOG_TAG, "----------- SEE WALKS BUTTON -----------");
                    Log.d(LOG_TAG, "--");
                    onDogInfoItemClickListener.onDogInfoItemClick(FragmentEvents.SEE_WALK_RECORDS_CALL);
                    break;
        }
    }

    private String buildDogDescription(Dog dog) {

        String result;

        if(dog.getDescription() == null) {
            result = buildDefaultDogDescription();

        } else {
            result = dog.getDescription();
        }

        return result;
    }

    private String buildDefaultDogDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.dog_character))
                .append(getResources().getString(R.string.dog_age))
                .append(getResources().getString(R.string.dog_weight))
                .append(getResources().getString(R.string.dog_color))
                .append(getResources().getString(R.string.dog_home));
        return sb.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onStart() call");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.getClass().getCanonicalName() + ".onResume() call");
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