package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.api.constant.Constant;
import com.api.database.domain.ProductDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ProductRepository extends BasicAWSDynamoOps<ProductDao> {
    public List<Map<String, Object>> findProductByImageName(String image) {
        Map<String, Object> valueMap = new ValueMap().withString(":image", image);
        List<Map<String, Object>> results = scan(Constant.PRODUCT_TABLE, "Id, Product, Image, Brand, Price", "Image = :image", valueMap);

        // Modified image url
        for (Map<String, Object> item: results) {
            StringBuilder image_url = new StringBuilder(Constant.IMAGE_URL).append(item.get("Image"));
            item.put("Image", image_url.toString());
        }

        return results;
    }

    public List<Map<String, Object>> findProducts(List<String> items) {
        List<AttributeValue> attributeValues = items.stream()
                .map(item -> new AttributeValue().withS(item))
                .collect(Collectors.toList());
        List<Map<String, Object>> results = new ArrayList<>();


        if (attributeValues.size() > 0) {
            results = getProductsWithFilter("Image", attributeValues);
        }

        return results;

    }

    public List<Map<String, Object>> getProductsWithFilter(String filterCondition,  List<AttributeValue> attributeValues) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        List<Map<String, Object>> results = new ArrayList<>();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition(filterCondition,
                new Condition()
                        .withComparisonOperator(ComparisonOperator.IN)
                        .withAttributeValueList(attributeValues));

        List<ProductDao> scanResult =  mapper.scan(ProductDao.class, scanExpression);

        ObjectMapper objectMapper = new ObjectMapper();
        scanResult.forEach((ProductDao productDao) -> {
            String imageUrl = Constant.IMAGE_URL + productDao.getImage();
            productDao.setImage(imageUrl);
            results.add(objectMapper.convertValue(productDao, Map.class));
        });

        return results;
    }

    public List<Map<String, Object>> findProductsForMember() {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<ProductDao> scanResult = mapper.scan(ProductDao.class, scanExpression);

        List<Map<String, Object>> results = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        scanResult.forEach(productDao -> results.add(objectMapper.convertValue(productDao, Map.class)));

        return results;




    }
}
