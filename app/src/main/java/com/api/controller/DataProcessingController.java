package com.api.controller;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.services.DataProcessingService;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


@RestController
@RequestMapping("/customer")
public class DataProcessingController {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingController.class);

    @Autowired
    DataProcessingService dataProcessingService;

    @RequestMapping(method = RequestMethod.POST, value="/processData", consumes = "application/json")
    public ResponseMessage processUserData(@RequestBody Map<String, String> bodyRequest) {
        // Json {"id": "", "image" : "", "quantity": <value>} where data is base64ImgStr, and id is unique value for image name
        // for mobile user, need to pass email in the body request
        log.info("Process data from IoT device  and mobile API via Kafka");
        return dataProcessingService.processData(bodyRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value="/recommend", consumes = "application/json")
    @CrossOrigin
    public ResponseMessage getRecommendation(@RequestBody Map<String, Object> bodyRequest) {
        log.info("Get recommendation API");
        return dataProcessingService.getRecommendation(bodyRequest);
    }

    @RequestMapping(method = RequestMethod.POST, value="/promotions", consumes = "application/json")
    @CrossOrigin
    public ResponseMessage getTodaySpecial(@RequestBody Map<String, Object> bodyRequest) {
        log.info("Get promotion API");
        List<Map<String, Object>> results =  dataProcessingService.getPromotion(bodyRequest);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Special Promotions");
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

    @RequestMapping(method = RequestMethod.POST, value = "/testScript")
    public void uploadImage(@RequestBody Map<String, String> map) throws IOException {
        String image_file = map.get("image");
        Map<String, Object> params =  new HashMap<>();
        params.put("--top_k", "12");
        params.put("--image_file", Paths.get(Constant.IMAGE_PATH,  image_file).toString());
        Utils.executeScript("classify_images.py", params);
    }
}
