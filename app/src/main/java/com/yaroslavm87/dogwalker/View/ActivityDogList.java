package com.yaroslavm87.dogwalker.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.ViewModel.ViewModelDogList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ActivityDogList extends AppCompatActivity implements View.OnClickListener, RVAdapter.OnViewHolderItemClickListener {

    private ViewModelDogList viewModelDogList;

    private static int SIGN_IN_REQUEST_CODE = 1;

    private View activity_dog_list;

    private Button addDogButton, deleteDogButton, walkDogButton, sortNameButton, sortTimeButton, signOutButton;
    private EditText dogNameEditText;

    private RecyclerView recyclerView;
    private RVAdapter rvAdapter;

    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity_dog_list = findViewById(R.id.activity_dog_list);
        setContentView(R.layout.activity_dog_list);

        viewModelDogList = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
        ).get(ViewModelDogList.class);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else {

            initViewElements();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Snackbar.make(findViewById(R.id.activity_dog_list), "Вход выполнен", Snackbar.LENGTH_SHORT).show();

                initViewElements();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Snackbar.make(activity_dog_list, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }

        /*
        * @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
        * */

    }

    private void initViewElements() {
        recyclerView = findViewById(R.id.recyclerView);
        rvAdapter = new RVAdapter(new ArrayList<>(), R.layout.layout_for_view_holder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rvAdapter);

        addDogButton = findViewById(R.id.addDog);
        addDogButton.setTag("addDogButton");

        deleteDogButton = findViewById(R.id.deleteDog);
        deleteDogButton.setTag("deleteDogButton");

        walkDogButton = findViewById(R.id.walkDog);
        walkDogButton.setTag("walkDogButton");

        sortNameButton = findViewById(R.id.sortName);
        sortNameButton.setTag("sortNameButton");

        sortTimeButton = findViewById(R.id.sortTime);
        sortTimeButton.setTag("sortTimeButton");

        signOutButton = findViewById(R.id.signOut);
        signOutButton.setTag("signOutButton");

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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (Objects.requireNonNull(v.getTag().toString())) {

            case "addDogButton":

                String s = dogNameEditText.getText().toString();

                if(s.toCharArray().length > 1) {

                    viewModelDogList.addNewDog(s);
                    dogNameEditText.setText("");
                }
                hideKeyboard();
                break;

            case "deleteDogButton":

                viewModelDogList.deleteDog();
                break;

            case "walkDogButton":

                viewModelDogList.walkDog();
                break;

            case "sortNameButton":

                viewModelDogList.sortName();
                break;

            case "sortTimeButton":

                viewModelDogList.sortTime();
                break;

            case "signOutButton":

                signOut();
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


    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {

            Snackbar.make(findViewById(R.id.activity_dog_list), "Выход выполнен", Snackbar.LENGTH_SHORT).show();
            finish();

        });
    }


}