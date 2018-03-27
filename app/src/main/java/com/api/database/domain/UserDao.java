package com.api.database.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.apache.catalina.User;


@DynamoDBTable(tableName = "user")
public class UserDao {
    private String id;
    private String username;
    private String email;
    private String password;

    @DynamoDBHashKey(attributeName = "Id")
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "Email")
    public String getEmail() {
        return email;
    }

    @DynamoDBAttribute(attributeName = "Password")
    public String getPassword() {
        return password;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

