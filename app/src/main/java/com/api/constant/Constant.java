package com.api.constant;

public class Constant {
    public enum ResponseStatus { OK, FAIL }

    public static String PYTHON_CMD = "python3";
    public static String SCRIPTS_PATH = "/home/ubuntu/CMPE295A/engine/scripts/";
    public static String IMAGE_PATH = "/home/ubuntu/CMPE295A/engine/uploads/";

    // Error message
    public static String UPLOAD_FAILED = "Failed to upload the image";

    public static String USER_PASSWORD = "password";
    public static String USER_EMAIL ="email";

    // Table
    public static String USER_TABLE = "user";
    public static String PRODUCT_TABLE = "product";
    public static String USER_HISTORY_TABLE = "user_history";

    public static String S3_URL = "https://s3-us-west-2.amazonaws.com/app-product-image/";
    public static String IMAGE_URL = "https://dln7rdaxtcw3b.cloudfront.net/";
}
