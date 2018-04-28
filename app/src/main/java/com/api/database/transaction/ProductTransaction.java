package com.api.database.transaction;

import com.api.database.domain.ProductDao;
import com.api.database.repository.ProductRepository;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ProductTransaction extends DataTransactionManager<Map<String, Object>, ProductDao> {
    private static final Logger log = LoggerFactory.getLogger(ProductTransaction.class);

    @Autowired
    ProductRepository productRepository;

    public List<Map<String, Object>> findProducts(List<String> items) {
        return productRepository.findProducts(items);
    }

    public List<Map<String, Object>> findProductsForMember() {
        return productRepository.retrieveVendorProducts();
    }

    public List<Map<String, Object>> findRandomProducts(int quantity) {
        return productRepository.findRandomProducts(quantity);
    }

    public List<Map<String, Object>> findPromotions(int quantity) {
        List<Map<String, Object>> promotions =  productRepository.findRandomProducts(quantity);
        productRepository.randomizeDiscount(promotions);
        return promotions;
    }

    public int addedProducts(String filePath) {
        return productRepository.saveItemsInBatch(Utils.createProductList(filePath));
    }

    public List<Map<String, Object>> getProduct(String product) {
        return productRepository.findProductByImageName(product);
    }
}
