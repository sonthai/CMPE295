package com.api.services;

import com.api.model.ResponseMessage;
import com.api.model.UserRequest;

import java.util.List;
import java.util.Map;

public interface IRecommendationService {
    List<Map<String, Object>> processRecommendation(UserRequest userRequest);
    List<Map<String, Object>> recommend(Map<String, Object> requestBody);
}
