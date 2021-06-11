package com.yaroslavm87.dogwalker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.ViewModelDogList;
//import com.yaroslavm87.dogwalker.databinding.ActivityMainBinding;
import com.yaroslavm87.dogwalker.model.Dog;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

//public class ActivityDogList extends AppCompatActivity implements View.OnClickListener, FragmentDogList.OnFragmentViewClickListener {


public class ActivityMain extends AppCompatActivity implements FragmentDogList.OnFragmentViewClickListener {

    //private ActivityMainBinding binding;
    private ViewModelDogList viewModelDogList;
    private FragmentManager fragmentManager;
    private FragmentDogList fragmentDogList;
    private FragmentDogInfo fragmentDogInfo;
    private final MutableLiveData<Dog> chosenDogLive;
    private static int SIGN_IN_REQUEST_CODE = 1;
    private final String LOG_TAG;

    {
        chosenDogLive = new MutableLiveData<>();
        LOG_TAG = "myLogs";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isCurrentUserAuthenticated()) {
            initVariables();
            initViewElements();
            subscribeViewElements();

        } else {
            authenticateCurrentUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                //Snackbar.make(binding.activityMainRootLayout, "Вход выполнен", Snackbar.LENGTH_SHORT).show();

                initVariables();
                initViewElements();
                subscribeViewElements();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                //Snackbar.make(binding.activityMainRootLayout, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onFragmentViewClick() {
        if(fragmentDogInfo == null) {
            addFragmentDogInfo();
        }
        else {
            showFragment(fragmentDogInfo);
        }
        hideFragment(fragmentDogList);
        fragmentManager.executePendingTransactions();
    }

    private void initVariables() {

        fragmentManager = getSupportFragmentManager();

        viewModelDogList = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
        ).get(ViewModelDogList.class);
    }

    private void initViewElements() {
        addFragmentDogList();
//
//        addFragmentDogInfo();
//        hideFragment(fragmentDogInfo);
    }

    private void subscribeViewElements() {

        viewModelDogList.getChosenIndexOfDogFromListLive()
                .observe(this,(index) -> {
                    if(index < 0) {
                        showFragment(fragmentDogList);
                        hideFragment(fragmentDogInfo);
                        fragmentManager.executePendingTransactions();
                    }
                }
        );

//        viewModelDogList.getListOfDogsLive().observe(
//                this, fragmentDogList::receiveListOfDogs
//        );
//
//        viewModelDogList.getInsertedDogIndexLive().observe(
//                this, fragmentDogList::receiveIndexOfDogInsertedInList
//        );
    }

    private void addFragmentDogList() {
        if(fragmentDogInfo != null) return;
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(
                        R.id.fragment_container_dog_list,
                        FragmentDogList.class,
                        null,
                        "fragmentDogList"
                )
                //.addToBackStack(null)
                .commit();
        fragmentManager.executePendingTransactions();
        fragmentDogList = (FragmentDogList) fragmentManager.findFragmentByTag("fragmentDogList");
    }

    private void addFragmentDogInfo() {
        if(fragmentDogInfo != null) return;
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(
                        R.id.fragment_container_dog_info,
                        FragmentDogInfo.class,
                        null,
                        "fragmentDogInfo"
                )
                //.addToBackStack(null)
                .commit();
        fragmentManager.executePendingTransactions();
        fragmentDogInfo = (FragmentDogInfo) fragmentManager.findFragmentByTag("fragmentDogInfo");
    }

    private void hideFragment(Fragment fragment) {
        if(fragment == null) return;
        if(fragment.isVisible()) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .hide(fragment)
                    //.addToBackStack(null)
                    .commit();
            //fragmentManager.executePendingTransactions();
        }
    }

    private void showFragment(Fragment fragment) {
        if(fragment == null) return;
        if(fragment.isHidden()) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .show(fragment)
                    //.addToBackStack(null)
                    .commit();
            //fragmentManager.executePendingTransactions();
        }
    }

    private boolean isCurrentUserAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void authenticateCurrentUser() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
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
    }

}






/*
*
*
* protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_alt);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);



        if (isCurrentUserAuthenticated()) {

            viewModelDogList = new ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
            ).get(ViewModelDogList.class);

            initViewElements();

            subscribeViewElements();

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
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
            authenticateCurrentUser();




            //viewModelDogList.requestDogList();

//            subscribeViewElements();
//            viewModelDogList.requestDogList();

//            if(savedInstanceState == null) {
//                initViewElements();
//                subscribeViewElements();
//            }
        }
    }

    private boolean isCurrentUserAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void authenticateCurrentUser() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                //Snackbar.make(binding.activityDogList, "Вход выполнен", Snackbar.LENGTH_SHORT).show();

//                initViewElements();
//                subscribeViewElements();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                //Snackbar.make(binding.activityDogList, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initViewElements() {

//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(
//                        R.id.fragment_container_dog_list,
//                        FragmentDogList.class,
//                        null,
//                        "DogList"
//                )
//                .commit();

//        recyclerView = findViewById(R.id.dog_list_view);
//
//        dogListAdapter = new DogListAdapter(new ArrayList<>());
//        dogListAdapter.setViewHolderLayout(viewHolderLayout);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(dogListAdapter);
//        RecyclerView.ItemDecoration itemDecoration = new
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(itemDecoration);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());


//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);

    }

    private void subscribeViewElements() {

//        viewModelDogList.getListOfDogsLive().observe(
//                this,(dogArrayList) -> {
//                    dogListAdapter.setDogList(dogArrayList);
//                    dogListAdapter.notifyDataSetChanged();
//                });
//
//        viewModelDogList.getInsertedDogIndexLive().observe(
//                this,(index) -> {
//                    dogListAdapter.notifyItemInserted(index);
//                });


//        FragmentDogList fragmentDogList = (FragmentDogList) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_container_dog_list);
//
//        if(fragmentDogList != null) {
//
//            viewModelDogList.getListOfDogsLive().observe(
//                    this,
//                    fragmentDogList::receiveNewDogList
//            );
//
//            viewModelDogList.getInsertedDogIndexLive().observe(
//                    this,
//                    fragmentDogList::receiveIndexOfInsertedDogInList
//            );
//
//            viewModelDogList.getChangedDogIndexLive().observe(
//                    this,
//                    fragmentDogList::receiveIndexOfChangedDogInList
//            );
//
//            viewModelDogList.getDeletedDogIndexLive().observe(
//                    this,
//                    fragmentDogList::receiveIndexOfDeletedDogFromList
//            );
//        } else {
//
//            Log.d(LOG_TAG, "ActivityMain -> fragmentDogList == null");
//
//        }

//        new Thread(() -> {
//
//            try{
//                Thread.sleep(5000);
//
//            } catch(InterruptedException e) {
//                Log.d(LOG_TAG, "ViewModelDogList constructor call -> " + e);
//            }
//
//
//        }).start();


    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onClick(View v) {
//
//        switch (Objects.requireNonNull(v.getTag().toString())) {
//
//            case "addDogButton":
//
//                String s = binding.dogNameEditText.getText().toString();
//
//                if(s.toCharArray().length > 1) {
//
//                    viewModelDogList.addNewDog(s);
//                    binding.dogNameEditText.setText("");
//                }
//                hideKeyboard();
//                break;
//
//            case "deleteDogButton":
//
//                viewModelDogList.deleteDog();
//                break;
//
//            case "walkDogButton":
//
//                viewModelDogList.walkDog();
//                break;
//
//            case "sortNameButton":
//
//                viewModelDogList.sortName();
//                break;
//
//            case "sortTimeButton":
//
//                viewModelDogList.sortTime();
//                break;
//
//            case "signOutButton":
//                signOut();
//                break;
//        }
//    }

//    @Override
//    public void onViewHolderItemClick(View view, int position) {
//        Log.d(LOG_TAG, "ActivityDogList.onViewHolderItemClick() call");
//        viewModelDogList.setCurrentChosenDogByIndex(position);
//        LinearLayout ll = (LinearLayout) view;
//
//        Log.d(LOG_TAG, "ActivityDogList.ll.getChildCount() call = " + ll.getChildCount());
//        for(int i = 0; i < ll.getChildCount(); i++) {
//            TextView tv = (TextView) ll.getChildAt(i);
//            tv.setLines(5);
//        }
//        ll.getChildAt(0);
//
//    }

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

            //Snackbar.make(findViewById(R.id.activity_dog_list), "Выход выполнен", Snackbar.LENGTH_SHORT).show();
            finish();

        });
    }
}
*
*
*
*
* */