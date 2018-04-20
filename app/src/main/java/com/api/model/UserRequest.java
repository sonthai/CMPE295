package com.api.model;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class UserRequest {

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRequest() {}
    public UserRequest(String requestId, String userEmail, String image, int quantity) {
        if (!StringUtils.isEmpty(userEmail)) {
            this.userEmail = userEmail;
        }
        this.image = image;
        this.keepImage = false;
        this.requestId = requestId;
        this.quantity = quantity;
    }

    public boolean isKeepImage() {
        return keepImage;
    }

    public void setKeepImage(boolean keepImage) {
        this.keepImage = keepImage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private String userEmail;
    private String image;
    private boolean keepImage;
    private String requestId;
    private int quantity;

}
