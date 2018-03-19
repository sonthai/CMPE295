package com.api.services;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class RecommendationService implements IRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    @Override
    public ResponseMessage processInCommingData(Map<String, String> bodyRequest) {
        String output = Utils.executeScript("classify_images", "image_file", new String[] {""});
        ResponseMessage response = new ResponseMessage();
        response.setResponseCode(Constant.ResponseStatus.OK);
        response.setResponseMsg(output);
        return response;
    }

    @Override
    public ResponseMessage recommend(String ids) {
        // To Do Retrieve message based on the id from Message store

        return null;
    }
}
