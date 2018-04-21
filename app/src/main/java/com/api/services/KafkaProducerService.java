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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    @Autowired
    KafkaTemplate<String, UserRequest> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Autowired
    RecommendationService recommendationService;


    public List<Map<String, Object>> send(UserRequest userRequest) {
        log.info("sending data=" + userRequest.getImage());

        // User Request sent from mobile
        if (!StringUtils.isEmpty(userRequest.getUserEmail())) {
            return recommendationService.processRecommendation(userRequest);
        } else {
            // User Request sent from Jetson Tx2
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    kafkaTemplate.send(kafkaTopic, userRequest);
                }
            });
            //kafkaTemplate.send(kafkaTopic, userRequest);
            executorService.shutdown();
            return null;
        }

    }
}
