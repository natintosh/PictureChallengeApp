package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.os.Bundle;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.fragment.CameraFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.CropImageFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.FilterImageFragment;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.ImageBitmapViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class CameraActivity extends AppCompatActivity implements CameraFragment.OnUserInputListener,
    CropImageFragment.OnEditProfileCropListener{

    FragmentManager mFragmentManager;

    public static final int CAMERA_FRAGMENT_TRANSACTION_ID = 1;
    public static final int CROP_IMAGE_FRAGMENT_TRANSACTION_ID = 2;
    public static final int FILTER_IMAGE_FRAGMENT_TRANSACTION_ID = 3;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ImageBitmapViewModel mImageBitmapViewModel = ViewModelProviders.of(this).get(ImageBitmapViewModel.class);
        mImageBitmapViewModel.initialize();

        mFragmentManager = getSupportFragmentManager();
        Fragment cameraFragment = new CameraFragment();

        fragmentTransaction(cameraFragment);
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.camera_view_frame_layout, fragment);
        if (!(fragment instanceof CameraFragment)) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onProceed(int positon) {
        switch (positon) {
            case CAMERA_FRAGMENT_TRANSACTION_ID:
                Fragment cameraFragment = new CameraFragment();
                fragmentTransaction(cameraFragment);
                break;
            case CROP_IMAGE_FRAGMENT_TRANSACTION_ID:
                Fragment cropImageFragment = new CropImageFragment();
                fragmentTransaction(cropImageFragment);
                break;
            case FILTER_IMAGE_FRAGMENT_TRANSACTION_ID:
                Fragment filterImageFragment = new FilterImageFragment();
                fragmentTransaction(filterImageFragment);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onEditProfileCropButtonClick() {

    }
}
