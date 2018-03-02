package com.api.services;

import com.api.model.ResponseMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecommendationService implements IRecommendationService {
    @Override
    public ResponseMessage recommend(Map<String, String> bodyRequest) {
        return null;
    }
}
