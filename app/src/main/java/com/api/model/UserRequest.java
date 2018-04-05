package com.api.model;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class UserRequest {

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRequest(String userEmail, String imagePath) {
        if (!StringUtils.isEmpty(userEmail)) {
            this.userEmail = userEmail;
        }
        this.imagePath = imagePath;
        this.keepImage = false;
    }

    public boolean isKeepImage() {
        return keepImage;
    }

    public void setKeepImage(boolean keepImage) {
        this.keepImage = keepImage;
    }

    private String userEmail;
    private String imagePath;
    private boolean keepImage;

}
