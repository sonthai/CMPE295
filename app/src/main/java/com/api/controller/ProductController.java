package com.api.controller;

import com.api.constant.Constant;
import com.api.model.ResponseMessage;
import com.api.services.ProductService;
import com.api.services.UserHistoryService;
import com.api.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @Autowired
    UserHistoryService userHistoryService;

    // Load data from csv to Dynamodb
    @RequestMapping(method = RequestMethod.POST, value = "/load_products")
    public ResponseMessage uploadProductsToDB(@RequestBody Map<String, String> bodyRequest) {
        log.info("Test load product API");
        String filePath = bodyRequest.get("file_name"); //"C:\\Users\\sjsu\\Downloads\\product.csv";
        String msg;
        if (filePath.contains("product.csv")) {
            int totalAddedProduct = productService.addedProducts(filePath);
            msg = "Total added products: " + String.valueOf(totalAddedProduct);
        } else{
            msg = "Unsupported file " + filePath;
        }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg(msg);
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;
    }


    @RequestMapping(method = RequestMethod.POST, value="/info", consumes = "application/json", produces = "application/json")
    public ResponseMessage getProductByName(@RequestBody Map<String, String> productMap) {
        String productName = productMap.get("name");
        log.info("Get product information for {}", productName);

        List<Map<String, Object>> result = productService.getProduct(productName);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Product information");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.POST, value="/list", consumes = "application/json", produces = "application/json")
    public ResponseMessage getProductList(@RequestBody Map<String, String> productMap) {
        String productName = productMap.get("name");
        log.info("Get product list information for {}", productName);

        List<Map<String, Object>> result = productService.getProducts(Arrays.asList(productName.split(",")));

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Product list information");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.POST, value="/userHistory", consumes = "application/json", produces = "application/json")
    public ResponseMessage getUserHistory(@RequestBody Map<String, String> productMap) {
        String email = productMap.get("email");
        List<Map<String, Object>> result = userHistoryService.findProductByUserEmail(email);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Recommendation History");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        responseMessage.setData(result);
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.POST, value="/getRecommendedProducts", consumes = "application/json", produces = "application/json")
    public ResponseMessage readProductsFromJson(@RequestBody Map<String, String> bodyRequest) {
        String file =  bodyRequest.get("file");
        List<String> products = Utils.getSimilarProducts(file);
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResponseMsg("Recommended product in json file");
        responseMessage.setResponseCode(Constant.ResponseStatus.OK);
        return responseMessage;

    }




}
