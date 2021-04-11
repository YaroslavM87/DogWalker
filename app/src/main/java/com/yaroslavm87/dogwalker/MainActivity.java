package com.yaroslavm87.dogwalker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.yaroslavm87.dogwalker.ViewModel.MyViewModel;
import com.yaroslavm87.dogwalker.model.Dog;
import com.yaroslavm87.dogwalker.model.EntitiesComEnv;
import com.yaroslavm87.dogwalker.repository.DatabaseAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EntitiesComEnv model;
    private MyViewModel myViewModel;
    RecyclerView recyclerView;
    RVAdapter rvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        model = EntitiesComEnv.INSTANCE;

        model.setRepository(new DatabaseAdapter(this));

        model.loadListOfDogsFromRepo();



        recyclerView = findViewById(R.id.recyclerView);
        rvAdapter = new RVAdapter(model.getListOfDogs(), R.layout.layout_for_view_holder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rvAdapter);


        myViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
        ).get(MyViewModel.class);

        myViewModel.getListOfDogsLive().observe(this, dogs -> {
            rvAdapter.setDogList(dogs);
            rvAdapter.notifyDataSetChanged();
        });



//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myViewModel.a();
//            }
//        });


    }

    @Override
    public void onClick(View v) {

    }
}