package com.sg.flooringmastery.model;

import java.math.BigDecimal;

/**
 * This is the Taxes Class Definition taken from the Project Brief.
 * All Datatypes are correct according to the brief.
 * File only contains getters() + setters()
 */
public class Taxes {

    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

}
