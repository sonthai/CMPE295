package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class BasicAWSDynamoOps<T> {
    @Autowired
    AmazonDynamoDB amazonDynamoDB;

    public void save(T obj) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        mapper.save(obj);
    }

    public List<Map<String, Object>> scan(String tableName, String projection,
                                          String filter, Map<String, Object> valueMap) {
        List<Map<String, Object>> results = new ArrayList<>();
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable(tableName);
        ScanSpec scanSpec = new ScanSpec().withProjectionExpression(projection)
                .withFilterExpression(filter)
                .withValueMap(valueMap);
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            Map<String, Object> itemMap = item.asMap();
            results.add(itemMap);
        }

        return results;
    }

    protected DynamoDBScanExpression getScanExpression(Map<String, List<AttributeValue>> filterConditionMap) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        filterConditionMap.forEach((filterCondition, attributeValues) -> {
            scanExpression.addFilterCondition(filterCondition,
                    new Condition()
                            .withComparisonOperator(ComparisonOperator.IN)
                            .withAttributeValueList(attributeValues));
        });

        return scanExpression;
    }

    public int saveItemsInBatch(List<T> items) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        List<DynamoDBMapper.FailedBatch> failedBatch  = mapper.batchSave(items);

        return items.size() - failedBatch.size();
    }

    public UpdateItemResult update(String tableName, Map<String, AttributeValue> key, String updateExpression, Map<String, AttributeValue> attributeValues) {
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withUpdateExpression(updateExpression)
                .withExpressionAttributeValues(attributeValues)
                .withReturnValues(ReturnValue.UPDATED_NEW);
        return amazonDynamoDB.updateItem(updateItemRequest);
    }

    public void updateBatchRequestItems(String tableName, List<Map<String, AttributeValue>> keys, Map<String, String> updateExpressionMap,  Map<String, Map<String, AttributeValue>> attributeValuesMap) {
        for (Map<String, AttributeValue> keyMap: keys) {
            for (AttributeValue key: keyMap.values()) {
                update(tableName, keyMap, updateExpressionMap.get(key.getS()), attributeValuesMap.get(key.getS()));
            }
        }
    }


}
