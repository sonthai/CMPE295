package com.api.services;


import com.api.constant.Constant;
import com.api.constant.Constant.*;
import com.api.database.transaction.UserTransaction;
import com.api.model.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                users.get(0).remove("Password");
            } else {
                status = ResponseStatus.FAIL;
                msg = data.get(Constant.USER_EMAIL) + " failed to login.";
                users.clear();
            }

        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        response.setResponseMsg(msg);
        response.setData(users);
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

    @Override
    public ResponseMessage updateProfile(Map<String, String> data) {
        log.info("Update profile service is called");

        List<Map<String, Object>> users = userTransaction.getUsers(data);
        ResponseStatus status = ResponseStatus.FAIL;
        if (users.size() == 1) {
            data.put("Id", users.get(0).get("Id").toString());
            boolean updateStatus = userTransaction.updateProfile(data);

            if (updateStatus) {
                status = ResponseStatus.OK;
            }
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(status);
        String msg = "";
        if (status == ResponseStatus.OK) {
            msg = "Profile updated successfully";
        } else {
            msg = "Profile failed to update";
        }

        response.setResponseMsg(msg);
        return response;
    }
}
