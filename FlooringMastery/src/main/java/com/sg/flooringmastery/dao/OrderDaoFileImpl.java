package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Orders;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class is responsible for managing all order data that’s saved in text files. It’s part of the Dao
 * layer and works behind the scenes to load, save, edit, and delete orders from files on disk.
 */
@Repository
public class OrderDaoFileImpl implements OrderDao {

    // Constants defined for path and delimiter as these do not change throughout this project.
    // Allows for easy changing if the path were to change.
    private static final String ORDER_FOLDER = "SampleFileData/Orders";
    private static final String DELIMITER = ",";

    // Orders are read from the file to a HashMap as customerNumbers are unique.
    private final Map<LocalDate, Map<Integer, Orders>> allOrders = new HashMap<>();

    // ------------------ CRUD OPERATIONS -------------------

    // Adds a new order to the in memory map and writes all orders back to file.
    @Override
    public void addOrder(LocalDate date, Orders order) throws PersistenceException {
        Map<Integer, Orders> ordersForDate = allOrders.getOrDefault(date, new HashMap<>());
        ordersForDate.put(order.getOrderNumber(), order);
        allOrders.put(date, ordersForDate);
        writeFile();
    }

    // Updates an existing order for the specified date.
    @Override
    public void editOrder(LocalDate date, int orderNumber, Orders updatedOrder) throws PersistenceException {
        // Always load existing orders first
        getOrdersByDate(date);

        Map<Integer, Orders> ordersForDate = allOrders.get(date);

        if (ordersForDate == null || !ordersForDate.containsKey(orderNumber)) {
            throw new PersistenceException("Order not found for editOrder().");
        }

        // Update and rewrite file
        ordersForDate.put(orderNumber, updatedOrder);
        writeFile();
    }

    // Removes an order from the in memory collection and updates the file.
    @Override
    public void removeOrder(LocalDate date, int orderNumber) throws PersistenceException {
        Map<Integer, Orders> ordersForDate = allOrders.get(date);
        if (ordersForDate == null) return;
        Orders removed = ordersForDate.remove(orderNumber);
        writeFile();
    }

    // ----------------------- FILE LOADING ------------------------

    // Reads in a whole Order File by first calculating the file name via the date.
    @Override
    public List<Orders> getOrdersByDate(LocalDate date) throws PersistenceException {
        String filename = ORDER_FOLDER + "/Orders_"
                + date.format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";

        List<Orders> orders = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            allOrders.remove(date); // ensure consistency
            return orders;
        }

        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // skip header
            }

            while (scanner.hasNextLine()) {
                String currentLine = scanner.nextLine();
                Orders order = unmarshallOrder(currentLine);
                orders.add(order);
                ordersMap.put(order.getOrderNumber(), order);
            }
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load order data for date: " + date, e);
        }
        allOrders.put(date, ordersMap);

        return orders;
    }

    // Converts a single Text line from the file into an Object. Called by getOrdersByDate().
    private static Orders unmarshallOrder(String currentLine) {
        String[] tokens = currentLine.split(DELIMITER);

        Orders orderFromFile = new Orders();
        orderFromFile.setOrderNumber(Integer.parseInt(tokens[0]));
        orderFromFile.setCustomerName(tokens[1]);
        orderFromFile.setState(tokens[2]);
        orderFromFile.setTaxRate(new BigDecimal(tokens[3]));
        orderFromFile.setProductType(tokens[4]);
        orderFromFile.setArea(new BigDecimal(tokens[5]));
        orderFromFile.setCostPerSquareFoot(new BigDecimal(tokens[6]));
        orderFromFile.setLaborCostPerSquareFoot(new BigDecimal(tokens[7]));
        orderFromFile.setMaterialCost(new BigDecimal(tokens[8]));
        orderFromFile.setLaborCost(new BigDecimal(tokens[9]));
        orderFromFile.setTax(new BigDecimal(tokens[10]));
        orderFromFile.setTotal(new BigDecimal(tokens[11]));

        return orderFromFile;
    }

    // ----------------------- FILE WRITING ------------------------

    // Writes all orders currently in memory to their corresponding files.
    @Override
    public void writeFile() throws PersistenceException {
        try {
            for (Map.Entry<LocalDate, Map<Integer, Orders>> entry : allOrders.entrySet()) {
                LocalDate date = entry.getKey();
                String filename = "SampleFileData/Orders/Orders_"
                        + date.format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";

                try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
                    out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,"
                            + "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");

                    for (Orders currentOrder : entry.getValue().values()) {
                        out.println(marshallOrder(currentOrder));
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Could not save order data.", e);
        }
    }

    // Converts an Order object into a string separated by commas, suitable for saving to a file.
    private static String marshallOrder(Orders o) {
        return o.getOrderNumber() + DELIMITER +
                o.getCustomerName() + DELIMITER +
                o.getState() + DELIMITER +
                o.getTaxRate().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getProductType() + DELIMITER +
                o.getArea().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getCostPerSquareFoot().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getLaborCostPerSquareFoot().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getMaterialCost().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getLaborCost().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getTax().setScale(2, RoundingMode.HALF_UP) + DELIMITER +
                o.getTotal().setScale(2, RoundingMode.HALF_UP);
    }

    // ----------------------- HELPER METHODS ------------------------

    // Generates the next availabile order number for a given date.
    @Override
    public int generateNextOrderNumber(LocalDate date) throws PersistenceException {
        Map<Integer, Orders> ordersForDate = allOrders.get(date);
        if (ordersForDate == null || ordersForDate.isEmpty()) return 1;
        return Collections.max(ordersForDate.keySet()) + 1;
    }
}
