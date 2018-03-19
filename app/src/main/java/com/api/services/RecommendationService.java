package com.api.services;

import com.api.constant.Constant;
import com.api.database.transaction.UserHistoryTransaction;
import com.api.model.ResponseMessage;
import com.api.model.UserRequest;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service
public class RecommendationService implements IRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    UserHistoryTransaction userHistoryTransaction;

    @Override
    public ResponseMessage processRecommendation(UserRequest userRequest) {
        String imageName = Utils.executeScript("classify_images", "image_file", new String[] {userRequest.getImagePath()});

        if (!StringUtils.isEmpty(userRequest.getUserId())) {
            userHistoryTransaction.save(userRequest);
        }

        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg(imageName);
        return response;
    }

    @Override
    public ResponseMessage recommend(String ids) {
        // To Do Retrieve message based on the id from Message store

        return null;
    }
}
