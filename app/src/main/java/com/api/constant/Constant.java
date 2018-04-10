package com.api.constant;

public class Constant {
    public enum ResponseStatus { OK, FAIL }

    public static String PYTHON_CMD = "/usr/bin/python3";
    public static String SCRIPTS_PATH = "/home/ubuntu/CMPE295/engine/scripts/";
    public static String IMAGE_PATH = "/home/ubuntu/CMPE295/engine/uploads/";

    public static String IMAGE_FILE_FLAG = "--image_file";
    public static String TOP_K_FLAG = "--top_k";
    public static String VECTOR_PATH_PLAG = "--vector_path";

    //public static String PYTHON_CMD = "C:\\Users\\sjsu\\Desktop\\CMPE295\\engine\\scripts\\python\\python.exe";
    //public static String SCRIPTS_PATH = "C:\\Users\\sjsu\\Desktop\\CMPE295\\engine\\scripts";
    //public static String IMAGE_PATH = "C:\\Users\\sjsu\\Desktop\\CMPE295\\engine\\uploads";

    // Error message
    public static String UPLOAD_FAILED = "Failed to upload the image";

    public static String USER_PASSWORD = "password";
    public static String USER_EMAIL ="email";
    public static String PRODUCT_ID = "product";

    // Table
    public static String USER_TABLE = "user";
    public static String PRODUCT_TABLE = "product";
    public static String USER_HISTORY_TABLE = "user_history";

    public static String S3_URL = "https://s3-us-west-2.amazonaws.com/app-product-image/";
    public static String IMAGE_URL = "https://dln7rdaxtcw3b.cloudfront.net/";

    public static int NUMBER_PRODUCTS_RECOMMENDED = 5;
}
