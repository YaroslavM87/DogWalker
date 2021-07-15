package com.yaroslavm87.dogwalker.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;
//import com.yaroslavm87.dogwalker.databinding.ActivityMainBinding;

import java.util.Collections;
import java.util.List;

//public class ActivityDogList extends AppCompatActivity implements View.OnClickListener, FragmentDogList.OnFragmentViewClickListener {


public class ActivityMain extends AppCompatActivity implements FragmentDogList.OnDogListItemClickListener,
        FragmentDogInfo.OnDogInfoItemClickListener,
        FragmentImageCrop.OnImageCropItemClickListener {

    //private ActivityMainBinding binding;
    private AppViewModel appViewModel;
    private NavController navController;
    private FragmentManager fragmentManager;
    private static int SIGN_IN_REQUEST_CODE = 1;
    private static int PICK_GALLERY_PICTURE_REQUEST_CODE = 2;
    private final String LOG_TAG;

    {
        LOG_TAG = "myLogs";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isCurrentUserAuthenticated()) {
            initVariables();
            //initToolbar();
            //initViewElements();
            //subscribeViewElements();

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
                //initViewElements();
                //subscribeViewElements();

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

        if (requestCode == PICK_GALLERY_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                navController.navigate(R.id.action_fragmentDogInfo_to_fragmentImageCrop);

                // this is the image selected by the user
                String path = data.getData().getPath();
                Uri uri = data.getData();

                Log.d(LOG_TAG, this.getClass().getCanonicalName() + " path = " + "content://media" + path);
                Log.d(LOG_TAG, this.getClass().getCanonicalName() + " uri = " + uri);
                //appViewModel.receiveDogProfilePicPath(path);
                appViewModel.receiveDogProfilePicUri(uri);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    @Override
    public void onDogListItemClick(FragmentDogList.FragmentEvents event) {
        switch (event) {

            case ADD_DOG_CALL:
                hideKeyboard();
                break;

            case DOG_LIST_ITEM_CLICKED:
                navController.navigate(R.id.action_fragmentDogList_to_fragmentDogInfo);
                break;
        }
    }

    @Override
    public void onDogInfoItemClick(FragmentDogInfo.FragmentEvents event) {
        switch (event) {

            case SET_PROFILE_PIC:
                getProfilePicFromGallery();
                break;

            case WALK_CALL:
                appViewModel.walkDog();
                break;

            case SEE_WALK_RECORDS_CALL:
                navController.navigate(R.id.action_fragmentDogInfo_to_fragmentWalkRecords);
                break;

            case REMOVE_FROM_LIST_CALL:
                navController.popBackStack();
                appViewModel.deleteDog();
                break;
        }
    }

    @Override
    public void onImageCropItemClick(FragmentImageCrop.FragmentEvents event) {
        switch (event) {

            case IMAGE_CROP_DONE:
                navController.navigate(R.id.action_fragmentImageCrop_to_fragmentDogInfo);
                break;
        }
    }

    private void initVariables() {
        fragmentManager = getSupportFragmentManager();

        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        appViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())
        ).get(AppViewModel.class);
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

    private void getProfilePicFromGallery() {

        Intent pictureActionIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        // Create and launch sign-in intent,

        startActivityForResult(
                pictureActionIntent,
                PICK_GALLERY_PICTURE_REQUEST_CODE
        );
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
    }



    private void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {

            //Snackbar.make(findViewById(R.id.activity_dog_list), "Выход выполнен", Snackbar.LENGTH_SHORT).show();
            finish();

        });
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it

        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}