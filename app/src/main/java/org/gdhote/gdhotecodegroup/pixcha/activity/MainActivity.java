package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.gdhote.gdhotecodegroup.pixcha.R;
import org.gdhote.gdhotecodegroup.pixcha.fragment.CameraFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.CropImageFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.EditProfileFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.FeedsFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.ProfileFragment;
import org.gdhote.gdhotecodegroup.pixcha.fragment.SignInFragment;
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;
import org.gdhote.gdhotecodegroup.pixcha.utils.UserHelper;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.EditProfileViewModel;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.MainActivityNavigationViewModel;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnSignInButtonClickListener,
        EditProfileFragment.OnSubmitButtonClickListener, EditProfileFragment.OnLoadCropFragment,
        CropImageFragment.OnEditProfileCropListener, CameraFragment.OnUserInputListener,
        ProfileFragment.OnProfileDetailsEditButtonListener {

    private static final int RC_SIGN_IN = 1001;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final int FEED_POSTION = 0;
    private final int CAMERA_POSITION = 1;
    private final int PROFILE_POSITION = 2;

    private FragmentManager mFragmentManager;
    private final FeedsFragment mFeedsFragment = new FeedsFragment();
    private final ProfileFragment mProfileFragment = new ProfileFragment();
    private final SignInFragment mSignInFragment = new SignInFragment();
    private final EditProfileFragment mEditProfileFragment = new EditProfileFragment();
    public static Fragment activeFragment;

    private MainActivityNavigationViewModel navigationViewModel;
    private EditProfileViewModel profileViewModel;

    public static BottomNavigationView bottomNavigationView;
    CurrentUser currentUser = CurrentUser.getInstance();

    private final static List<AuthUI.IdpConfig> signInProviders = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserHelper.updateCurrentUser();

        navigationViewModel = ViewModelProviders.of(this).get(MainActivityNavigationViewModel.class);
        profileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        profileViewModel.initialize();

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        activeFragment = mFeedsFragment;


        if (user != null) {
            loadFragment(mFeedsFragment, false);
        } else {
            startFirebaseUiIntent();
        }
    }

    @Override
    public void onBackPressed() {
        int selectedNavItemId = bottomNavigationView.getSelectedItemId();

        if (selectedNavItemId != R.id.nav_action_feeds) {
            super.onBackPressed();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int selectedItemId = bottomNavigationView.getMenu().getItem(navigationViewModel.getPosition()).getItemId();
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        // load fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.view_frame_layout, fragment);

        final int count = mFragmentManager.getBackStackEntryCount();

        if (addToBackStack) transaction.addToBackStack(null);

        transaction.commit();

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if (mFragmentManager.getBackStackEntryCount() <= count) {
                    // pop all the fragment and remove the listener
                    mFragmentManager.popBackStack(PROFILE_POSITION, POP_BACK_STACK_INCLUSIVE);
                    mFragmentManager.removeOnBackStackChangedListener(this);
                    // set the home button selected
//                    bottomNavigationView.getMenu().getItem(FEED_POSTION).setChecked(true);

                }
            }
        });
    }

    private void setNavigationViewVisibility(Boolean setVisibility) {

        if (setVisibility) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
        } else {
            bottomNavigationView.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    switch (itemId) {
                        case R.id.nav_action_feeds:
                            if (activeFragment != mFeedsFragment) {
                                loadFragment(mFeedsFragment, true);
                                activeFragment = mFeedsFragment;
                                navigationViewModel.setPosition(FEED_POSTION);
                            }
                            return true;
                        case R.id.nav_action_camera:
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.nav_action_profile:
                            if (activeFragment != mProfileFragment) {
                                loadFragment(mProfileFragment, true);
                                activeFragment = mProfileFragment;
                                navigationViewModel.setPosition(PROFILE_POSITION);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                makeToast("Successfully sign in");
                FirebaseUserMetadata metadata = FirebaseAuth.getInstance().getCurrentUser().getMetadata();

                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    // The user is new, show them a fancy intro screen!
                    makeToast("Welcome");
                    loadFragment(EditProfileFragment.newInstance(0), false);
                    setNavigationViewVisibility(false);
                } else {
                    // This is an existing user, show them a welcome back screen.
                    loadFragment(mFeedsFragment, false);
                    // Set navigation visibility
                    setNavigationViewVisibility(true);
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                // Set navigation visibility
                setNavigationViewVisibility(false);
                loadFragment(mSignInFragment, false);
                if (response == null) {
                    makeToast("Sign in failed");
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    makeToast("No internet connection");
                    return;
                }
                makeToast("Unknown error occurred");
            }
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInBottonClick() {
        startFirebaseUiIntent();
    }

    private void startFirebaseUiIntent() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(signInProviders)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.mipmap.ic_launcher_round)      // Set logo drawable
                        .setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public void onSubmitButtonClick() {
        StorageReference rootStorageReference = storage.getReference();
        StorageReference profileImagesStorageRef = rootStorageReference.child("profile_images");
        String fileName = "image" + "_" + profileViewModel.getId() + ".jpg";
        final StorageReference fileRef = profileImagesStorageRef.child(fileName);

        if (profileViewModel != null) {

            String documentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
            CollectionReference usersCollectionRef = firestoreDb.collection("users");
            final DocumentReference userDocumentRef = usersCollectionRef.document(documentId);

            if (profileViewModel.getBitmap() != null) {
                Bitmap bitmap = profileViewModel.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] data = baos.toByteArray();

                final UploadTask uploadTask = fileRef.putBytes(data);

                openProgressDialog();

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });


                Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return fileRef.getDownloadUrl();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String fileUrl = uri.toString();

                        User user = new User(profileViewModel.getId(), fileUrl, profileViewModel.getDisplayName(), profileViewModel.getEmailAddress(), profileViewModel.getBio());


                        currentUser.setId(user.getId());
                        currentUser.setDisplayName(user.getDisplayName());
                        currentUser.setEmailAddress(user.getEmailAddress());
                        currentUser.setBio(user.getBio());
                        currentUser.setProfileImageUrl(user.getProfileImageUrl());

                        userDocumentRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "successfully written!", Toast.LENGTH_SHORT).show();
                                closeProgressDialog();
                                loadFragment(mFeedsFragment, false);
                                setNavigationViewVisibility(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onLoadCropFragment(Uri uri) {
        Fragment fragment = CropImageFragment.newInstance(1, uri);
        loadFragment(fragment, true);
    }

    @Override
    public void onProceed(int positon) {
        // Unused implementation. Implementation not needed for this scenario.
    }

    @Override
    public void onEditProfileCropButtonClick() {
        loadFragment(mEditProfileFragment, false);
    }

    Dialog dialog;

    public void openProgressDialog() {
        View imageDialog = getLayoutInflater().inflate(R.layout.custom_image_dialog, null);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(imageDialog);
        ImageView gifImage = imageDialog.findViewById(R.id.custom_image_dialog_image);
        DrawableImageViewTarget target = new DrawableImageViewTarget(gifImage);

        GlideApp.with(this)
                .load(R.drawable.ic_spinner)
                .placeholder(R.drawable.ic_spinner)
                .centerCrop()
                .into(target);
        dialog.show();
    }

    public void closeProgressDialog() {
        dialog.dismiss();
    }

    @Override
    public void onProfileDetailsEditButtonClick() {
        loadFragment(EditProfileFragment.newInstance(1), true);
    }
}
