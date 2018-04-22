package com.quangpham.drs;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by quangpham on 3/31/18.
 */

public final class AppConstant {

    //for REST API CALLS
    public final static String URL_BASE_REST_API = "http://34.216.224.185:8080/app-1";
    public final static String URL_REST_API_LOGIN = "/user/login";
    public final static String URL_REST_API_REGISTER = "/user/register";
    public final static String URL_REST_API_UPDATE_PROFILE = "/user/update_password";
    public final static String URL_REST_API_PROCESS_IMAGE = "/customer/processData";
    public final static String URL_REST_API_UPLOAD_IMAGE = "/customer/upload";
    public final static String URL_REST_API_PROCESS_RECOMMENDATION = "/customer/recommend";
    public final static String URL_CLOUD_FRONT_IMAGE = "https://dln7rdaxtcw3b.cloudfront.net/";
    public final static int NUMBER_IMAGES_HOME_TAB = 20;
    public final static int NUMBER_IMAGES_SEARCH_TAB = 20;
    public final static String CAMERA_FILE_NAME = "pxq";
    public final static String IMAGE_DIR = "MyImages";

    //for Map Intent Service
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.quangpham.drs";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY1 = PACKAGE_NAME + ".RESULT_DATA_KEY1";
    public static final String RESULT_DATA_KEY2 = PACKAGE_NAME + ".RESULT_DATA_KEY2";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    //stores supported info
    public static final String STORE_HOUR_1[] = {"9AM - 7PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 9PM", "9AM - 7PM"};
    public static final StoreInfo STORES[] = {
            new StoreInfo("Westfield Valley Fair Mall", new LatLng(37.3257904, -121.9457041), STORE_HOUR_1),
            new StoreInfo("Westfield Oakridge Mall", new LatLng(37.2520341, -121.8631498), STORE_HOUR_1),
            new StoreInfo("Great Mall Outlet", new LatLng(37.415738, -121.897412), STORE_HOUR_1),
    };
}
