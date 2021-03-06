package com.api.database.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "product")
public class ProductDao {
    private int id;
    private String product_name;
    private String image;
    private int brand;
    private double price;

    @DynamoDBHashKey(attributeName = "Id")
    public int getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "Product")
    public String getProductName() {
        return product_name;
    }

    @DynamoDBAttribute(attributeName = "Image")
    public String getImage() {
        return image;
    }

    @DynamoDBAttribute(attributeName = "Brand")
    public int getBrand() {
        return brand;
    }

    @DynamoDBAttribute(attributeName = "Price")
    public double getPrice() {
        return price;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBrand(int brand) {
        this.brand = brand;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
