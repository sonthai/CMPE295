package com.api.database.transaction;

import com.api.database.domain.UserDao;
import com.api.database.repository.UserRepository;
import com.api.model.User;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserTransaction implements ITransaction<User, UserDao> {
    private static final Logger log = LoggerFactory.getLogger(UserTransaction.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Override
    public void save(User data) {
        try {
            userRepository.save(convert(data));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(User data) {

    }

    @Override
    public UserDao convert(User rawData) throws Exception {
        String encrypted = utils.encrypt(rawData.getPassword());
        UserDao userDao = new UserDao(UUID.randomUUID(), rawData.getUsername(), encrypted, rawData.getEmail());
        return userDao;
    }

    public boolean findUser(User rawData) {
        List<UserDao> users = userRepository.findUserByUserName(rawData.getUsername());
        if (users.size() < 1)
            return  false;
        else {
            String encrypted = "";
            try {
                encrypted = utils.encrypt(rawData.getPassword());
            } catch (Exception e){
                log.error(e.getMessage());
            } finally {
                if (encrypted.equals(users.get(0).getPassword())) {
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

}
