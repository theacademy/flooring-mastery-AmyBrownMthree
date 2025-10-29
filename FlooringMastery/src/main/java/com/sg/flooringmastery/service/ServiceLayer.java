package com.sg.flooringmastery.service;

import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.model.Products;
import com.sg.flooringmastery.service.exceptions.NoSuchOrderException;
import com.sg.flooringmastery.service.exceptions.PersistenceException;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This file is the interface where the controller can speak to the Business Logic of the application to recieve required data.
 * It holds some key functionality definitions.
 */

public interface ServiceLayer {

    Orders getOrderByDateAndNumber(LocalDate date, int orderNumber) throws PersistenceException, NoSuchOrderException;

    void editOrder(LocalDate orderDate, int orderNumber, Orders order) throws PersistenceException, NoSuchOrderException;

    void removeOrder(LocalDate orderDate, int orderNumber) throws PersistenceException, NoSuchOrderException;

    List<Products> getProducts() throws PersistenceException;

    Orders calculateOrder(LocalDate orderDate, String customerName, String state, String productType, BigDecimal area)
            throws PersistenceException, FileNotFoundException;

    boolean isValidState(String input) throws FileNotFoundException, PersistenceException;

    boolean isValidProduct(String input) throws FileNotFoundException, PersistenceException;
}
