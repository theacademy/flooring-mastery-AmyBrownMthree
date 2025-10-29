package com.sg.flooringmastery.controller;

// Import necessary classes and interfaces
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.service.ServiceLayerImpl;
import com.sg.flooringmastery.service.exceptions.NoSuchOrderException;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import com.sg.flooringmastery.view.FlooringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FlooringController acts as the controller for the Flooring Mastery application.
 * It handles user interactions, commands to the service layer, and controls program flow. "The contractor".
 */
@Component
public class FlooringController {

    // Dependencies injected via constructor
    private final FlooringView view;
    private final ServiceLayerImpl service;
    private final TaxDao taxDao;
    private final ProductDao productDao;

    /**
     * Constructor - initializes controller with necessary components.
     */
    @Autowired
    public FlooringController(FlooringView view, ServiceLayerImpl service, TaxDao taxDao, ProductDao productDao) {
        this.view = view;
        this.service = service;
        this.taxDao = taxDao;
        this.productDao = productDao;
    }

    /**
     * Main method that runs the program loop.
     * Displays the menu and handles user selections until exit.
     */
    public void run() throws IOException, PersistenceException, NoSuchOrderException {
        boolean keepGoing = true;

        view.displayMessage("== WELCOME ==\n");

        while (keepGoing) {
            int choice = view.printMenuAndAnswer(); // Display menu and get user input

            switch (choice) {
                case 1:
                    displayOrders();
                    break;
                case 2:
                    addOrder();
                    break;
                case 3:
                    editOrder();
                    break;
                case 4:
                    removeOrder();
                    break;
                case 5:
                    exportData();
                    break;
                case 6:
                    keepGoing = false; // Exit loop
                    break;
                default:
                    unknownCommand(); // Handle invalid input
            }
        }
        exitMessage();
    }


    // Placeholder for export functionality.
    private void exportData() {
        System.out.println("not yet implemented");
    }

    //Removes an existing order after user confirmation from main menu.
    private void removeOrder() {
        view.displayRemoveOrderBanner();

        try {
            LocalDate orderDate = view.getOrderDate(); // Get date of previous order from user.
            int orderNumber = view.getCustomerNumber(orderDate, service); // Get order number from user.

            Orders foundOrder = service.getOrderByDateAndNumber(orderDate, orderNumber);
            view.displayOrderSummary(foundOrder); // Display order details when found to the user.

            //The following if-else statement ensures back with the user before deleting.
            String confirm = view.getConfirmation("Are you sure you want to remove this order? (Y/N) ");
            if (!confirm.equalsIgnoreCase("Y")) {
                view.displayErrorMessage("Order removal cancelled.");
                return;
            } else {
                service.removeOrder(orderDate, orderNumber);
                view.displayMessage("Order successfully removed!");
            }
        } catch (NoSuchOrderException | PersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }

        view.backToMainMenu();
    }

    //Edits an existing order’s details.
    private void editOrder() throws PersistenceException, NoSuchOrderException, FileNotFoundException {
        view.displayEditOrderBanner();

        LocalDate orderDate = view.getOrderDate();
        int orderNumber = view.getCustomerNumber(orderDate, service);

        Orders foundOrder = service.getOrderByDateAndNumber(orderDate, orderNumber);
        view.displayOrderSummary(foundOrder);

        // Collect changed information from user, keeping defaults if not changed (pressed enter).
        String newName = view.getEditedCustomerName(foundOrder.getCustomerName());
        String newState = view.getEditedState(foundOrder.getState(), service);
        String newProduct = view.getEditedProductType(foundOrder.getProductType(), service);
        BigDecimal newArea = view.getEditedArea(foundOrder.getArea());

        // Recalculate the order with the new information
        Orders newOrder = service.calculateOrder(orderDate, newName, newState, newProduct, newArea);
        newOrder.setOrderNumber(foundOrder.getOrderNumber());

        // Show user the updated order entry.
        view.displayOrderSummary(newOrder);

        // Confirm before saving changes
        String confirm = view.getConfirmation("Save changes? (Y/N) ");
        if (!confirm.equalsIgnoreCase("Y")) {
            view.displayErrorMessage("Edit cancelled. No changes were made.");
            return;
        } else {
            service.editOrder(orderDate, newOrder.getOrderNumber(), newOrder);
            view.displayMessage("Order successfully updated!");
            view.displayOrderSummary(newOrder);
        }
        view.backToMainMenu();
    }

    //Adds a new order based on user input.
    private void addOrder() throws IOException, PersistenceException {
        view.displayAddOrderBanner();

        // Gather necessary information from user and validate at the same time.
        LocalDate orderDate = view.getValidOrderDateInput("Please enter the date of order ");
        String customerName = view.getValidCustomerNameInput("Please enter your name ");
        String state = view.getValidStateInput("Please enter your State ", taxDao);

        // Show available products before asking for product type
        view.displayProducts(service.getProducts());

        String productType = view.getValidProductTypeInput("Please enter the product type you would like ", productDao);
        BigDecimal area = view.getValidAreaInput("please enter the area of product you would like ");

        // Calculate full new order details.
        Orders newOrder = service.calculateOrder(orderDate, customerName, state, productType, area);
        view.displayOrderSummary(newOrder);

        String confirm = view.getConfirmation("Would you like to place this order? (Y/N) ");

        if (confirm.equalsIgnoreCase("Y")) {
            service.addOrderFile(orderDate, newOrder);
            view.displayMessage("Order successfully added!");
        } else {
            view.displayErrorMessage("Order cancelled.");
        }

        view.backToMainMenu();
    }


    //Displays all orders for a specific date.
    private void displayOrders() {
        LocalDate date = view.getOrderDate();
        List<Orders> orders = service.getOrdersByDate(date); // Reads from text file within the working directory.

        // Ensures the file exists, as this could be a common occurence within the application, due to its nature.
        if (orders.isEmpty()) {
            view.displayErrorMessage("No Orders found for " + date);
        } else {
            view.displayOrders(orders);
        }
    }


    // Displays exit message when the user quits the program.
    private void exitMessage() {
        view.displayExitBanner();
    }

    // Handles unknown or invalid menu commands within the switch case.
    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }
}
