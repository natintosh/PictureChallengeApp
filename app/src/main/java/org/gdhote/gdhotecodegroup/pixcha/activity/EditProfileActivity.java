package org.gdhote.gdhotecodegroup.pixcha.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.User;
import org.gdhote.gdhotecodegroup.pixcha.utils.GlideApp;
import org.gdhote.gdhotecodegroup.pixcha.viewmodel.EditProfileViewModel;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class EditProfileActivity extends AppCompatActivity implements EditProfileFragment.OnEditProfileListener,
        CropImageFragment.OnEditProfileCropListener, CameraFragment.OnUserInputListener {


    static final String EDIT_PROFILE_TYPE_INTENT_EXTRA = "edit_profile_type";
    static final int TYPE_UPDATE_PROFILE = 0;
    static final int TYPE_EDIT_PROFILE = 1;

    private FragmentManager mFragmentManager;
    private EditProfileViewModel profileViewModel;

    private EditProfileFragment mEditProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        profileViewModel.initialize();

        Intent intent = getIntent();
        int editProfileType = intent.getIntExtra(EDIT_PROFILE_TYPE_INTENT_EXTRA, TYPE_EDIT_PROFILE);

        mFragmentManager = getSupportFragmentManager();


        switch (editProfileType) {
            case TYPE_UPDATE_PROFILE:
                mEditProfileFragment = EditProfileFragment.newInstance(0);
                loadFragment(EditProfileFragment.newInstance(0), false);
                break;
            case TYPE_EDIT_PROFILE:
                mEditProfileFragment = EditProfileFragment.newInstance(1);
                loadFragment(EditProfileFragment.newInstance(1), false);
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

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        // load fragment
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.edit_activity_frame_layout, fragment);

        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();

    }


    @Override
    public void onSubmitButtonClick() {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference rootStorageReference = storage.getReference();
        final StorageReference profileImagesStorageRef = rootStorageReference.child("profile_images");
        final String fileName = "image" + "_" + profileViewModel.getId() + ".jpg";
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
                        Toast.makeText(EditProfileActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
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

                        CurrentUser currentUser = CurrentUser.getInstance();
                        currentUser.setId(user.getId());
                        currentUser.setDisplayName(user.getDisplayName());
                        currentUser.setEmailAddress(user.getEmailAddress());
                        currentUser.setBio(user.getBio());
                        currentUser.setProfileImageUrl(user.getProfileImageUrl());

                        userDocumentRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, "successfully written!", Toast.LENGTH_SHORT).show();
                                closeProgressDialog();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }


    Dialog dialog;

    public void openProgressDialog() {
        View imageDialog = getLayoutInflater().inflate(R.layout.dialog_custom_image, null);
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
        onBackPressed();
    }
}
