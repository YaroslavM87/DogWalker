package com.yaroslavm87.dogwalker.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.ViewModel.ViewModelDogList;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.Model;

import java.util.ArrayList;

public class ActivityDogList extends AppCompatActivity implements View.OnClickListener, RVAdapter.OnViewHolderItemClickListener {

    private ViewModelDogList viewModelDogList;
    private Button addDogButton;
    private Button deleteDogButton;
    private EditText dogNameEditText;
    private int chosenDogFromList_index;
    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;
    private final String LOG_TAG = "myLogs";

    {
        this.chosenDogFromList_index = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_list);

        viewModelDogList = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
        ).get(ViewModelDogList.class);

        recyclerView = findViewById(R.id.recyclerView);
        rvAdapter = new RVAdapter(new ArrayList<>(), R.layout.layout_for_view_holder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rvAdapter);

        addDogButton = findViewById(R.id.addDog);
        addDogButton.setTag("addDogButton");

        deleteDogButton = findViewById(R.id.deleteDog);
        deleteDogButton.setTag("deleteDogButton");

        dogNameEditText = findViewById(R.id.dogNameEditText);

        viewModelDogList.getListOfDogsLive().observe(this, dogList -> {
            rvAdapter.setDogList(dogList);
            rvAdapter.notifyDataSetChanged();
        });

        viewModelDogList.getInsertedDogIndexLive().observe(this, dogIndex -> {
            Log.d(LOG_TAG, "notifyItemInserted() call");
            rvAdapter.notifyItemInserted(dogIndex);
        });

        viewModelDogList.getDeletedDogIndexLive().observe(this, dogIndex -> {
            Log.d(LOG_TAG, "notifyItemRemoved() call");
            rvAdapter.notifyItemRemoved(dogIndex);
        });

        rvAdapter.setOnViewHolderItemClickListener(this);

        findViewById(R.id.addDog).setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()) {

            case "addDogButton":

                String s = this.dogNameEditText.getText().toString();

                if(s.toCharArray().length > 1) {

                    this.viewModelDogList.addNewDog(s);
                    this.dogNameEditText.setText("");
                }
                break;

            case "deleteDogButton":

                if(this.chosenDogFromList_index > -1) {

                    this.viewModelDogList.deleteDog(this.chosenDogFromList_index);
                    this.chosenDogFromList_index = -1;
                }
                break;
        }

    }

    @Override
    public void onViewHolderItemClick(int position) {

        this.chosenDogFromList_index = position;
    }
}