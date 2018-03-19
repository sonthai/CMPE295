package com.api.services;

import com.api.model.ResponseMessage;

import java.util.Map;

public interface IRecommendationService {
    public ResponseMessage processInCommingData(Map<String, String> bodyRequest);
    public ResponseMessage recommend(String ids);
}
