package org.my_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Hello world!
 *
 */
@EnableAutoConfiguration
public class Application {
    public static void main( String[] args ){
    	 SpringApplication.run(Application.class, args);
    }
}
