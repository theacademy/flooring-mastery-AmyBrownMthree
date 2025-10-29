package com.sg.flooringmastery.view;

import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.model.Products;
import com.sg.flooringmastery.model.Taxes;
import com.sg.flooringmastery.service.ServiceLayer;
import com.sg.flooringmastery.service.exceptions.NoSuchOrderException;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This file handles all userInput and display on behalf of the controller.
 */
@Component
public class FlooringView {

    private final UserIO io;

    //constructor
    @Autowired
    public FlooringView(UserIO io){
        this.io = io;
    }

    //shows user main menu, and recieves their input through the interface.
    public int printMenuAndAnswer(){
        io.print("***************");
        io.print("* <<Flooring Program>> ");
        io.print("* 1. Display Orders ");
        io.print("* 2. Add an Order ");
        io.print("* 3. Edit an Order ");
        io.print("* 4. Remove an Order ");
        io.print("* 5. Export all Data ");
        io.print("* 6. Quit ");
        io.print("***************");

        return io.readInt("Please select an option 1-6 ", 1, 6);
    }

    public LocalDate getOrderDate() {
        LocalDate date = null;
        boolean valid = false;

        while (!valid) {
            String input = io.readString("Please enter a date (YYYY-MM-DD): ");
            try {
                date = LocalDate.parse(input);
                valid = true;
            } catch (Exception e) {
                io.print("Invalid date format. Please try again using YYYY-MM-DD.");
            }
        }

        return date;
    }

    public int getCustomerNumber(LocalDate date, ServiceLayer service) {
        while (true) {
            try {
                String input = io.readString("Please enter the order number connected to your order ").trim();
                if (input.isEmpty()) {
                    io.print("Input cannot be empty. Please enter a valid order number.");
                    continue;
                }
                int orderNumber = Integer.parseInt(input);
                if (orderNumber <= 0) {
                    io.print("Order number must be a positive integer. Please try again.");
                    continue;
                }
                try {
                    service.getOrderByDateAndNumber(date, orderNumber);
                    return orderNumber; // valid
                } catch (NoSuchOrderException e) {
                    io.print("No order found with number " + orderNumber + " for date " + date + ".");
                } catch (PersistenceException e) {
                    io.print("Error reading orders from file. Please try again later.");
                }

            } catch (NumberFormatException e) {
                io.print("Invalid input. Please enter a valid whole number (e.g., 123).");
            } catch (Exception e) {
                io.print("An unexpected error occurred. Please try again.");
            }
        }
    }

    public String getConfirmation(String prompt) {
        return io.readString(prompt);
    }

    public void backToMainMenu() {
        io.readString("Please press enter to return to main menu");
    }

    // -------------------- DISPLAY --------------------

    public void displayOrders(List<Orders> orders){
        io.print("\n=== Orders ===");
        for(Orders o: orders){
            io.print("Order #"+ o.getOrderNumber() +": "+
                    o.getCustomerName()+ " | "+
                    o.getState()+ " | "+
                    o.getProductType()+ " | "+
                    "Total: £"+ o.getTotal());
        }
        backToMainMenu();
    }

    public void displayOrderSummary(Orders newOrder) {
        io.print("\n===== ORDER SUMMARY =====");
        io.print("Order Number: " + newOrder.getOrderNumber());
        io.print("Customer Name: " + newOrder.getCustomerName());
        io.print("State: " + newOrder.getState());
        io.print("Tax Rate: " + newOrder.getTaxRate() + "%");
        io.print("Product Type: " + newOrder.getProductType());
        io.print("Area: " + newOrder.getArea() + " sq ft");
        io.print("Cost per Square Foot: $" + newOrder.getCostPerSquareFoot());
        io.print("Labor Cost per Square Foot: $" + newOrder.getLaborCostPerSquareFoot());
        io.print("Material Cost: $" + newOrder.getMaterialCost());
        io.print("Labor Cost: $" + newOrder.getLaborCost());
        io.print("Tax: $" + newOrder.getTax());
        io.print("-------------------------");
        io.print("Total: $" + newOrder.getTotal());
        io.print("=========================\n");
    }

    public void displayProducts(List<Products> products) {
        io.print("\n=== Available Products ===");
        for (Products p : products) {
            io.print(p.getProductType()
                    + " | Material Cost: $" + p.getCostPerSquareFoot()
                    + " | Labor Cost: $" + p.getLabourCostPerSquareFoot());
        }
        io.print("==========================\n");
    }

    public void displayErrorMessage(String s) {
        io.print(s);
    }

    public void displayMessage(String prompt) {
        io.print(prompt);
    }

    // -------------------- VALIDATE for Order Add Functionality --------------------

    public LocalDate getValidOrderDateInput(String prompt) {
        LocalDate date = null;
        boolean valid = false;

        while (!valid) {
            String input = io.readString(prompt);

            try {
                date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    io.print("Date should be in the future!");
                } else {
                    valid = true;
                }
            } catch (Exception e) {
                io.print("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
        return date;
    }

    public String getValidCustomerNameInput(String prompt) {
        String customerName;

        while (true) {
            customerName = io.readString(prompt);

            // Handle nulls or extra spaces
            if (customerName == null) {
                io.print("Customer name cannot be null.");
                continue;
            }
            customerName = customerName.trim();
            if (customerName.isEmpty()) {
                io.print("Customer name cannot be empty!");
            } else if (!customerName.matches("[A-Za-z0-9., ]+")) {
                io.print("Customer name can only contain letters, numbers, spaces, commas, and periods.");
            } else {
                break; // valid input
            }
            prompt = "Please re-enter your name: ";
        }
        return customerName;
    }

    public String getValidStateInput(String prompt, TaxDao taxDao)
            throws FileNotFoundException, PersistenceException {

        String state;
        Taxes tax;

        while (true) {
            state = io.readString(prompt);

            if (state == null || state.trim().isEmpty()) {
                io.print("State cannot be empty. Please enter a valid state abbreviation (e.g., TX, OH, MI).");
                continue;
            }
            state = state.trim().toUpperCase();
            tax = taxDao.getTaxByState(state);
            if (tax != null) {
                return state; // Valid state found
            } else {
                io.print("We do not sell in '" + state + "'. Please try again.");
            }
        }
    }

    public String getValidProductTypeInput(String prompt, ProductDao productDao)
            throws FileNotFoundException, PersistenceException {

        String product;
        Products foundProduct;

        while (true) {
            product = io.readString(prompt);
            if (product == null || product.trim().isEmpty()) {
                io.print("Product type cannot be empty. Please enter a valid product name (e.g., Tile, Carpet, Wood).");
                continue;
            }
            product = product.trim(); // Clean up spaces
            foundProduct = productDao.getProductByType(product);
            if (foundProduct != null) {
                return product; // Valid product type
            } else {
                io.print("We do not sell '" + product + "'. Please try again.");
            }
        }
    }

    public BigDecimal getValidAreaInput(String prompt) {
        BigDecimal area = null;

        while (true) {
            try {
                String input = io.readString(prompt).trim();
                if (input.isEmpty()) {
                    io.print("Area cannot be empty. Please enter a number.");
                    continue;
                }
                area = new BigDecimal(input);
                if (area.compareTo(new BigDecimal("100")) < 0) {
                    io.print("Area must be at least 100 sq ft. Please try again.");
                    continue;
                }
                return area;
            } catch (NumberFormatException e) {
                io.print("Invalid input. Please enter a valid number (e.g., 150.5).");
            }
        }
    }

    // -------------------- VALIDATE for Edit Order Functionality --------------------

    public String getEditedCustomerName(String oldName) {
        while (true) {
            String newName = io.readString("Enter new customer name (" + oldName + " to keep) ").trim();

            if (newName.isEmpty()) {
                return oldName; // keep old name if blank
            }

            // Validate: must be alphanumeric plus . , and spaces
            if (newName.matches("[A-Za-z0-9., ]+")) {
                return newName; // valid
            } else {
                io.print("Invalid name. Only letters, numbers, spaces, commas, and periods are allowed. Please try again.");
            }
        }
    }

    public String getEditedProductType(String oldProductType, ServiceLayer service) {
        while (true) {
            String newProduct = io.readString(
                    "Enter new product type (" + oldProductType + " to keep): "
            ).trim();

            if (newProduct.isEmpty()) {
                return oldProductType;
            }

            if (!newProduct.matches("[A-Za-z ]+")) {
                io.print("Invalid product type. Please enter only letters and spaces.");
                continue;
            }

            try {
                if (service.isValidProduct(newProduct)) {
                    return newProduct; // valid
                } else {
                    io.print("We do not sell in " + newProduct + ". Please try again.");
                }
            } catch (PersistenceException | FileNotFoundException e) {
                io.print("Error checking state. Please try again.");
            }
        }
    }



    public BigDecimal getEditedArea(BigDecimal oldArea) {
        while (true) {
            String input = io.readString("Enter new area in sq ft (" + oldArea + " to keep) ").trim();

            // If user presses Enter, keep old value
            if (input.isEmpty()) {
                return oldArea;
            }

            try {
                BigDecimal newArea = new BigDecimal(input);

                // Must be at least 100 sq ft
                if (newArea.compareTo(new BigDecimal("100")) >= 0) {
                    return newArea;
                } else {
                    io.print("Area must be at least 100 sq ft. Please try again.");
                }
            } catch (NumberFormatException e) {
                io.print("Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    public String getEditedState(String oldState, ServiceLayer service) {
        while (true) {
            String input = io.readString("Enter new State abbreviation (" + oldState + " to keep) ").trim();

            // Keep old value if blank
            if (input.isEmpty()) {
                return oldState;
            }

            try {
                if (service.isValidState(input)) {
                    return input; // valid
                } else {
                    io.print("We do not sell in " + input + ". Please try again.");
                }
            } catch (PersistenceException | FileNotFoundException e) {
                io.print("Error checking state. Please try again.");
            }
        }
    }

    // -------------------- FORMATTING UI --------------------

    public void displayUnknownCommandBanner() {
        io.print("Unknown command!!!");
    }

    public void displayExitBanner() {
        io.print("Goodbye!!!");
    }

    public void displayAddOrderBanner() {
        io.print("\n=== Add Order ===");
    }

    public void displayEditOrderBanner() {
        io.print("\n=== Edit Order ===");
    }

    public void displayRemoveOrderBanner() {
        io.print("\n=== Remove Order ===");
    }
}
