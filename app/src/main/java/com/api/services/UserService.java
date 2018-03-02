package com.api.services;


import com.api.constant.Constant.*;
import com.api.database.transaction.UserTransaction;
import com.api.model.ResponseMessage;
import com.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserTransaction userTransaction;

    @Override
    public ResponseMessage register(Map<String, String> data) {
        log.info("Registration service is called");
        ObjectMapper mapper = new ObjectMapper();
        User u = mapper.convertValue(data, User.class);
        userTransaction.save(u);

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(ResponseStatus.OK);
        response.setResponseMsg(u.getUsername() + " successfully registered");
        return response;
    }

    @Override
    public ResponseMessage logIn(Map<String, String> data) {
        log.info("Login service is called");
        ObjectMapper mapper = new ObjectMapper();
        User u = mapper.convertValue(data, User.class);
        boolean found = userTransaction.findUser(u);
        ResponseMessage response = new ResponseMessage();
        if (found) {
            response.setResponseCode(ResponseStatus.OK);
            response.setResponseMsg(u.getUsername() + " logged in successfully.");
        } else {
            response.setResponseCode(ResponseStatus.OK);
            response.setResponseMsg(u.getUsername() + " failed to login.");
        }
        return response;
    }
}
