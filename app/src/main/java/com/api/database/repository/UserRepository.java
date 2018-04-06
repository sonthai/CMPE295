package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.api.constant.Constant;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public class UserRepository<UserDao> extends BasicAWSDynamoOps<UserDao> {
    public List<Map<String, Object>> findByUserEmail(String userEmail) {
        Map<String, Object> valueMap = new ValueMap().withString(":email", userEmail);
        return scan(Constant.USER_TABLE, "Id, Email, Password", "Email = :email", valueMap);
    }

    public boolean updatePassword(Map<String, String> dataMap) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Id", new AttributeValue().withS(dataMap.get("Id")));

        Map<String, AttributeValue> attributeValues = new HashMap<>();
        AttributeValue pwd =  new AttributeValue().withS(dataMap.get(Constant.USER_PASSWORD));
        attributeValues.put(":password", pwd);

        StringBuilder sb = new StringBuilder();
        sb.append("set Password = :password");

        UpdateItemResult result = update(Constant.USER_TABLE, key, sb.toString(), attributeValues);

        if (result.getAttributes().get("Password").equals(pwd)) {
            return true;
        }

        return false;

    }
}
