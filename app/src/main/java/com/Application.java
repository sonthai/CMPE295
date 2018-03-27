package com;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.api.database.domain.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


@ImportResource("classpath*:/appContext.xml")
@SpringBootApplication
public class Application  {
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

/*
    @Override
    public void run(String... strings) throws Exception {
        //clearData();
        //saveData();
        //lookup();
        //createUserTable();
        //testDynamoDB();

    }*/

    private void createUserTable() {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        //amazonDynamoDB.deleteTable("user");

       CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(UserDao.class);
        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));
        amazonDynamoDB.createTable(tableRequest);

        //dynamoDBMapper.batchDelete((List<User>) userRepository.findAll());
    }

    private void testDynamoDB() {
        UserDao user = new UserDao(); //("sdthai", "son.thai@sjsu.edu", "1234567");
        user.setEmail("sdthai@sjsu.edu");
        user.setPassword("123455");
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        mapper.save(user);
        System.out.println("user id " + user.getId());
        UserDao u = mapper.load(UserDao.class, user.getId());
        /*userRepository.save(user);
        user = new UserDao("sdthai2", "thaison242@gmail.com", "1234567");
        userRepository.save(user);
        List<UserDao> ud = userRepository.findByEmail("son.thai@sjsu.edu");
        System.out.println("User name " + ud.get(0).getUsername());

        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);*/
        //Table table = dynamoDB.getTable("user")
        /*Item item = new Item()
                    .withPrimaryKey("id", "1")
                    .with("username", "sdthai")
                    .with("email", "son.thai@sjsu.edu")
                    .with("password", "123455");
        table.putItem(item);*/
        //Item item = table.getItem("username", "sdthai");
        //System.out.println("Id= " + item.get("id"));
        //ItemCollection<QueryOutcome> i = table.query("username", "sdthai");
        //System.out.println(i.getAccumulatedItemCount());
    }

   /* public void clearData() {
        userRepository.deleteAll();
    }*/
}
