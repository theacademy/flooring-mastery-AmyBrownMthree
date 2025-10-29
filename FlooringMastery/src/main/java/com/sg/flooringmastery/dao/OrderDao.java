package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.service.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.List;

/**
 * This interface allows for communication between the Order Dao and the ServiceLayer.
 * Lists the actions that the service layer is allowed to do with relation to the Order Files.
 */

public interface OrderDao {

    void addOrder(LocalDate date, Orders order) throws PersistenceException;

    void editOrder(LocalDate date, int orderNumber, Orders updatedOrder) throws PersistenceException;

    void removeOrder(LocalDate date, int orderNumber) throws PersistenceException;

    List<Orders> getOrdersByDate(LocalDate date) throws PersistenceException;

    int generateNextOrderNumber(LocalDate date) throws PersistenceException;

    void writeFile() throws PersistenceException;
}
