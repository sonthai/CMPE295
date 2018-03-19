package com.api.services;

import com.api.database.domain.ClassifierDao;
import com.api.model.UserAppearance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataProcessingService {
    private static final Logger log = LoggerFactory.getLogger(DataProcessingService.class);

    @Autowired
    KafkaProducerService producer;

    @Autowired
    RecommendationService recommendationService;

    public void processData(Map<String, String> data) {
        ObjectMapper mapper = new ObjectMapper();
        UserAppearance appearance = mapper.convertValue(data, UserAppearance.class);

        producer.send(convertUserAppearanceData(appearance));

    }

    public void getRecommendation(Map<String, String> data) {
        recommendationService.recommend(data.get("ids"));
    }

    private ClassifierDao convertUserAppearanceData(UserAppearance ua) {
        ClassifierDao dao = new ClassifierDao();
        dao.setUpper(ua.getUpper());
        dao.setLower(ua.getLower());
        dao.setBrand(ua.getBrand());
        dao.setStyle(ua.getStyle());
        dao.setColor(ua.getColor());
        dao.setGender(ua.getGender());
        return dao;
    }
}
