package com.api.services;

import com.api.database.transaction.UserHistoryTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserHistoryService {
    private static final Logger log = LoggerFactory.getLogger(UserHistoryService.class);

    @Autowired
    UserHistoryTransaction userHistoryTransaction;

    public List<Map<String, Object>> findProductByUserEmail(String userEmail) {
       return userHistoryTransaction.findProductByUserEmail(userEmail);
    }
}
