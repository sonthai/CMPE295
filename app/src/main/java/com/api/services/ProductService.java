package com.api.services;

import com.api.database.transaction.ProductTransaction;
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
    ProductTransaction productTransaction;

    public int addedProducts(String filePath) {
        return productTransaction.addedProducts(filePath);
    }

    public List<Map<String, Object>> getProduct(String product) {
        return productTransaction.getProduct(product);
    }

    public List<Map<String, Object>> getProducts(List<String> products) {
        return productTransaction.findProducts(products);
    }
}
