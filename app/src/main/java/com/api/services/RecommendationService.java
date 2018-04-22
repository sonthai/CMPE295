package com.api.services;

import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import com.api.database.transaction.ProductTransaction;
import com.api.database.transaction.UserHistoryTransaction;
import com.api.model.UserRequest;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService implements IRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    UserHistoryTransaction userHistoryTransaction;

    @Autowired
    ProductTransaction productTransaction;

    @Autowired
    UserHistoryService userHistoryService;


    @Override
    public List<Map<String, Object>> processRecommendation(UserRequest userRequest) {
        Map<String, Object> params =  new HashMap<>();
        params.put("--top_k", userRequest.getQuantity());
        params.put("--image_file", getImagePath(userRequest.getImage()));
        Utils.executeScript("classify_images.py", params);
        // Add logic to check if <imageId>.json exists then parse json file
        Path jsonResultFilePath =  Utils.getProductJsonFilePath(userRequest.getRequestId());

        List<String> images = new ArrayList<>();
        List<Map<String, Object>> productList = new ArrayList<>();
        if (Files.exists(jsonResultFilePath)) {
            // Remove the upload image
            String msg = Utils.removeImage(getImagePath(userRequest.getImage()));
            log.info(msg);

            images = Utils.getSimilarProducts(userRequest.getRequestId());
            if (!StringUtils.isEmpty(userRequest.getUserEmail())) {
                productList = updateUserHistory(images, userRequest.getUserEmail());
            } else {
                // Store result in in-memory MessageStore for non member users
                NonCustomerResponseService instance = NonCustomerResponseService.getMessageStoreInstance();
                images.forEach(image -> instance.addImages(image));

                //List<Integer> productIds = productRepository.findUserByUserName(new String[] {imageName});
                //instance.addUserId(userRequest.getUserId(), productIds.get(0));
            }

            msg = Utils.removeImage(jsonResultFilePath.toString());
            log.info("Finished processing json file. Remove {}", msg);
        }
        return productList;
    }

    @Override
    public List<Map<String, Object>> recommend(Map<String, Object> requestBody) {
        String email = (String) requestBody.getOrDefault("email", "");
        int quantity = (int) requestBody.getOrDefault("quantity", Constant.NUMBER_PRODUCTS_RECOMMENDED);
        List<Map<String, Object>> results;

        if (!StringUtils.isEmpty(email)) {
            results =  getRecommendationForMember(email, quantity);
        } else {
            results =  getRecommendationForNonMember(quantity);
        }

        return results;
    }

    private List<Map<String, Object>> getRecommendationForNonMember(int quantity) {
        // To Do Retrieve message based on the id from Message store
        List<String> imageList = NonCustomerResponseService.getMessageStoreInstance().getImages(quantity);
        if (imageList.size() > 0) {
            return productTransaction.findProducts(imageList);
        } else {
            return productTransaction.findProductsForNonMember(quantity);
        }
    }

    private List<Map<String, Object>> getRecommendationForMember(String email, int quantity) {
        List<Map<String, Object>> products = new ArrayList<>();

        // Check if there are any data in user history table
        products  = userHistoryTransaction.findProductByUserEmail(email);

        if (products.size() == 0) {
            products = productTransaction.findProductsForMember();
        } /* else {
            // Perform recommendation
        }*/

        List<Map<String, Object>> results =  new ArrayList<>();

        if (products.size() > quantity) {
            while (results.size() < quantity) {
                int index = new Random().nextInt(quantity);
                results.add(products.remove(index));
            }
        } else {
            results = products;
        }

        return results;
    }

    private String getImagePath(String imageId) {
        return Paths.get(Constant.IMAGE_PATH, imageId + ".jpg").toString();
    }

    private List<Map<String, Object>> updateUserHistory(List<String> images, String email) {
        List<Map<String, Object>> productList  = productTransaction.findProducts(images);

        List<String> productIds = productList.stream().map(p-> p.get("id").toString()).collect(Collectors.toList());

        List<Integer> existingProductIdList = userHistoryService.retrieveUserHistoryBasedOnProducts(productIds, email);

        List<Integer> newRecommendProducts = productIds.stream().map(Integer::parseInt)
                                                .collect(Collectors.toList()).stream()
                                                .filter(id -> !existingProductIdList.contains(id))
                                                .collect(Collectors.toList());

        // Save the user email and recommended products in user request history table
        List<UserHistoryDao> userHistoryDaoAddList = new ArrayList<>();
        newRecommendProducts.forEach(productId -> {
            UserHistoryDao uhd = new UserHistoryDao(email, Integer.valueOf(productId));
            uhd.setFrequency(1);
            userHistoryDaoAddList.add(uhd);
        });

        if (userHistoryDaoAddList.size() > 0) {
            userHistoryTransaction.saveUserHistoryInBatch(userHistoryDaoAddList);
        }

        return productList;
    }

    // Reserve for recommendation implementation for mobile
    private List<Map<String, Object>> performRecommendationBasedOnUserHistory () {
        List<Map<String, Object>> recommendedProduct = new ArrayList<>();

        return recommendedProduct;
    }

}
