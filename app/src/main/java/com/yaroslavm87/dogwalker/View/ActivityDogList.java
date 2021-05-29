package com.yaroslavm87.dogwalker.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.ViewModel.ViewModelDogList;
import com.yaroslavm87.dogwalker.model.Dog;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityDogList extends AppCompatActivity implements View.OnClickListener, RVAdapter.OnViewHolderItemClickListener {

    private ViewModelDogList viewModelDogList;

    private static int SIGN_IN_REQUEST_CODE = 1;

    private Button addDogButton, deleteDogButton, walkDogButton;
    private EditText dogNameEditText;

    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;

    private final String LOG_TAG = "myLogs";

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

        walkDogButton  = findViewById(R.id.walkDog);
        walkDogButton.setTag("walkDogButton");

        dogNameEditText = findViewById(R.id.dogNameEditText);

        viewModelDogList.getListOfDogsLive().observe(this, dogList -> {
            rvAdapter.setDogList(dogList);
            rvAdapter.notifyDataSetChanged();
        });

        viewModelDogList.getInsertedDogIndexLive().observe(this, dogIndex -> {
            Log.d(LOG_TAG, "ActivityDogList.notifyItemInserted() call");
            rvAdapter.notifyItemInserted(dogIndex);
        });

        viewModelDogList.getChangedDogIndexLive().observe(this, dogIndex -> {
            Log.d(LOG_TAG, "ActivityDogList.notifyItemInserted() call");
            rvAdapter.notifyDataSetChanged();
        });

        viewModelDogList.getDeletedDogIndexLive().observe(this, dogIndex -> {
            Log.d(LOG_TAG, "ActivityDogList.notifyItemRemoved() call");

            if(dogIndex > -1) {
                rvAdapter.notifyItemRemoved(dogIndex);
            }
        });

        rvAdapter.setOnViewHolderItemClickListener(this);

//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    Log.d(LOG_TAG, "connected");
//                } else {
//                    Log.d(LOG_TAG, "not connected");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(LOG_TAG, "Listener was cancelled");
//            }
//        });




    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (Objects.requireNonNull(v.getTag().toString())) {

            case "addDogButton":

                String s = this.dogNameEditText.getText().toString();

                if(s.toCharArray().length > 1) {

                    this.viewModelDogList.addNewDog(s);
                    this.dogNameEditText.setText("");
                }
                hideKeyboard();
                break;

            case "deleteDogButton":

                this.viewModelDogList.deleteDog();
                break;

            case "walkDogButton":

                this.viewModelDogList.walkDog();
                break;
        }
    }

    @Override
    public void onViewHolderItemClick(int position) {
        Log.d(LOG_TAG, "ActivityDogList.onViewHolderItemClick() call");
        this.viewModelDogList.setCurrentChosenDogByIndex(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}