package com.api.database.transaction;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import com.api.database.repository.UserHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserHistoryTransaction extends DataTransactionManager<Map<String, Object>,UserHistoryDao> { //implements ITransaction<Map<String, Object>,UserHistoryDao> {
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
    public UserHistoryDao convert(Map<String, Object> rawData) throws Exception {
        UserHistoryDao userHistoryDao = new UserHistoryDao();
        userHistoryDao.setUserEmail(rawData.get(Constant.USER_EMAIL).toString());
        userHistoryDao.setProduct(Integer.valueOf(rawData.get(Constant.PRODUCT_ID).toString()));
        return userHistoryDao;
    }

    public int saveUserHistoryInBatch(List<UserHistoryDao> userHistoryDaoList) {
        return userHistoryRepository.saveItemsInBatch(userHistoryDaoList);
    }

    public List<Map<String, Object>> findProductByUserEmail(String userEmail) {
        return userHistoryRepository.findProductByUserEmail(userEmail);
    }
    
    public List<UserHistoryDao> findUserHistoryFromProductIds(List<String> productIds, String email) {
        return userHistoryRepository.findUserHistoryFromProductIdAndEmail(productIds, email);
    }

    public void updateProductHistoryFrequency(String tableName, List<Map<String, AttributeValue>> keys, Map<String, String> updateExpressionMap, Map<String, Map<String, AttributeValue>> attributeValuesMap) {
        userHistoryRepository.updateBatchRequestItems(tableName, keys, updateExpressionMap, attributeValuesMap);
    }
}
