package com.api.database.transaction;

import com.api.constant.Constant;
import com.api.database.domain.UserDao;
import com.api.database.repository.UserRepository;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public boolean findUser(Map<String, String> rawData) {
        List<Map<String, Object>> users = userRepository.findByUserEmail(rawData.get(Constant.USER_EMAIL));
        if (users.size() < 1)
            return  false;
        else {
            String encrypted = "";
            try {
                encrypted = utils.encrypt(rawData.get(Constant.USER_PASSWORD));
            } catch (Exception e){
                log.error(e.getMessage());
            } finally {
                if (encrypted.equals(users.get(0).get("Password"))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public boolean userExists(Map<String, String> rawData) {
        List<Map<String, Object>> users = userRepository.findByUserEmail(rawData.get(Constant.USER_EMAIL));
        return users.size() == 1;
    }

}
