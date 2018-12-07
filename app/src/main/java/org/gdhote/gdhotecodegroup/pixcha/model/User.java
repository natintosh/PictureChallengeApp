package org.gdhote.gdhotecodegroup.pixcha.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String profileImageUrl;
    private String displayName;
    private String emailAddress;
    private String bio;

    public User() {

    }

    public User(String id, String profileImageUrl, String displayName, String emailAddress, String bio) {
        this.id = id;
        this.profileImageUrl = profileImageUrl;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.bio = bio;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

}
