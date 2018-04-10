package com.api.services;

import com.api.model.ResponseMessage;
import com.api.model.UserRequest;

import java.util.List;
import java.util.Map;

public interface IRecommendationService {
    public List<Map<String, Object>> processRecommendation(UserRequest userRequest);
    public List<Map<String, Object>> recommend(Map<String, Object> requestBody);
}
