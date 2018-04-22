package com.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@CrossOrigin(origins = "http://recommedation-ui.s3-website-us-west-2.amazonaws.com", maxAge = 3600)
@ComponentScan("com.api")
@EnableWebMvc
@PropertySource("classpath:/application.properties")
public class AppConfig {}
