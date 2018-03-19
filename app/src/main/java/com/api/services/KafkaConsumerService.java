package com.api.services;

import com.api.model.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    RecommendationService recommendationService;

    @KafkaListener(topics="${kafka.topic}")
    public void receive(UserRequest userRequest) {
        log.info("received content = " + userRequest.getImagePath());
        recommendationService.processRecommendation(userRequest);
    }
}
