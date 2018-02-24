package com;

import com.api.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.UUID;

@ImportResource("classpath*:/appContext.xml")
@SpringBootApplication
public class Application {
    @Autowired
    //UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

/*
    @Override
    public void run(String... strings) throws Exception {
        clearData();
        //saveData();
        //lookup();

    }

    public void clearData() {
        userRepository.deleteAll();
    }

    public void saveData() {
        System.out.println("Save User data");
        UserTable u1 = new UserTable(UUID.randomUUID(), "sdthai", "sdthai@gmail.com", "123456");
        UserTable u2 = new UserTable(UUID.randomUUID(), "testuser1", "testuser1@gmail.com", "abcgdf");
        UserTable u3 = new UserTable(UUID.randomUUID(), "testuser2", "testuser2@gmail.com", "ajhfhg");

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
    }

    public void lookup() {
        Iterable<UserTable> userList = userRepository.findAll();
    }*/
}
