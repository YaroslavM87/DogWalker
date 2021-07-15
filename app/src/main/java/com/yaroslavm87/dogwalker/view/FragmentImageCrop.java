package com.yaroslavm87.dogwalker.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lyft.android.scissors.CropView;
import com.yaroslavm87.dogwalker.R;
import com.yaroslavm87.dogwalker.viewModel.AppViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FragmentImageCrop extends Fragment implements View.OnClickListener {

    private AppViewModel appViewModel;
    private CropView cropView;
    private FloatingActionButton btnDone, btnRotate;
    private OnImageCropItemClickListener onImageCropItemClickListener;
    private float rotationAngle;
    private String LOG_TAG = "myLogs";

    public interface OnImageCropItemClickListener {
        void onImageCropItemClick(FragmentImageCrop.FragmentEvents event);
    }

    public enum FragmentEvents{
        IMAGE_CROP_DONE
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentDogInfo.OnDogInfoItemClickListener) {
            onImageCropItemClickListener = (FragmentImageCrop.OnImageCropItemClickListener) context;

        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentDogInfo.OnDogInfoItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_crop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVariables();
        initViewElements(view);
    }

    private void initVariables() {
        appViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    }

    private void initViewElements(View view) {
        cropView = (CropView) view.findViewById(R.id.crop_view);
        btnDone = view.findViewById(R.id.btn_image_crop_done);
        btnDone.setOnClickListener(this);
        btnRotate = view.findViewById(R.id.btn_image_crop_rotate);
        btnRotate.setOnClickListener(this);
    }

    private void subscribeViewElements() {
        appViewModel.getDogProfileImageUriLive().observe(
                getViewLifecycleOwner(),
                (uri) -> {

//                    Bitmap bitmap = BitmapFactory.decodeFile("content://media" + path);
//                    if(bitmap == null) Log.d(LOG_TAG, "bitmap null");
//                    cropView.setImageBitmap(bitmap);

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                        cropView.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        Log.d(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeViewElements();
    }

    @Override
    public void onClick(View v) {

        switch(v.getTag().toString()) {

            case "imageRotate":
                setRotationAngle();
                break;

            case "imageDone":
                onCropClicked();
                onImageCropItemClickListener.onImageCropItemClick(FragmentEvents.IMAGE_CROP_DONE);
                break;
        }
    }

    private void onCropClicked() {
        final File croppedFile = new File(requireActivity().getCacheDir(), "cropped.jpg");
        try {
            Bitmap bitmap = cropView.crop();
            bitmap = rotateBitmap(bitmap, rotationAngle);
            FileOutputStream fos = new FileOutputStream(croppedFile);
            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    40,
                    fos
            );
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = croppedFile.getPath();
        appViewModel.updateDogImage(path);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setRotationAngle() {
        rotationAngle = rotationAngle == 360 ? 0 : rotationAngle + 90;
        cropView.setRotation(rotationAngle);
    }

}