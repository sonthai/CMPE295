package com.api.services;

import com.api.model.ResponseMessage;

import java.util.Map;

interface IUserService {
    public ResponseMessage register(Map<String, String> bodyRequest);
    public ResponseMessage logIn(Map<String, String> bodyRequest);
    public ResponseMessage updatePassword(Map<String, String> bodyRequest);
}
