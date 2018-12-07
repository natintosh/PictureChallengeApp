package org.gdhote.gdhotecodegroup.pixcha.model;

import com.google.firebase.Timestamp;

;

public class FeedPost {

    private String id;
    private String uploadedBy;
    private String imageUrl;
    private String captionText;
    private Timestamp uploadedAt;
    private int likes;

    public FeedPost() {
    }

    public FeedPost(String id, String uploadedBy, String imageUrl, String captionText, Timestamp uploadedAt, int likes) {
        this.id = id;
        this.uploadedBy = uploadedBy;
        this.imageUrl = imageUrl;
        this.captionText = captionText;
        this.uploadedAt = uploadedAt;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaptionText() {
        return captionText;
    }

    public void setCaptionText(String captionText) {
        this.captionText = captionText;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
