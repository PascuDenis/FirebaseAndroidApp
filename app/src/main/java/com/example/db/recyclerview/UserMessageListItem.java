package com.example.db.recyclerview;

public class UserMessageListItem {
    private String currentUserId;
    private String displayedUserId;
    private String displayedProfilePicture;
    private String displayedUsername;
    private String status;

    public UserMessageListItem(String currentUserId, String displayedUserId, String displayedProfilePicture, String displayedUsername, String status) {
        this.currentUserId = currentUserId;
        this.displayedUserId = displayedUserId;
        this.displayedProfilePicture = displayedProfilePicture;
        this.displayedUsername = displayedUsername;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
