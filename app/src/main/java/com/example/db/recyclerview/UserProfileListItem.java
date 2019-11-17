package com.example.db.recyclerview;

import android.net.Uri;

public class UserProfileListItem {
    private String currentUserId;
    private String displayedUserId;
    private String displayedProfilePicture;
    private String displayedUsername;
    private String displayedTopicName;
    private String displayedExperianceLevel;
    private String status;

    public UserProfileListItem() {
    }

    public UserProfileListItem(String currentUserId, String displayedUserId, String displayedProfilePicture, String displayedUsername, String status) {
        this.currentUserId = currentUserId;
        this.displayedUserId = displayedUserId;
        this.displayedProfilePicture = displayedProfilePicture;
        this.displayedUsername = displayedUsername;
        this.status = status;
    }

    public UserProfileListItem(String currentUserId, String displayedUserId, String displayedProfilePicture, String displayedUsername, String displayedTopicName, String displayedExperianceLevel, String status) {
        this.currentUserId = currentUserId;
        this.displayedUserId = displayedUserId;
        this.displayedProfilePicture = displayedProfilePicture;
        this.displayedUsername = displayedUsername;
        this.displayedTopicName = displayedTopicName;
        this.displayedExperianceLevel = displayedExperianceLevel;
        this.status = status;

    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getDisplayedUserId() {
        return displayedUserId;
    }

    public void setDisplayedUserId(String displayedUserId) {
        this.displayedUserId = displayedUserId;
    }

    public String getDisplayedProfilePicture() {
        return displayedProfilePicture;
    }

    public void setDisplayedProfilePicture(String displayedProfilePicture) {
        this.displayedProfilePicture = displayedProfilePicture;
    }

    public String getDisplayedUsername() {
        return displayedUsername;
    }

    public void setDisplayedUsername(String displayedUsername) {
        this.displayedUsername = displayedUsername;
    }

    public String getDisplayedTopicName() {
        return displayedTopicName;
    }

    public void setDisplayedTopicName(String displayedTopicName) {
        this.displayedTopicName = displayedTopicName;
    }

    public String getDisplayedExperianceLevel() {
        return displayedExperianceLevel;
    }

    public void setDisplayedExperianceLevel(String displayedExperianceLevel) {
        this.displayedExperianceLevel = displayedExperianceLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
