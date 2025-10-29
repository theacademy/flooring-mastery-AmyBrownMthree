package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Products;
import com.sg.flooringmastery.service.exceptions.PersistenceException;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for managing all Product data that’s saved in text files. It’s part of the Dao
 * layer and works behind the scenes to load, save, edit, and delete orders from files on disk.
 */
@Repository
public class ProductDaoFileImpl implements ProductDao {

    // Constants defined for path and delimiter as these do not change throughout this project.
    // Allows for easy changing if the path were to change.
    private static final String PRODUCT_FILE = "SampleFileData/Data/Products.txt";
    private static final String DELIMITER =",";

    // Loads all the products contained within the 'Products.txt' File into an ArrayList.
    @Override
    public List<Products> getAllProducts() throws FileNotFoundException, PersistenceException {
        List<Products> productList = new ArrayList<>();
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));

        if (scanner.hasNextLine()) {
            scanner.nextLine(); // skip header
        }

        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            String[] tokens = currentLine.split(DELIMITER);

            // data unmarshalling on smaller scale;
            Products product = new Products();
            product.setProductType(tokens[0]);
            product.setCostPerSquareFoot(new BigDecimal(tokens[1]));
            product.setLabourCostPerSquareFoot(new BigDecimal(tokens[2]));
            productList.add(product);
        }

        scanner.close();
        return productList;
    }

    // Retrieves a matching object from the file dependant on the type parameter.
    @Override
    public Products getProductByType(String productType) throws FileNotFoundException, PersistenceException {

        List<Products> allProducts = getAllProducts();
        for(Products t: allProducts){
            if(t.getProductType().equalsIgnoreCase(productType)){
                return t;
            }
        }
        return null;
    }
}
