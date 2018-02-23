package com.api.services;

import com.api.database.domain.UserTable;
import com.api.database.repository.UserRepository;
import com.api.model.ResponseMessage;
import com.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class UserService implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public ResponseMessage register(Map<String, String> data) {
        log.info("Registration service is called");
        ObjectMapper mapper = new ObjectMapper();
        User u = mapper.convertValue(data, User.class);
        UserTable userRow = new UserTable(UUID.randomUUID(), u.getUsername(), u.getEmail(), u.getPassword());
        userRepository.save(userRow);
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(0);
        response.setResponseMsg("Register successfully: " + data);

        return response;
    }

    @Override
    public ResponseMessage logIn(Map<String, String> data) {
        log.info("Login service is called");
        ObjectMapper mapper = new ObjectMapper();
        User u = mapper.convertValue(data, User.class);
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(0);
        response.setResponseMsg("Login successfully: " + data);

        return response;
    }
}
