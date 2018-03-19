package com.api.model;

import org.springframework.stereotype.Component;

public class UserRequest {

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserRequest(String userId, String imagePath) {
        this.userId = userId;
        this.imagePath = imagePath;
    }

    private String userId;
    private String imagePath;

}
