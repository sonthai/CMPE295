package com.quangpham.drs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by quangpham on 4/4/18.
 */

public class ProductInfo {

    private String productName;
    private String productImageName;
    private String productPrice;
    private Bitmap productImage;
    private boolean favorite;
    private String email;

    public ProductInfo() {
    }

    public ProductInfo(String productName, String productImageName, String productPrice, Bitmap productImage, String email) {
        this.productName = productName;
        this.productImageName = productImageName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.favorite = false;
        this.email = email;
    }

    //this is for loading for favorite list from DB
    public ProductInfo(String productName, String productImageName, String productPrice, String base64String, String email) {
        this.productName = productName;
        this.productImageName = productImageName;
        this.productPrice = productPrice;
        this.productImage = covertBase64ToBitmap(base64String);
        this.favorite = true;
        this.email = email;
    }


    public Bitmap getProductImage() {
        return productImage;
    }

    public void setProductImage(Bitmap productImage) {
        this.productImage = productImage;
    }


    public ProductInfo(String productName, String productImageName, String productPrice) {
        this.productName = productName;
        this.productImageName = productImageName;
        this.productPrice = productPrice;
    }

    public String getBitmapBase64() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        productImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return encoded;
    }

    public static String getBitmapBase64(Bitmap productImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        productImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return encoded;
    }

    public static Bitmap covertBase64ToBitmap(String base64String) {
        byte[] decodedByte = Base64.decode(base64String,  Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void toggleFavorite() {
        favorite = !favorite;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageName() {
        return productImageName;
    }

    public void setProductImageName(String productImageName) {
        this.productImageName = productImageName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "product_info";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IMAGE_NAME = "image_name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_IMAGE_BASE64 = "image_base64";
        public static final String COLUMN_NAME_USER_EMAIL = "user_email";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_IMAGE_NAME + " TEXT PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_PRICE + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE_BASE64 + " TEXT," +
                    FeedEntry.COLUMN_NAME_USER_EMAIL + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

}
