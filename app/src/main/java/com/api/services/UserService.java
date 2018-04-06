package com.api.services;


import com.api.constant.Constant;
import com.api.constant.Constant.*;
import com.api.database.transaction.UserTransaction;
import com.api.model.ResponseMessage;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
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
        if (userTransaction.getUsers(data).size() == 1) {
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
        String msg;
        ResponseStatus status = ResponseStatus.OK;
        List<Map<String, Object>> users = userTransaction.getUsers(data);
        if (users.size() == 0) {
            status = ResponseStatus.FAIL;
            msg = data.get(Constant.USER_EMAIL) + " does not exist.";
        } else {
            if (userTransaction.isPasswordMatch(users.get(0), data.get(Constant.USER_PASSWORD))) {
                msg = data.get(Constant.USER_EMAIL) + " logged in successfully.";
            } else {
                status = ResponseStatus.FAIL;
                msg = data.get(Constant.USER_EMAIL) + " failed to login.";
            }

        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        response.setResponseMsg(msg);
        return response;
    }

    @Override
    public ResponseMessage updatePassword(Map<String, String> data) {
        log.info("Update password service is called");
        String msg;
        ResponseStatus status = ResponseStatus.OK;
        Map<String, String> userMap = new HashMap<>();
        userMap.put(Constant.USER_EMAIL, data.get("email"));
        userMap.put(Constant.USER_PASSWORD, data.get("old_password"));

        List<Map<String, Object>> users = userTransaction.getUsers(userMap);

        if (userTransaction.isPasswordMatch(users.get(0), data.get("old_password"))) {
            userMap.put(Constant.USER_PASSWORD, data.get("new_password"));
            userMap.put("Id", users.get(0).get("Id").toString());
            if (userTransaction.updatePassword(userMap)) {
                msg = data.get(Constant.USER_EMAIL) + " updated password successfully.";
            } else {
                status = ResponseStatus.FAIL;
                msg = data.get(Constant.USER_EMAIL) + " failed to update password.";
            }
        } else {
            status = ResponseStatus.FAIL;
            msg = "Original password is incorrect";
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        response.setResponseMsg(msg);
        return response;
    }
}
