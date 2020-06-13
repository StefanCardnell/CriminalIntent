package com.bignerdranch.android.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class PictureFragment extends DialogFragment {

    private static final String ARG_PHOTO_PATH = "photo_path";

    private ImageView mPhotoView;

    public static PictureFragment newInstance(File photoPath) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_PATH, photoPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO_PATH);

        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        mPhotoView = v.findViewById(R.id.photo);

        if (photoFile.exists()) {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

        return v;
    }
}