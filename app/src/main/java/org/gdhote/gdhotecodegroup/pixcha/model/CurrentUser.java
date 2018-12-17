package org.gdhote.gdhotecodegroup.pixcha.model;

public class CurrentUser extends User {

    private String id;
    private String profileImageUrl;
    private String displayName;
    private String emailAddress;
    private String bio;

    private static CurrentUser instance;

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    private CurrentUser() {

    }

    private CurrentUser(String id, String profileImageUrl, String displayName, String emailAddress, String bio) {
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
