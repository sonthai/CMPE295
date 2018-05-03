package com.quangpham.drs.utils;

import com.google.android.gms.maps.model.LatLng;
import com.quangpham.drs.dto.StoreInfo;

/**
 * Created by quangpham on 3/31/18.
 */

public final class AppConstant {

    //for REST API CALLS
    public final static String URL_BASE_REST_API = "http://54.213.163.194:8080/app-1";
    public final static String URL_REST_API_LOGIN = "/user/login";
    public final static String URL_REST_API_REGISTER = "/user/register";
    public final static String URL_REST_API_UPDATE_PASSWORD = "/user/update_password";
    public final static String URL_REST_API_UPDATE_PROFILE = "/user/update_profile";
    public final static String URL_REST_API_PROCESS_IMAGE = "/customer/processData";
    public final static String URL_REST_API_PROCESS_RECOMMENDATION = "/customer/recommend";
    public final static String URL_CLOUD_FRONT_IMAGE = "https://dln7rdaxtcw3b.cloudfront.net/";
    public final static int NUMBER_IMAGES_HOME_TAB = 20;
    public final static int NUMBER_IMAGES_SEARCH_TAB = 20;
    public final static String IMAGE_DIR = "MyImages";

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    //promotion
    public static final double PROMO_RATE = 0.8;

    //stores supported info
    public static final String STORE_HOUR_1[] = {"9AM - 7PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 7PM"};
    public static final StoreInfo STORES[] = {
            new StoreInfo("Westfield Valley Fair Mall", new LatLng(37.3257904, -121.9457041), STORE_HOUR_1),
            new StoreInfo("Westfield Oakridge Mall", new LatLng(37.2520341, -121.8631498), STORE_HOUR_1),
            new StoreInfo("Great Mall Outlet", new LatLng(37.415738, -121.897412), STORE_HOUR_1),
    };
}
