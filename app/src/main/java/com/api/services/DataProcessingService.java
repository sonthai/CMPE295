package com.api.services;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.model.UserRequest;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataProcessingService {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingService.class);

    @Autowired
    KafkaProducerService producer;

    @Autowired
    RecommendationService recommendationService;

    public ResponseMessage processData(Map<String, String> data) {
        List<Map<String, Object>> productList = new ArrayList<>();

        String imageName = Utils.saveIncomingImage(data.get("id"), data.get("image"));

        if (!StringUtils.isEmpty(imageName)) {
            UserRequest userRequest = new UserRequest(data.get("id"), data.getOrDefault(Constant.USER_EMAIL, ""), data.get("id"), Integer.valueOf(data.getOrDefault("quantity", "10")));
            userRequest.setKeepImage(Boolean.valueOf(data.getOrDefault("keepImage", "false")));
            userRequest.setGender(data.getOrDefault(Constant.GENDER, ""));
            productList = producer.send(userRequest);
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg("List of recommended products for " + imageName);
        response.setData(productList);

        return response;
    }

    public ResponseMessage getRecommendation(Map<String, Object> data) {
        List<Map<String, Object>> results = recommendationService.recommend(data);
        String msg = "";

        if (data.containsKey("email") || NonCustomerResponseService.getMessageStoreInstance().isRecommended()) {
            NonCustomerResponseService.getMessageStoreInstance().resetImageCnt();
            msg = "Recommended items from engine.";
        } else {
            msg = "Randomized items from engine.";
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg(msg);
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        responseMessage.setData(results);

        return responseMessage;
    }

    public List<Map<String, Object>> getPromotion(Map<String, Object> data) {
        return recommendationService.promotions(data);
    }

    // Support Test method
    public String saveImageToServer(Map<String, String> data) {
        return Utils.saveIncomingImage(data.get("id"), data.get("image"));
    }
}
