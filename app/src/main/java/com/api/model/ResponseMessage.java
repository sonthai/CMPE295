package com.api.model;

import com.api.constant.Constant.ResponseStatus;

import java.util.List;
import java.util.Map;

public class ResponseMessage {
    private ResponseStatus responseCode;
    private String responseMsg;

    private List<Map<String, Object>> data;

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
    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
