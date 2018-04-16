package com.api.services;

import com.api.model.ResponseMessage;

import java.util.Map;

interface IUserService {
    ResponseMessage register(Map<String, String> bodyRequest);
    ResponseMessage logIn(Map<String, String> bodyRequest);
    ResponseMessage updatePassword(Map<String, String> bodyRequest);
}
