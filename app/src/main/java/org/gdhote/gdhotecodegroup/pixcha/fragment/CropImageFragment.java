package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.theartofdev.edmodo.cropper.CropImageView;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.CameraActivity;
import org.gdhote.gdhotecodegroup.pixcha.model.ImageBitmapViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class CropImageFragment extends Fragment {

    public static final int DEGREES_OF_ROTATION = 90;

    public CropImageFragment() {
        // Required empty public constructor
    }

    private CropImageView cropImageView;
    CameraFragment.OnUserInputListener mCallBack;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (CameraFragment.OnUserInputListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crop_image, container, false);

        cropImageView = view.findViewById(R.id.cropImageView);

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().setTitle("Crop Image");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        ImageBitmapViewModel model = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);
        model.getOriginalBitmap().observe(getActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                cropImageView.setImageBitmap(bitmap);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crop_image_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flip_crop_image_menu:
                cropImageView.flipImageHorizontally();
                return true;
            case R.id.rotate_crop_image_menu:
                cropImageView.rotateImage(DEGREES_OF_ROTATION);
                return true;
            case R.id.crop_crop_image_menu:
                Bitmap bitmap = cropImageView.getCroppedImage();

                ImageBitmapViewModel imageBitmapViewModel = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);
                imageBitmapViewModel.setCroppedBitmap(bitmap);
                mCallBack.onProceed(CameraActivity.FILTER_IMAGE_FRAGMENT_TRANSACTION_ID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
