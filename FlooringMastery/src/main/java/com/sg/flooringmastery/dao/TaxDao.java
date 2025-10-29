package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Taxes;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This interface allows for communication between the Tax Dao and the ServiceLayer.
 * Lists the actions that the service layer is allowed to do with relation to the Tax File.
 */

public interface TaxDao {

    List<Taxes> getAllTaxes() throws FileNotFoundException, PersistenceException;

    Taxes getTaxByState(String stateAbbreviation) throws FileNotFoundException, PersistenceException;
}
