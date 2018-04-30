package com.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NonCustomerResponseService {
    private static final Logger log = LoggerFactory.getLogger(NonCustomerResponseService.class);

    private static NonCustomerResponseService nonCustomerResponseService;
    Queue<String> imageList;

    public NonCustomerResponseService () { imageList = new LinkedList<>();
    }

    public static NonCustomerResponseService getMessageStoreInstance() {
        if (nonCustomerResponseService == null) {
            nonCustomerResponseService = new NonCustomerResponseService();
        }

        return nonCustomerResponseService;
    }

    public boolean hasImage() {
        return imageList.isEmpty();
    }


    public void addImages(String image) {
        imageList.offer(image);
        log.info("Available products for NonCustomerMember {}", imageList.size());
    }


    public List<String> getImages(int limit) {
        List<String> result = new ArrayList<>();
        int lower = Math.min(limit, imageList.size());
        for (int i = 1; i <= lower; i++) {
            result.add(imageList.poll());
        }

        log.info("Retrieved: {}, available products left: {}", result.size(), imageList.size());
        /*if (imageList.size() > 0) {
            if (limit >= imageList.size()) {
                requests = imageList.subList(0, imageList.size());
            } else {
                requests = imageList.subList(0, limit);
            }

        } else {
            log.info("No recommended products are available");
        }

        for (Iterator iterator = imageList.iterator(); iterator.hasNext();) {
            String image = iterator.next().toString();
            if (requests.contains(image)) {
                iterator.remove();
            }
        }*/
        return result;
    }
}
