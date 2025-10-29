package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Taxes;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for managing all Tax data that’s saved in text files. It’s part of the Dao
 * layer and works behind the scenes to load, save, edit, and delete orders from files on disk.
 */
@Repository
public class TaxDaoFileImpl implements TaxDao{

    // Constants defined for path and delimiter as these do not change throughout this project.
    // Allows for easy changing if the path were to change.
    private static final String TAX_FILE = "SampleFileData/Data/Taxes.txt";
    private static final String DELIMITER =",";

    //Retrives the whole Taxes.txt File and stores it within a List of Tax objects.
    @Override
    public List<Taxes> getAllTaxes() throws FileNotFoundException {

        List<Taxes> taxes = new ArrayList<>();
        File file = new File(TAX_FILE);

        Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
        if (scanner.hasNextLine()) {
            scanner.nextLine(); // skip header
        }

        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();
            Taxes tax = unmarshallOrder(currentLine);
            taxes.add(tax);
        }

        return taxes;
    }

    // changes a String into a OOP Object.
    private static Taxes unmarshallOrder(String currentLine) {
        String[] taxTokens = currentLine.split(DELIMITER);

        Taxes taxFromFile = new Taxes();
        taxFromFile.setStateAbbreviation(taxTokens[0]);
        taxFromFile.setStateName(taxTokens[1]);
        taxFromFile.setTaxRate(new BigDecimal(taxTokens[2]));

        return taxFromFile;
    }

    // Retrieves a matching object from the file dependant on the type parameter.
    @Override
    public Taxes getTaxByState(String state) throws FileNotFoundException {
        List<Taxes> states = getAllTaxes();
        for(Taxes t: states){
            if(t.getStateName().equalsIgnoreCase(state)){
                return t;
            }
        }
        return null;
    }
}
