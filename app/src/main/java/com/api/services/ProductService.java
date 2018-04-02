package com.api.services;

import com.api.database.repository.ProductRepository;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductRepository productRepository;

    public int addedProducts(String filePath) {
        return productRepository.saveItemsInBatch(Utils.createProductList(filePath));
    }

    public List<Map<String, Object>> getProduct(String product) {
        return productRepository.findProductByImageName(product);
    }

    public List<Map<String, Object>> getProducts(List<String> products) {
        return productRepository.findProducts(products);
    }
}
