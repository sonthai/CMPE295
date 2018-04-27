package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.api.constant.Constant;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public class UserRepository<UserDao> extends BasicAWSDynamoOps<UserDao> {

    private Map<String, String> updateFields = new ImmutableMap.Builder<String, String>()
            .put("fullName", "FullName")
            .put("gender", "Gender")
            .put("birthDate", "BirthDate")
            .put("yearJoined", "YearJoined")
            .put("avatarBase64", "Avatar")
            .build();

    public List<Map<String, Object>> findByUserEmail(String userEmail) {
        Map<String, Object> valueMap = new ValueMap().withString(":email", userEmail);
        return scan(Constant.USER_TABLE, "Id, Email, Password, FullName, Gender, BirthDate, YearJoined, Avatar",
                "Email = :email", valueMap);
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

    public boolean updateProfile(Map<String, String> dataMap) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("Id", new AttributeValue().withS(dataMap.remove("Id")));
        dataMap.remove("email");

        StringBuilder updateStmt = new StringBuilder();

        Map<String, AttributeValue> attributeValues = new HashMap<>();

        for (String keyMap: dataMap.keySet()) {
            if (updateStmt.length() > 0) {
                updateStmt.append(",");
            } else {
                updateStmt.append("set");
            }
            AttributeValue attrVal = new AttributeValue().withS(dataMap.get(keyMap));
            StringBuilder sb = new StringBuilder(":").append(keyMap);
            attributeValues.put(sb.toString(), attrVal);
            updateStmt.append(" ").append(updateFields.get(keyMap)).append(" = ").append(sb.toString());
        }

        UpdateItemResult result = update(Constant.USER_TABLE, key, updateStmt.toString(), attributeValues);

        return result.getAttributes().keySet().size() == attributeValues.keySet().size();
    }

}
