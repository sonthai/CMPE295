package com.api.services;

import com.api.constant.Constant;
import com.api.database.domain.UserHistoryDao;
import com.api.database.repository.ProductRepository;
import com.api.database.repository.UserHistoryRepository;
import com.api.database.transaction.UserHistoryTransaction;
import com.api.model.ResponseMessage;
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
    ProductRepository productRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Override
    public ResponseMessage processRecommendation(UserRequest userRequest) {
        Map<String, Object> params =  new HashMap<>();
        params.put("--top_k", userRequest.getQuantity());
        params.put("--image_file", getImagePath(userRequest.getImage()));
        //Utils.executeScript("classify_images.py", "--image_file", getImagePath(userRequest.getImagePath()));
        Utils.executeScript("classify_images.py", params);
        // Add logic to check if <imageId>.json exists then parse json file
        Path jsonResultFilePath =  Utils.getProductJsonFilePath(userRequest.getRequestId());
        List<Map<String, Object>> productList = new ArrayList<>();
        if (Files.exists(jsonResultFilePath)) {
            // Remove the upload image
            String msg = Utils.removeImage(getImagePath(userRequest.getImage()));
            log.info(msg);

            //List<Map<String, Object>> productList = new ArrayList<>();
            List<String> images = Utils.getSimilarProducts(userRequest.getRequestId());
            if (!StringUtils.isEmpty(userRequest.getUserEmail())) {
                productList = productRepository.findProducts(images);

                // Save the user email and recommended product in user request history table
                List<UserHistoryDao> userHistoryDaoList = new ArrayList<>();
                productList.forEach(p -> {
                    UserHistoryDao uhd = new UserHistoryDao(userRequest.getUserEmail(), Integer.valueOf(p.get("Id").toString()));
                    userHistoryDaoList.add(uhd);
                });
                //Map<String, Object> request = new HashMap<>();
                //request.put(Constant.USER_EMAIL, userRequest.getUserEmail());
                //request.put(Constant.PRODUCT_ID, productList.get(0).get("Id"));
                //userHistoryTransaction.save(request);
                userHistoryTransaction.saveUserHistoryInBatch(userHistoryDaoList);
            } else {
                // Store result in in-memory MessageStore for non member users
                NonCustomerResponseService instance = NonCustomerResponseService.getMessageStoreInstance();
                images.forEach(image -> instance.addImages(image));

                //List<Integer> productIds = productRepository.findUserByUserName(new String[] {imageName});
                //instance.addUserId(userRequest.getUserId(), productIds.get(0));
            }
        }
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg("List of recommended products");
        response.setData(productList);
        return response;
    }

    @Override
    public List<Map<String, Object>> recommend(Map<String, Object> requestBody) {
        String email = (String) requestBody.getOrDefault("email", "");
        int quantity = (int) requestBody.getOrDefault("quantity", Constant.NUMBER_PRODUCTS_RECOMMENDED);
        List<Map<String, Object>> results;

        if (!StringUtils.isEmpty(email)) {
            results =  getRecommendationForMember(email, quantity);
        } else {
            results =  getRecommendationForNonMemmber(quantity);
        }

        return results;
    }

    private List<Map<String, Object>> getRecommendationForNonMemmber(int quantity) {
        // To Do Retrieve message based on the id from Message store
        List<String> imageList = NonCustomerResponseService.getMessageStoreInstance().getImages(quantity);
        return productRepository.findProducts(imageList);
    }

    private List<Map<String, Object>> getRecommendationForMember(String email, int quantity) {
        List<Map<String, Object>> products = new ArrayList<>();

        // Check if there are any data in user history table
        products  = userHistoryTransaction.findProductByUserEmail(email);

        if (products.size() == 0) {
            products = productRepository.findProductsForMember();
        }

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
        return Paths.get(Constant.IMAGE_PATH, imageId + ".jpeg").toString();
    }
}
