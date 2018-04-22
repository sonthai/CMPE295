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
    //Map<UUID, Integer> userIdMapProductId;
    List<String> imageList;

    public NonCustomerResponseService () { imageList = new ArrayList<>();
    }

    public static NonCustomerResponseService getMessageStoreInstance() {
        if (nonCustomerResponseService == null) {
            nonCustomerResponseService = new NonCustomerResponseService();
        }

        return nonCustomerResponseService;
    }

    public void addImages(String image) {
        imageList.add(image);
        log.info("Available products for NonCustomerMember {}", imageList.size());
    }

    public List<String> getImages(int limit) {
        List<String> requests = new ArrayList<>();
        if (imageList.size() > 0) {
            if (limit >= imageList.size()) {
                requests = imageList.subList(0, imageList.size());
                imageList.clear();
            } else {
                requests = imageList.subList(0, limit);
                imageList = imageList.subList(limit, imageList.size());
            }

            log.info("Retrieved: {}, available products left: {}", requests.size(), imageList.size());
        } else {
            log.info("No recommended products are available");
        }

        return requests;
    }

    /*public void addUserId(UUID userId, int productId) {
        userIdMapProductId.put(userId, productId);
    }*/

   /* public String getUserIds() {
        String users = "";
        if (userIds.size() > 0) {
            users = userIds.stream().map(i -> i.toString()).collect(Collectors.joining(","));
            userIds.clear();
        }
        return users;
    }*/

  /* public String getProductIds() {
       String productIds = "";
       if (userIdMapProductId.size() > 0) {
           productIds = userIdMapProductId.values().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(","));
           userIdMapProductId.clear();

       }
       return productIds;
   }

   public int getProductByUserId(UUID userUuid) {
       return userIdMapProductId.get(userUuid);
   }*/
}
