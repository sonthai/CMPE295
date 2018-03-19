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
        String msg = "";
        ResponseStatus status = ResponseStatus.OK;
        if (userTransaction.userExists(u)) {
            msg = "User already exists";
            status = ResponseStatus.FAIL;
        } else {
            userTransaction.save(u);
            msg = u.getUsername() + " successfully registered";
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        response.setResponseMsg(msg);
        return response;
    }

    @Override
    public ResponseMessage logIn(Map<String, String> data) {
        log.info("Login service is called");
        ObjectMapper mapper = new ObjectMapper();
        User u = mapper.convertValue(data, User.class);
        boolean found = userTransaction.findUser(u);
        ResponseMessage response = new ResponseMessage();
        String msg = "";
        ResponseStatus status = ResponseStatus.OK;
        if (found) {
            msg = u.getUsername() + " logged in successfully.";
        } else {
            msg = u.getUsername() + " failed to login.";
            status = ResponseStatus.FAIL;
        }
        response.setResponseMsg(msg);
        response.setResponseCode(status);
        return response;
    }
}
