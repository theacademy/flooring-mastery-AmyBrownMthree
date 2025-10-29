package com.sg.flooringmastery.model;


import java.math.BigDecimal;

/**
 * This is the Products Class Definition taken from the Project Brief.
 * All Datatypes are correct according to the brief.
 * File only contains getters() + setters()
 */
public class Products {

    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal labourCostPerSquareFoot;

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
    }

    public BigDecimal getLabourCostPerSquareFoot() {
        return labourCostPerSquareFoot;
    }

    public void setLabourCostPerSquareFoot(BigDecimal labourCostPerSquareFoot) {
        this.labourCostPerSquareFoot = labourCostPerSquareFoot;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }




}
