package com.bignerdranch.android.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;

public class PictureFragment extends DialogFragment {

    private static final String ARG_PICTURE_PATH = "picture_path";

    private ImageView mPictureView;
    
    private File mPictureFile; 

    public static PictureFragment newInstance(File photoPath) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE_PATH, photoPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPictureFile = (File) getArguments().getSerializable(ARG_PICTURE_PATH);

        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        mPictureView = v.findViewById(R.id.picture);
        mPictureView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePictureView();
            }
        });
        
        return v;
    }

    /**
     * Only to be called after the Picture Image View has been drawn.
     */
    private void updatePictureView(){
        if(!mPictureFile.exists()) return;

        int height = mPictureView.getHeight();
        if (height != 0) {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPictureFile.getPath(), mPictureView.getWidth(), mPictureView.getHeight());
            mPictureView.setImageBitmap(bitmap);
        }

    }
}