package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserHistoryRepository extends BasicAWSDynamoOps<UserHistoryDao> {
    @Autowired
    ProductRepository productRepository;

    public List<Map<String, Object>> findProductByUserEmail(String userEmail) {
        Map<String, Object> valueMap = new ValueMap().withString(":email", userEmail);
        List<Map<String, Object>> userHistory = scan(Constant.USER_HISTORY_TABLE, "Id, Email, Product", "Email = :email", valueMap);

        List<AttributeValue> productIds = userHistory.stream()
                .map(p -> new AttributeValue().withN(p.get("Product").toString()))
                .collect(Collectors.toList());
        List<Map<String, Object>> results = new ArrayList<>();

        if (productIds.size() > 0) {
            results = productRepository.getProductsWithFilter("Id", productIds);
        }

        return results;
    }

    public List<UserHistoryDao> findUserHistoryFromProductIdAndEmail(List<String> productIds, String email) {
        Map<String, List<AttributeValue>> filterConditionMap = new HashMap<>();

        List<AttributeValue> prodAttributeValues = productIds.stream()
                .map(item -> new AttributeValue().withN(item))
                .collect(Collectors.toList());
        List<AttributeValue> emailAttributeValues = Arrays.asList(new AttributeValue().withS(email));

        filterConditionMap.put("Product", prodAttributeValues);
        filterConditionMap.put("Email", emailAttributeValues);

        List<UserHistoryDao> results = new ArrayList<>();

        if (filterConditionMap.keySet().size() > 0) {
            results = getProductsWithFilter(filterConditionMap);
        }

        return results;
    }

    private List<UserHistoryDao> getProductsWithFilter(Map<String, List<AttributeValue>> filterConditionMap) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        DynamoDBScanExpression scanExpression = getScanExpression(filterConditionMap);
        List<UserHistoryDao> scanResult = mapper.scan(UserHistoryDao.class, scanExpression);
        return scanResult;
    }

    public Map<String, Map<Integer, Double>> queryAllUserHistoryData () {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<UserHistoryDao> userHistoryRows = mapper.scan(UserHistoryDao.class, scanExpression);

        Map<String, Map<Integer, Double>> userHistoryData = new HashMap<>();

        for (UserHistoryDao uhd: userHistoryRows) {
            if (!userHistoryData.containsKey(uhd.getUserEmail())) {
                userHistoryData.put(uhd.getUserEmail(), new HashMap<>());
            }
            userHistoryData.get(uhd.getUserEmail()).put(uhd.getProduct(), Double.valueOf(uhd.getFrequency()));
        }


        return userHistoryData;
    }




}
