package org.gdhote.gdhotecodegroup.pixcha.model;

import com.google.firebase.Timestamp;

public class Comment {

    private String displayName;
    private String message;
    private Timestamp uploadedAt;

    public Comment() {

    }

    public Comment(String displayName, String message, Timestamp uploadedAt) {
        this.displayName = displayName;
        this.message = message;
        this.uploadedAt = uploadedAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
