package com.sg.flooringmastery;

import com.sg.flooringmastery.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sg.flooringmastery.controller.FlooringController;
import com.sg.flooringmastery.dao.*;
import com.sg.flooringmastery.service.exceptions.NoSuchOrderException;
import com.sg.flooringmastery.service.exceptions.PersistenceException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        FlooringController controller = ctx.getBean(FlooringController.class);

        try {
            controller.run();
        } catch (IOException | PersistenceException | NoSuchOrderException e) {
            throw new RuntimeException(e);
        }
    }
}