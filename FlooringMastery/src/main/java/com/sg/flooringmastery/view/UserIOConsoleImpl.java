package com.sg.flooringmastery.view;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

/**
 * This class is used by the application to handle user interaction through the console.
 * By providing further context.
 * Defines how interaction with the console is to be carried out.
 */
@Component
public class UserIOConsoleImpl implements UserIO{

    private final Scanner scanner = new Scanner(System.in);

    public void print(String prompt){
        System.out.println(prompt);
    }

    public String readString(String prompt){
        System.out.println(prompt+": ");
        return scanner.nextLine();
    }

    public int readInt(String prompt){
        System.out.println(prompt+": ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int readInt(String prompt, int min, int max){
        int result;
        do{
            result = readInt(prompt);
            if(result < min || result > max){
                print("Error: Please enter a number between "+ min +" and "+ max +".");
            }
        }while(result < min || result > max);
        return result;
    }

    public BigDecimal readBigDecimal(String prompt){
        System.out.println(prompt+": ");
        return new BigDecimal(scanner.nextLine()).setScale(2, RoundingMode.HALF_UP) ;
    }

}
