package com.api.controller;

import com.api.model.ResponseMessage;
import com.api.services.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/data")
public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(method = RequestMethod.POST, value="/register", consumes = "application/json")
    public ResponseMessage register(@RequestBody Map<String, String> bodyRequest) {
        log.info("Register API is called");

        ResponseMessage response = registrationService.registerService(bodyRequest);

        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test", produces = "application/json")
    public ResponseMessage testApi() {
        log.info("Test Rest API");
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(0);
        response.setResponseMsg("Rest API works!!!");

        return response;
    }
}
