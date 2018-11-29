package org.gdhote.gdhotecodegroup.pixcha.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.activity.MainActivity;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.ui.CircularImageView;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.EditProfileViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class EditProfileFragment extends Fragment implements BSImagePicker.OnSingleImageSelectedListener {
    // the fragment initialization parameters
    public static final String PROVIDER_AUTHORITY = "org.gdhote.gdhotecodegroup.pixcha.fileprovider";

    private static final String ARG_FRAGMENT_CASE = "param";
    public static final String TAG = EditProfileFragment.class.getSimpleName();

    private int mFragmentCase;

    private OnSubmitButtonClickListener mSubmitButtonCallback;
    private OnLoadCropFragment mLoadCropFragmentCallback;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(int param1) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAGMENT_CASE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentCase = getArguments().getInt(ARG_FRAGMENT_CASE, 0);
        }
    }

    private CircularImageView profileImageView;
    private TextInputLayout displayNameTextLayout, emailAddressTextLayout, bioTextLayout;
    private TextInputEditText displayNameEditText, emailAddressEditText, bioEditText;
    private FloatingActionButton imagePickerFloatingActionButton, completedFloatingActionButton;

    private FirebaseUser firebaseUser;
    private EditProfileViewModel profileViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profileViewModel = ViewModelProviders.of(getActivity()).get(EditProfileViewModel.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MainActivity.isAwayFromNav = true;

        if (mFragmentCase == 0) {
            getActivity().setTitle("Update profile");
            shouldShowAppBar(false);
            profileViewModel.setInitialProfile(firebaseUser);
        }
        if (mFragmentCase == 1) {
            getActivity().setTitle("Edit profile");
            shouldShowAppBar(true);
            profileViewModel.setCurrentUser(CurrentUser.getInstance());
        }

        initialiseViews(view);
        insertDataIntoView();

        displayNameEditText.addTextChangedListener(new MyTextWatcher(displayNameEditText));
        emailAddressEditText.addTextChangedListener(new MyTextWatcher(emailAddressEditText));
        bioEditText.addTextChangedListener(new MyTextWatcher(bioEditText));

        final BSImagePicker profilerImagePicker = new BSImagePicker.Builder(PROVIDER_AUTHORITY)
                .setMaximumDisplayingImages(Integer.MAX_VALUE)
                .setSpanCount(5)
                .setGridSpacing(Utils.dp2px(2))
                .setPeekHeight(Utils.dp2px(360))
                .hideGalleryTile()
                .build();

        imagePickerFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilerImagePicker.show(getChildFragmentManager(), "image-picker");
            }
        });

        completedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfile();
            }
        });
        return view;
    }

    private void shouldShowAppBar(Boolean shouldShow) {
        if (shouldShow) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(shouldShow);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(shouldShow);
    }

    private void insertDataIntoView() {
        GlideApp.with(this)
                .asBitmap()
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .load(profileViewModel.getProfilePictureUrl())
                .into(profileImageView);


        profileViewModel.getProfilePicture().observe(getActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                profileImageView.setImageBitmap(bitmap);
            }
        });
        displayNameEditText.setText(profileViewModel.getDisplayName());
        emailAddressEditText.setText(profileViewModel.getEmailAddress());
        bioEditText.setText(profileViewModel.getBio());
    }

    @Override
    public void onResume() {
        super.onResume();
        newInstance(mFragmentCase);
    }

    private void initialiseViews(View view) {
        profileImageView = view.findViewById(R.id.edit_profile_circular_image);

        displayNameTextLayout = view.findViewById(R.id.edit_profile_display_name_text_layout);
        emailAddressTextLayout = view.findViewById(R.id.edit_profile_email_address_text_layout);
        bioTextLayout = view.findViewById(R.id.edit_profile_bio_text_layout);

        displayNameEditText = view.findViewById(R.id.edit_profile_display_name_text_input);
        emailAddressEditText = view.findViewById(R.id.edit_profile_email_address_edit_text);
        bioEditText = view.findViewById(R.id.edit_profile_bio_edit_text);

        imagePickerFloatingActionButton = view.findViewById(R.id.edit_profile_image_picker);
        completedFloatingActionButton = view.findViewById(R.id.edit_profile_complete_button);
    }

    private void submitProfile() {
        if (!validateName()) return;
        if (!validateEmail()) return;
        if (!validateBio()) return;

        profileViewModel.setId(firebaseUser.getUid());
        profileViewModel.setDisplayName(displayNameEditText.getText().toString().trim());
        profileViewModel.setEmailAddress(emailAddressEditText.getText().toString().trim());
        profileViewModel.setBio(bioEditText.getText().toString().trim());

        profileImageView.setDrawingCacheEnabled(true);
        profileImageView.buildDrawingCache();
        profileViewModel.setBitmap(((BitmapDrawable) profileImageView.getDrawable()).getBitmap());
        onButtonPressed();
    }

    public void onButtonPressed() {
        if (mSubmitButtonCallback != null) {
            mSubmitButtonCallback.onSubmitButtonClick();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitButtonClickListener) {
            mSubmitButtonCallback = (OnSubmitButtonClickListener) context;
            mLoadCropFragmentCallback = (OnLoadCropFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSubmitButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSubmitButtonCallback = null;
        mLoadCropFragmentCallback = null;
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        mLoadCropFragmentCallback.onLoadCropFragment(uri);
    }

    public interface OnSubmitButtonClickListener {
        void onSubmitButtonClick();
    }

    public interface OnLoadCropFragment {
        void onLoadCropFragment(Uri uri);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateName() {
        if (displayNameEditText.getText().toString().trim().isEmpty()) {
            displayNameTextLayout.setError("Display name can't be empty");
            requestFocus(displayNameEditText);
            return false;
        } else {
            displayNameTextLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = emailAddressEditText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailAddressTextLayout.setError("Invalid email address");
            requestFocus(emailAddressEditText);
            return false;
        } else {
            emailAddressTextLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateBio() {
        if (bioEditText.getText().toString().trim().length() > 150) {
            bioTextLayout.setError("limit passed");
            requestFocus(bioEditText);
            return false;
        } else {
            bioTextLayout.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {
        TextInputEditText editText;

        public MyTextWatcher(View view) {
            this.editText = (TextInputEditText) view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (editText.getId()) {
                case R.id.edit_profile_display_name_text_input:
                    validateName();
                    break;
                case R.id.edit_profile_email_address_edit_text:
                    validateEmail();
                    break;
                case R.id.edit_profile_bio_edit_text:
                    validateBio();
                    break;
            }
        }
    }
}
