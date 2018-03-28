package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public int saveItemsInBatch(List<T> items) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        List<DynamoDBMapper.FailedBatch> failedBatch  = mapper.batchSave(items);

        return items.size() - failedBatch.size();
    }
}
