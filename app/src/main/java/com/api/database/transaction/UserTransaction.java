package com.api.database.transaction;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.api.constant.Constant;
import com.api.database.domain.UserDao;
import com.api.database.repository.UserRepository;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class UserTransaction implements ITransaction<Map<String, String>, UserDao> {
    private static final Logger log = LoggerFactory.getLogger(UserTransaction.class);


    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Override
    public void save(Map<String, String> data) {
        try {
            userRepository.save(convert(data));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(Map<String, String> data) {

    }

    @Override
    public UserDao convert(Map<String, String> rawData) throws Exception {
        String encrypted = utils.encrypt(rawData.get(Constant.USER_PASSWORD));
        UserDao userDao = new UserDao();
        userDao.setEmail(rawData.get(Constant.USER_EMAIL));
        userDao.setPassword(encrypted);
        return userDao;
    }

    public boolean isPasswordMatch(Map<String, Object> userMap, String userPwdInput) {
        String encrypted = "";
        try {
            encrypted = utils.encrypt(userPwdInput);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return encrypted.equals(userMap.get("Password"));
        }

    }

    public List getUsers(Map<String, String> rawData) {
        return userRepository.findByUserEmail(rawData.get(Constant.USER_EMAIL));
    }

    public boolean updatePassword(Map<String, String> userMap) {
        try {
            userMap.put(Constant.USER_PASSWORD, utils.encrypt(userMap.get(Constant.USER_PASSWORD)));
            return userRepository.updatePassword(userMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
