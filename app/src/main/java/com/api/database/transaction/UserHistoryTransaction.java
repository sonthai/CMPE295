package com.api.database.transaction;

import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import com.api.database.repository.UserHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserHistoryTransaction implements ITransaction<Map<String, Object>,UserHistoryDao> {
    private static final Logger log = LoggerFactory.getLogger(UserHistoryTransaction.class);
    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Override
    public void save(Map<String, Object> data) {
        try {
            userHistoryRepository.save(convert(data));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void delete(Map<String, Object> data) {

    }

    @Override
    public UserHistoryDao convert(Map<String, Object> rawData) throws Exception {
        UserHistoryDao userHistoryDao = new UserHistoryDao();
        userHistoryDao.setUserEmail((String) rawData.get(Constant.USER_EMAIL));
        userHistoryDao.setProduct((int) rawData.get(Constant.PRODUCT_ID));
        return userHistoryDao;
    }

}
