package com.api.services;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import com.api.database.transaction.UserHistoryTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class UserHistoryService {
    private static final Logger log = LoggerFactory.getLogger(UserHistoryService.class);

    @Autowired
    UserHistoryTransaction userHistoryTransaction;

    public List<Map<String, Object>> findProductByUserEmail(String userEmail) {
       return userHistoryTransaction.findProductByUserEmail(userEmail);
    }

    public List<Integer> retrieveUserHistoryBasedOnProducts(List<String> productIds, String email) {
        List<UserHistoryDao> userHistory = userHistoryTransaction.findUserHistoryFromProductIds(productIds, email);
        List<Map<String, AttributeValue>> keys = new ArrayList<>();
        Map<String, Map<String, AttributeValue>> attributeValuesMap = new HashMap<>();
        Map<String, String> updateExpressionMap = new HashMap<>();

        userHistory.forEach(uhd -> {
            String key = uhd.getId();
            int freq = uhd.getFrequency();
            Map<String, AttributeValue> keyMap = new HashMap<>();
            keyMap.put("Id", new AttributeValue().withS(key));
            keys.add(keyMap);
            updateExpressionMap.put(key, "set Frequency = :frequency");
            Map<String, AttributeValue> attributeValues = new HashMap<>();
            attributeValues.put(":frequency", new AttributeValue().withN(String.valueOf(freq+1)));
            attributeValuesMap.put(key, attributeValues);
        });

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                userHistoryTransaction.updateProductHistoryFrequency(Constant.USER_HISTORY_TABLE, keys, updateExpressionMap, attributeValuesMap);
            }
        });

        executorService.shutdown();

        return userHistory.stream().map(uhd->uhd.getProduct()).collect(Collectors.toList());
    }
}
