package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.api.constant.Constant;
import com.api.database.domain.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserHistoryRepository<UserHistoryDao> extends BasicAWSDynamoOps<UserHistoryDao> {
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
}
