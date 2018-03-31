package com.api.controller;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.services.DataProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/customer")
public class DataProcessingController {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingController.class);

    @Autowired
    DataProcessingService dataProcessingService;

    @RequestMapping(method = RequestMethod.POST, value="/processData", consumes = "application/json")
    public ResponseMessage processUserData(@RequestBody Map<String, String> bodyRequest) {
        // Json {"id": "", "image" : ""} where data is base64ImgStr, and id is unique value for image name
        log.info("Process data from IoT device  and mobile API via Kafka");
        String output = dataProcessingService.processData(bodyRequest);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg(output);
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.GET, value="/recommend", consumes = "application/json")
    public ResponseMessage getRecommendation(@RequestBody Map<String, String> bodyRequest) {
        log.info("Get recommendation API");
        List<Map<String, Object>> results =  dataProcessingService.getRecommendation(bodyRequest);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Retrieve result from engine");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        responseMessage.setData(results);
        return responseMessage;
    }


    // Testing API for uploading image
    @RequestMapping(method = RequestMethod.POST, value = "/upload", produces = "application/json")
    public ResponseMessage upload(@RequestBody Map<String, String> bodyRequest) {
        log.info("Test Upload API");
        String output =  dataProcessingService.saveImageToServer(bodyRequest);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg(output);
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;
    }
}
