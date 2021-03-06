package com.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NonCustomerResponseService {
    private static final Logger log = LoggerFactory.getLogger(NonCustomerResponseService.class);

    private static NonCustomerResponseService nonCustomerResponseService;
    Queue<String> imageList;
    private boolean isImageRetrieved = false;

    public NonCustomerResponseService () { imageList = new LinkedList<>();
    }

    public static NonCustomerResponseService getMessageStoreInstance() {
        if (nonCustomerResponseService == null) {
            nonCustomerResponseService = new NonCustomerResponseService();
        }

        return nonCustomerResponseService;
    }

    public boolean hasImage() {
        return isImageRetrieved;
    }


    public void addImages(String image) {
        imageList.offer(image);
        log.info("Available products for NonCustomerMember {}", imageList.size());
    }


    public List<String> getImages(int limit) {
        List<String> result = new ArrayList<>();
        isImageRetrieved = false;
        int lower = Math.min(limit, imageList.size());
        for (int i = 1; i <= lower; i++) {
            result.add(imageList.poll());
        }

        log.info("Retrieved: {}, available products left: {}", result.size(), imageList.size());

        if (result.size() > 0) {
            isImageRetrieved = true;
        }
        return result;
    }
}
