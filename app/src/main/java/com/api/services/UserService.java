package com.api.services;


import com.api.constant.Constant;
import com.api.constant.Constant.*;
import com.api.database.transaction.UserTransaction;
import com.api.model.ResponseMessage;
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
        String msg;
        ResponseStatus status = ResponseStatus.OK;
        if (userTransaction.userExists(data)) {
            msg = "User already exists";
            status = ResponseStatus.FAIL;
        } else {
            userTransaction.save(data);
            msg = data.get(Constant.USER_EMAIL) + " successfully registered";
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        response.setResponseMsg(msg);
        return response;
    }

    @Override
    public ResponseMessage logIn(Map<String, String> data) {
        log.info("Login service is called");
        return userTransaction.findUser(data);
    }
}
