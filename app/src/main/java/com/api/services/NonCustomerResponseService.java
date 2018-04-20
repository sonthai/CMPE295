package com.api.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NonCustomerResponseService {
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

    public void addImages(String image) {imageList.add(image);}

    public List<String> getImages(int limit) {
        List<String> requests;
        if (limit < imageList.size()) {
            requests = imageList.subList(0, limit);
            imageList = imageList.subList(limit, imageList.size());
        } else {
            requests =  imageList.subList(0, limit);
            imageList = imageList.subList(limit, imageList.size());
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
