package com.project.hotel.entity;

public class UserProfileDto {
    private String displayName;
    private String imagePath;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public UserProfileDto(String displayName, String imagePath){
        this.displayName = displayName;
        this.imagePath = imagePath;
    }
    public UserProfileDto(){}

}
