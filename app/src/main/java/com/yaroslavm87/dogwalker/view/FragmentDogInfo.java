package com.yaroslavm87.dogwalker.view;

import androidx.fragment.app.Fragment;
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
import com.yaroslavm87.dogwalker.viewModel.ViewModelDogList;

public class FragmentDogInfo extends Fragment implements View.OnClickListener {

    private ViewModelDogList viewModelDogList;
    private TextView dogInfoDogName, dogInfoDogDescription;
    private Button walkDog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dog_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVariables();
        initViewElements(requireView());
        subscribeViewElements();
    }

    private void initVariables() {
        viewModelDogList = new ViewModelProvider(requireActivity()).get(ViewModelDogList.class);
    }

    private void initViewElements(View view) {
        dogInfoDogName = view.findViewById(R.id.dog_info_dog_name);
        dogInfoDogDescription = view.findViewById(R.id.dog_info_dog_description);
        walkDog = view.findViewById(R.id.walk_dog);
        walkDog.setTag("walk_dog");
        walkDog.setOnClickListener(this);
    }

    private void subscribeViewElements() {

        viewModelDogList.getChosenDogFromListLive().observe(
                getViewLifecycleOwner(),(dog) -> {

                    if(dog != null) {
                        dogInfoDogName.setText(dog.getName());
                        String description = dog.getId() + ", " + dog.getImageResId();
                        dogInfoDogDescription.setText(description);

                    } else {
                        dogInfoDogName.setText("");
                        dogInfoDogDescription.setText("");
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

        Log.d("myTags", "FragmentDogInfo.onClick()");

        switch(v.getTag().toString()) {

            case "walk_dog":

                viewModelDogList.walkDog();
                break;

        }

    }
}