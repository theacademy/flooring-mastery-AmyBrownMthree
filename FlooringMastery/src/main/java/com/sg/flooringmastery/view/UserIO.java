package com.sg.flooringmastery.view;

import java.math.BigDecimal;

/**
 * This interface defines the basic methods for console input and output operations, as seen within the data types
 * used by the model class definitions.
 */
public interface UserIO {

    void print(String prompt);
    String readString(String prompt);
    int readInt(String prompt);
    int readInt(String prompt, int min, int max);
    BigDecimal readBigDecimal(String prompt);

}
