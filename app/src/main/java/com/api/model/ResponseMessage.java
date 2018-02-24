package com.api.model;

import com.api.constant.Constant.ResponseStatus;

public class ResponseMessage {
    private ResponseStatus responseCode;
    private String responseMsg;

    public ResponseStatus getResponseCode() {
        return responseCode;
    }
    public void setResponseCode(ResponseStatus responseCode) {
        this.responseCode = responseCode;
    }
    public String getResponseMsg() {
        return responseMsg;
    }
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
