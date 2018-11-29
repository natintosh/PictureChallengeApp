package org.gdhote.gdhotecodegroup.pixcha.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import org.gdhote.gdhotecodegroup.pixcha.model.User;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditProfileViewModel extends ViewModel {

    private String id;
    private String displayName;
    private String emailAddress;
    private String bio;
    private MutableLiveData<Bitmap> profilePicture;
    private Bitmap imageBitmap;
    private Uri profilePictureUrl;

    public void initialize() {
        displayName = null;
        emailAddress = null;
        bio = null;
        profilePicture = new MutableLiveData<>();
        imageBitmap = null;
        profilePictureUrl = null;

        profilePicture.setValue(null);
    }

    public void setInitialProfile(FirebaseUser user) {
        setId(user.getUid());
        setDisplayName(user.getDisplayName());
        setEmailAddress(user.getEmail());
        setProfilePictureUrl(user.getPhotoUrl());
    }

    public void setCurrentUser(User user) {
        setId(user.getId());
        setDisplayName(user.getDisplayName());
        setEmailAddress(user.getEmailAddress());
        setProfilePictureUrl(Uri.parse(user.getProfileImageUrl()));
        setBio(user.getBio());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Bitmap getBitmap() {
        return imageBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.imageBitmap = bitmap;
    }

    public MutableLiveData<Bitmap> getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture.setValue(profilePicture);
        imageBitmap = profilePicture;
    }

    public Uri getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(Uri profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
