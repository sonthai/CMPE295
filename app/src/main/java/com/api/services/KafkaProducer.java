package com.api.services;

import com.api.database.domain.ClassifierDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    @Autowired
    KafkaTemplate<String, ClassifierDao> kafkaTemplate;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    public void send(ClassifierDao data) {
        log.info("sending data=" + data);
        kafkaTemplate.send(kafkaTopic, data);

    }
}
