package com.api.controller;

import com.api.constant.Constant;
import com.api.database.repository.UserRepository;
import com.api.model.ResponseMessage;
import com.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST, value="/register", consumes = "application/json", produces = "application/json")
    public ResponseMessage register(@RequestBody Map<String, String> bodyRequest) {
        log.info("Register API");

        ResponseMessage responseMessage =  userService.register(bodyRequest);
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseMessage logIn(@RequestBody Map<String, String> bodyRequest) {
        log.info("Log In API");
        return userService.logIn(bodyRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/test", produces = "application/json")
    public ResponseMessage testApi() {
        log.info("Test Rest API");
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg("Rest API works!!!");

        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user", produces = "application/json")
    public ResponseMessage getUser() {
        log.info("Get user");
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg("Hello");

        return response;
    }
}
