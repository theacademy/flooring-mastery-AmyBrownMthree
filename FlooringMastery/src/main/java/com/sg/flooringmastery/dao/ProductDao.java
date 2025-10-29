package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Products;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This interface allows for communication between the Product Dao and the ServiceLayer.
 * Lists the actions that the service layer is allowed to do with relation to the Product File.
 */

public interface ProductDao {

    List<Products> getAllProducts() throws FileNotFoundException, PersistenceException;

    Products getProductByType(String productType) throws FileNotFoundException, PersistenceException;
}
