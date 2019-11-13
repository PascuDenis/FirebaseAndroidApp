package com.example.db.config;

public class UploadImage {
    private String imageName;
    private String imageUrl;
    private String downloadUrl;

    public UploadImage() {
    }

    public UploadImage(String imageName, String imageUrl, String downloadUrl) {
        if (imageName.trim().equals("")){
            imageName = "No Name";
        }
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.downloadUrl = downloadUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
