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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository extends BasicAWSDynamoOps<ProductDao> {
    private static final Logger log = LoggerFactory.getLogger(BasicAWSDynamoOps.class);
    private static List<Map<String, Object>> products = null;

    public List<Map<String, Object>> findProductByImageName(String image) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            Map<String, Object> valueMap = new ValueMap().withString(":image", image);
            results = scan(Constant.PRODUCT_TABLE, "Id, Product, Image, Brand, Price", "Image = :image", valueMap);

            // Modified image url
            for (Map<String, Object> item : results) {
                String encodedImageName = URLEncoder.encode(item.get("Image").toString(), StandardCharsets.UTF_8.name());
                item.put("Image", Constant.IMAGE_URL + encodedImageName);
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
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
        List<Map<String, Object>> results = new ArrayList<>();

        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition(filterCondition,
                new Condition()
                        .withComparisonOperator(ComparisonOperator.IN)
                        .withAttributeValueList(attributeValues));

        List<ProductDao> scanResult = mapper.scan(ProductDao.class, scanExpression);

        ObjectMapper objectMapper = new ObjectMapper();
        scanResult.forEach((ProductDao productDao) -> {
            try {
                String encodedImageName = URLEncoder.encode(productDao.getImage(), StandardCharsets.UTF_8.name());
                productDao.setImage(Constant.IMAGE_URL + encodedImageName);
                results.add(objectMapper.convertValue(productDao, Map.class));
            } catch (UnsupportedEncodingException e) {}
        });
        return results;
    }

    public List<Map<String, Object>> retrieveVendorProducts() {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<ProductDao> scanResult = mapper.scan(ProductDao.class, scanExpression);

        List<Map<String, Object>> results = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        scanResult.forEach(productDao -> {
            try {
                String encodedImageName = URLEncoder.encode(productDao.getImage(), StandardCharsets.UTF_8.name());
                productDao.setImage(Constant.IMAGE_URL + encodedImageName);
                results.add(objectMapper.convertValue(productDao, Map.class));
            } catch (UnsupportedEncodingException ex) {};
        });

        return results;
    }

    public List<Map<String, Object>> findRandomProducts(int quantity) {

        List<Map<String, Object>>  temp = new ArrayList<>(getProducts());

        List<Map<String, Object>> results =  new ArrayList<>();

        while (results.size() < quantity) {
            int index = new Random().nextInt(quantity);
            results.add(temp.remove(index));
        }

        return results;
    }

    public List<Map<String, Object>> getProducts() {
        if (products == null) {
            products = retrieveVendorProducts();
        }

        return products;
    }

    public void randomizeDiscount(List<Map<String, Object>> result) {
        Random rand = new Random();

        int[] percentages = new int[]{5, 10, 15, 20};

        result.forEach(p -> {
            int randomDiscount = percentages[rand.nextInt(percentages.length)];
            double discountPrice = Math.round((double) p.get("price") * (100 - randomDiscount)) / 100;
            p.put("special_price", String.format("%4.2f", discountPrice));
        });
    }
}
