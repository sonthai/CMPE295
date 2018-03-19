package com.api.services;

import com.api.model.ResponseMessage;
import com.api.model.UserRequest;

import java.util.Map;

public interface IRecommendationService {
    public ResponseMessage processRecommendation(UserRequest userRequest);
    public ResponseMessage recommend(String ids);
}
