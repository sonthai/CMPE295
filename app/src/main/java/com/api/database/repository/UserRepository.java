package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.api.constant.Constant;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository<UserDao> extends BasicAWSDynamoOps<UserDao> {
    public List<Map<String, Object>> findByUserEmail(String userEmail) {
        Map<String, Object> valueMap = new ValueMap().withString(":email", userEmail);
        return scan(Constant.USER_TABLE, "Id, Email, Password", "Email = :email", valueMap);
    }
}
