// Abigail McIntyre
// Work Order Project
// 10/16/2021

// validates the data being submitted to the work order

import java.text.*;
import java.util.Date;
import javax.swing.InputVerifier;
import javax.swing.*;

public class Verifier extends InputVerifier 
{
    JLabel errorLabel;
    boolean isValid;
    int type;

    SimpleDateFormat dateFormat;
    ParsePosition parsePosition;
    Date date;

    Verifier(int type, JLabel errorLabel)
    {
        this.type = type;    
        this.errorLabel = errorLabel;
    }
    
    public boolean verify(JComponent component)
    {
        String input = ((JTextField)component).getText().trim();

        // If what's being verified is the name
        if(type == 0)         
            isValid = true;
        
        // If what's being verified is the description
        else if(type == 2) 
            isValid = true;
        
        // If what's being verified is the billing rate
        else if(type == 3)
        {   
            try 
            {
                if(input.equals(""))
                    isValid = true;

                else 
                {
                    float rateNum = Float.parseFloat(input);
                    if(!(rateNum > 4.2 && rateNum <= 15.8))
                    {
                        isValid = false;
                        errorLabel.setText("The rate is out of range");
                    } 
                    else 
                        isValid = true;
                }         
            } 
            catch(NumberFormatException e)
            {
                isValid = false;
                errorLabel.setText("The input is not a number");
            }
        } 

        // if what's being verified is the date
        else if(type == 4)
        {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);

            parsePosition = new ParsePosition(0);

            date = dateFormat.parse(input, parsePosition);

            isValid = input.equals("") || (parsePosition.getIndex() == input.length() && date != null);
        } 

        // if what's being verified is the date
        else if(type == 5)
        {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);

            parsePosition = new ParsePosition(0);

            date = dateFormat.parse(input, parsePosition);

            isValid = input.equals("") || (parsePosition.getIndex() == input.length() && date != null);
        }

        if(!isValid)
            errorLabel.setVisible(true);
        
        else 
            errorLabel.setVisible(false);
        
        return isValid;
    }

}