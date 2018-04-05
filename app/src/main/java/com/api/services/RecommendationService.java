package com.api.services;

import com.api.constant.Constant;
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

import java.util.*;

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
        String imageName = Utils.executeScript("classify_images.py", "--image_file", userRequest.getImagePath());
        //imageName = "aero_womentop_1.jpg";
        if (!StringUtils.isEmpty(imageName)) {
            // Remove the upload image
            String msg = Utils.removeImage(userRequest.getImagePath());
            log.info(msg);
        }

        List<Map<String, Object>> productList = new ArrayList<>();
        if (!StringUtils.isEmpty(userRequest.getUserEmail())) {
            productList = productRepository.findProductByImageName(imageName);

            // Save the user email and recommended product in user request history table
            Map<String, Object> request = new HashMap<>();
            request.put(Constant.USER_EMAIL, userRequest.getUserEmail());
            request.put(Constant.PRODUCT_ID, productList.get(0).get("Id"));
            userHistoryTransaction.save(request);
        } else {
            // Store result in in-memory MessageStore for non member users
            NonCustomerResponseService instance = NonCustomerResponseService.getMessageStoreInstance();
            instance.addImages(imageName);

            //List<Integer> productIds = productRepository.findUserByUserName(new String[] {imageName});
            //instance.addUserId(userRequest.getUserId(), productIds.get(0));
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg(imageName);
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
        products  = userHistoryRepository.findProductByUserEmail(email);

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
}
