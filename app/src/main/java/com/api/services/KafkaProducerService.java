package com.api.services;

import com.api.model.ResponseMessage;
import com.api.model.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    @Autowired
    KafkaTemplate<String, UserRequest> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Autowired
    RecommendationService recommendationService;

    public ResponseMessage send(UserRequest userRequest) {
        log.info("sending data=" + userRequest.getImage());

        // User Request sent from mobile
        if (!StringUtils.isEmpty(userRequest.getUserEmail())) {
            return recommendationService.processRecommendation(userRequest);
        } else {
            // User Request sent from Jetson Tx2
            kafkaTemplate.send(kafkaTopic, userRequest);
            return null;
        }

    }
}
