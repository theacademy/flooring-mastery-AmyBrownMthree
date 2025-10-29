package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.model.Products;
import com.sg.flooringmastery.model.Taxes;
import com.sg.flooringmastery.service.exceptions.NoSuchOrderException;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * This file handles all connections to Dao and calculation on behalf of the controller.
 * It does not handle any Console I/O however it does deal with File I/O.
 */

@Service
public class ServiceLayerImpl implements ServiceLayer {

    //the classes access to the Data Access Objects where all data will be recieved.
    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;

    //object constructor.
    @Autowired
    public ServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }

    // -------------------- ORDER MANAGEMENT --------------------

    /**
     *Sequentially looks for a filename match with the date given. Then within that file iterates through to see if there
     * is a match for the given orderNumber. This is used by both the editOrder() and removeOrder(). DRY.
     */
    @Override
    public Orders getOrderByDateAndNumber(LocalDate date, int orderNumber) throws PersistenceException, NoSuchOrderException {
        List<Orders> orders = getOrdersByDate(date);

        if (orders == null || orders.isEmpty()) {
            throw new NoSuchOrderException("No orders found for date: " + date);
        }

        for (Orders o : orders) {
            if (o.getOrderNumber() == orderNumber) {
                return o;
            }
        }

        throw new NoSuchOrderException("No order with number " + orderNumber + " found for date: " + date);
    }

    /**
     * Updates an existing order while keeping its original order number.
     */
    @Override
    public void editOrder(LocalDate orderDate, int orderNumber, Orders updatedOrder)
            throws PersistenceException {

        updatedOrder.setOrderNumber(orderNumber); // preserve number
        orderDao.editOrder(orderDate, orderNumber, updatedOrder);
    }

    /**
     * Removes an existing Order from File Storage.
     */
    @Override
    public void removeOrder(LocalDate orderDate, int orderNumber)
            throws PersistenceException, NoSuchOrderException {
        orderDao.removeOrder(orderDate, orderNumber);
    }

    /**
     *Adds a new Order into a File for a given date.
     */
    public void addOrderFile(LocalDate orderDate, Orders newOrder) {
        try {
            orderDao.addOrder(orderDate, newOrder);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------- HELPERS --------------------

    // Returns all Products from the 'Products.txt' file, into List Objects.
    @Override
    public List<Products> getProducts() throws PersistenceException {
        try {
            return productDao.getAllProducts();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Checks whether the given American State exists within the 'Taxes.txt' File.
    public boolean isValidState(String state) throws FileNotFoundException, PersistenceException {
        Taxes tax = taxDao.getTaxByState(state);
        return tax != null;
    }

    // Checks whether the given product exists within the 'Products.txt' file.
    public boolean isValidProduct(String productName) throws FileNotFoundException, PersistenceException{
        Products product = productDao.getProductByType(productName);
        return product != null;
    }

    // Retrieves all Orders associated with a specific date.
    public List<Orders> getOrdersByDate(LocalDate date) {
        try {
            return orderDao.getOrdersByDate(date);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------- ORDER CALCULATION --------------------

    //In future version this should become its own class.
    @Override
    public Orders calculateOrder(LocalDate orderDate, String customerName, String state,
                                 String productType, BigDecimal area)
            throws PersistenceException, FileNotFoundException {

        //recieve data from Tax and Product, thee are needed for the following calculations.
        Taxes tax = taxDao.getTaxByState(state);
        Products product = productDao.getProductByType(productType);

        // Perform calculations with rounding
        BigDecimal materialCost = area.multiply(product.getCostPerSquareFoot()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal laborCost = area.multiply(product.getLabourCostPerSquareFoot()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = materialCost.add(laborCost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxRateDecimal = tax.getTaxRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal taxTotal = subtotal.multiply(taxRateDecimal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxTotal).setScale(2, RoundingMode.HALF_UP);

        //Now the program has all the data needed to make a full Order.
        Orders order = new Orders();
        order.setOrderNumber(orderDao.generateNextOrderNumber(orderDate));
        order.setCustomerName(customerName);
        order.setState(state);
        order.setTaxRate(tax.getTaxRate());
        order.setProductType(productType);
        order.setArea(area);
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLabourCostPerSquareFoot());
        order.setMaterialCost(materialCost);
        order.setLaborCost(laborCost);
        order.setTax(taxTotal);
        order.setTotal(total);

        return order;
    }
}
