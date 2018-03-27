package com.api.database.transaction;

import com.api.database.domain.UserHistoryDao;
import com.api.database.repository.UserHistoryRepository;
import com.api.model.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserHistoryTransaction implements ITransaction<UserRequest,UserHistoryDao> {
    private static final Logger log = LoggerFactory.getLogger(UserHistoryTransaction.class);
    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Override
    public void save(UserRequest data) {
        try {
            userHistoryRepository.save(convert(data));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(UserRequest data) {

    }

    @Override
    public UserHistoryDao convert(UserRequest rawData) throws Exception {
        UserHistoryDao userHistoryDao = new UserHistoryDao();
        userHistoryDao.setUserEmail(rawData.getUserEmail());
        userHistoryDao.setProduct(rawData.getImagePath());
        return userHistoryDao;
    }
}
