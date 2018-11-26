package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.CameraActivity;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.ImageBitmapViewModel;
import org.gdhote.gdhotecodegroup.pixcha.utils.OnSwipeTouchListener;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class CameraFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener {

    private static final String PROVIDER_AUTHORITY = "org.gdhote.gdhotecodegroup.pixcha.fileprovider";

    private OnUserInputListener mCallBack;

    private CameraView cameraView;
    private ImageButton cameraFlashSwitchButton;
    private ImageButton activeCameraSwitchButton;

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.AUTO,
            Flash.ON,
            Flash.TORCH
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_on,
            R.drawable.ic_flast_touch
    };

    private static final Facing[] FACING_OPTIONS = {
            Facing.BACK,
            Facing.FRONT
    };

    private static final int[] FACING_ICONS = {
            R.drawable.ic_camera_rear,
            R.drawable.ic_camera_front
    };

    private int mCurrentFlash;
    private int mCurrentFacing;

    public CameraFragment() {
        // Required empty public constructor
    }

    public interface OnUserInputListener {
        void onProceed(int positon);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (OnUserInputListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().setTitle("Camera");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        cameraView = view.findViewById(R.id.camera_view);
        cameraView.setLifecycleOwner(this.getViewLifecycleOwner());
        cameraView.start();

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class).setOriginalBitmap(bitmap);
                        mCallBack.onProceed(CameraActivity.CROP_IMAGE_FRAGMENT_TRANSACTION_ID);
                    }
                });
            }
        });

        ImageButton cameraShutterButton = view.findViewById(R.id.camera_shutter_btn);
        cameraShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capturePicture();
            }
        });

        cameraFlashSwitchButton = view.findViewById(R.id.camera_flash_switch);
        cameraFlashSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    cameraFlashSwitchButton.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    cameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
            }
        });

        activeCameraSwitchButton = view.findViewById(R.id.camera_active_view_switch);
        activeCameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView != null) {
                    mCurrentFacing = (mCurrentFacing + 1) % FACING_OPTIONS.length;
                    activeCameraSwitchButton.setImageResource(FACING_ICONS[mCurrentFacing]);
                    cameraView.setFacing(FACING_OPTIONS[mCurrentFacing]);
                }
            }
        });

        final BSImagePicker imagePicker = new BSImagePicker.Builder(PROVIDER_AUTHORITY)
                .setSpanCount(4)
                .setGridSpacing(Utils.dp2px(2))
                .setPeekHeight(Utils.dp2px(360))
                .hideCameraTile()
                .hideGalleryTile()
                .build();

        ConstraintLayout mContraintLayout = view.findViewById(R.id.controls_layout_block);
        mContraintLayout.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeTop() {
                imagePicker.show(getChildFragmentManager(), "picker");
            }
        });


        ImageButton cameraSwipeUpButton = view.findViewById(R.id.camera_swipe_up_button);
        cameraSwipeUpButton.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeTop() {
                imagePicker.show(getChildFragmentManager(), "picker");
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !cameraView.isStarted()) {
            cameraView.start();
        }
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class).setOriginalBitmap(bitmap);
            mCallBack.onProceed(CameraActivity.CROP_IMAGE_FRAGMENT_TRANSACTION_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

}
