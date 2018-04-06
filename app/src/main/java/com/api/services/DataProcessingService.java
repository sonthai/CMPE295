package com.api.services;

import com.api.constant.Constant;
import com.api.model.UserRequest;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class DataProcessingService {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingService.class);

    @Autowired
    KafkaProducerService producer;

    @Autowired
    RecommendationService recommendationService;

    public String processData(Map<String, String> data) {
        String imageName = Utils.saveIncomingImage(data.get("id"), data.get("image"));

        if (!StringUtils.isEmpty(imageName)) {
            UserRequest userRequest = new UserRequest(data.get("id"), data.getOrDefault(Constant.USER_EMAIL, ""), data.get("id"));
            userRequest.setKeepImage(Boolean.valueOf(data.getOrDefault("keepImage", "false")));
            producer.send(userRequest);
        }

        return imageName;
    }

    public List<Map<String, Object>> getRecommendation(Map<String, Object> data) {
        return recommendationService.recommend(data);
    }

    // Support Test method
    public String saveImageToServer(Map<String, String> data) {
        return Utils.saveIncomingImage(data.get("id"), data.get("image"));
    }
}
