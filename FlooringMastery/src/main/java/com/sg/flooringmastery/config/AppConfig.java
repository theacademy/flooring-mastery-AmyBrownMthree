package com.sg.flooringmastery.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for component scanning.
 * This tells Spring where to look for @Component, @Service, @Repository, and @Controller beans.
 */
@Configuration
@ComponentScan(basePackages = "com.sg.flooringmastery")
public class AppConfig {

}

