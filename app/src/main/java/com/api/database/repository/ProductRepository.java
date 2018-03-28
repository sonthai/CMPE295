package com.api.database.repository;

import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.api.constant.Constant;
import com.api.database.domain.ProductDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductRepository extends BasicAWSDynamoOps<ProductDao> {
    public List<Map<String, Object>> findProductByImageName(String image) {
        Map<String, Object> valueMap = new ValueMap().withString(":image", image);
        List<Map<String, Object>> results = scan(Constant.PRODUCT_TABLE, "Id, Name, Image, Brand, Price", "Image = :image", valueMap);

        // Modified image url
        for (Map<String, Object> item: results) {
            StringBuilder image_url = new StringBuilder(Constant.S3_URL).append(item.get("Image"));
            item.put("Image", image_url.toString());
        }

        return results;
    }

    public List<Map<String, Object>> findAllProducts(List<String> images) {
        String imagesStr = images.stream().map(i -> new StringBuilder("\"").append(i).append("\"").toString()).collect(Collectors.joining(","));
        StringBuilder sb = new StringBuilder("(").append(imagesStr).append(")");
        Map<String, Object> valueMap = new ValueMap().withString(":image", sb.toString());
        List<Map<String, Object>> results = scan(Constant.PRODUCT_TABLE, "Id, Name, Image, Brand, Price", "Image IN :image", valueMap);

        // Modified image url
        for (Map<String, Object> item: results) {
            StringBuilder image_url = new StringBuilder(Constant.S3_URL).append(item.get("Image"));
            item.put("Image", image_url.toString());
        }

        return results;
    }
}
