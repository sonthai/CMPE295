package com.api.controller;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.services.DataProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer")
public class DataProcessingController {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingController.class);

    @Autowired
    DataProcessingService dataProcessingService;

    @RequestMapping(method = RequestMethod.POST, value="/recommend", consumes = "application/json")
    public ResponseMessage processUserData(@RequestBody Map<String, String> bodyRequest) {
        log.info("Process data from IoT device API");
        dataProcessingService.processData(bodyRequest);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Kafka API called");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;
    }

}
