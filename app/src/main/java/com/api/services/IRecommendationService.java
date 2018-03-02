package com.api.services;

import com.api.model.ResponseMessage;

import java.util.Map;

public interface IRecommendationService {
    public ResponseMessage recommend(Map<String, String> bodyRequest);
}
