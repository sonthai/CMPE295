package com.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.api.database.domain.BrandDao;
import com.api.database.domain.ProductDao;
import com.api.database.domain.UserDao;
import com.api.database.domain.UserHistoryDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DynamoDBConfig {
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();
        preloadDatabaseTable(amazonDynamoDB);
        return amazonDynamoDB;
    }

    public void preloadDatabaseTable(AmazonDynamoDB amazonDynamoDB) {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        List<CreateTableRequest> requests = new ArrayList<>();
        CreateTableRequest userTable = dynamoDBMapper
                .generateCreateTableRequest(UserDao.class);
        userTable.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        CreateTableRequest productTable = dynamoDBMapper
                .generateCreateTableRequest(ProductDao.class);
        productTable.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        CreateTableRequest userHistoryTable = dynamoDBMapper
                .generateCreateTableRequest(UserHistoryDao.class);
        userHistoryTable.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        CreateTableRequest brandTable = dynamoDBMapper
                .generateCreateTableRequest(BrandDao.class);
        brandTable.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        requests.add(userTable);
        requests.add(productTable);
        requests.add(userHistoryTable);
        requests.add(brandTable);

        requests.forEach(table -> TableUtils.createTableIfNotExists(amazonDynamoDB, table));
    }

}
