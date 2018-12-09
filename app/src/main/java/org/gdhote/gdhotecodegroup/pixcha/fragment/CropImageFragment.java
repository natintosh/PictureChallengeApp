package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
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
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.EditProfileViewModel;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.ImageBitmapViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class CropImageFragment extends Fragment {

    public static final int DEGREES_OF_ROTATION = 90;
    private static final String ARG_FRAGMENT_CASE = "param";
    private static final String ARG_IMAGE_URI = "uri";
    private int mFragmentCase;
    private Uri mImageUri;

    public CropImageFragment() {
        // Required empty public constructor
    }

    public static CropImageFragment newInstance(int mFragmentCase, Uri uri) {
        CropImageFragment fragment = new CropImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_CASE, mFragmentCase);
        args.putParcelable(ARG_IMAGE_URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentCase = getArguments().getInt(ARG_FRAGMENT_CASE, 0);
            mImageUri = getArguments().getParcelable(ARG_IMAGE_URI);
        }
        setHasOptionsMenu(true);
    }

    private CropImageView cropImageView;
    private CameraFragment.OnUserInputListener mCallBack;
    private OnEditProfileCropListener mEditProfileCallback;

    public interface OnEditProfileCropListener {
        void onEditProfileCropButtonClick();
    }

    private void onEditProfileCropMenuSelected() {
        if (mEditProfileCallback != null) {
            mEditProfileCallback.onEditProfileCropButtonClick();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (CameraFragment.OnUserInputListener) context;
            mEditProfileCallback = (OnEditProfileCropListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnUserInputListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
        mEditProfileCallback = null;
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

        if (mFragmentCase == 0) {
            ImageBitmapViewModel model = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);
            model.getOriginalBitmap().observe(getActivity(), new Observer<Bitmap>() {
                @Override
                public void onChanged(Bitmap bitmap) {
                    cropImageView.setImageBitmap(bitmap);
                }
            });
        }
        if (mFragmentCase == 1) {
            cropImageView.setImageUriAsync(mImageUri);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crop_image_fragment_menu, menu);

        if (mFragmentCase == 1) {
            MenuItem skipItem  = menu.findItem(R.id.skip_crop_image_menu);
            skipItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bitmap bitmap;
        ImageBitmapViewModel imageBitmapViewModel = ViewModelProviders.of(getActivity()).get(ImageBitmapViewModel.class);

        switch (item.getItemId()) {
            case R.id.flip_crop_image_menu:
                cropImageView.flipImageHorizontally();
                return true;
            case R.id.rotate_crop_image_menu:
                cropImageView.rotateImage(DEGREES_OF_ROTATION);
                return true;
            case R.id.skip_crop_image_menu:
                cropImageView.clearAspectRatio();
                cropImageView.setCropRect(new Rect(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE));
                bitmap = cropImageView.getCroppedImage();
                imageBitmapViewModel.setCroppedBitmap(bitmap);
                mCallBack.onProceed(CameraActivity.FILTER_IMAGE_FRAGMENT_TRANSACTION_ID);
                return true;
            case R.id.crop_crop_image_menu:
                bitmap = cropImageView.getCroppedImage();
                if (mFragmentCase == 0) {
                }
                if (mFragmentCase == 1) {
                    ViewModelProviders.of(getActivity()).get(EditProfileViewModel.class).setProfilePicture(bitmap);
                    onEditProfileCropMenuSelected();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
