package com.ymt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Hello world!
 *
 */
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com.ymt"})
public class Application {
    public static void main( String[] args ){
    	 SpringApplication.run(Application.class, args);
    }
}
