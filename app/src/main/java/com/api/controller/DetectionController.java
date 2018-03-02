package com.api.controller;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.services.KafkaProducer;
import com.api.services.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer")
public class DetectionController {
    private static final Logger log = LoggerFactory.getLogger(DetectionController.class);

    //@Autowired
    //RecommendationService recommendationService;

    @Autowired
    KafkaProducer kafkaProducer;

    @RequestMapping(method = RequestMethod.POST, value="/recommend", consumes = "application/json")
    public ResponseMessage processUserData(@RequestBody Map<String, String> bodyRequest) {
        log.info("Process data from IoT device API");
        kafkaProducer.send(bodyRequest.get("message"));

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Kafka API called");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;
    }

}
