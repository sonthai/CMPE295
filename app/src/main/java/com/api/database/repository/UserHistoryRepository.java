package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.api.constant.Constant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserHistoryRepository<UserHistoryDao> extends BasicAWSDynamoOps<UserHistoryDao> {
    public List<Map<String, Object>> findProductByUserEmail(String userEmail) {
        Map<String, Object> valueMap = new ValueMap().withString(":email", userEmail);
        return scan(Constant.USER_HISTORY_TABLE, "Id, Email, Product", "Email = :email", valueMap);
    }
}
