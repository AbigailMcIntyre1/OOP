// Abigail McIntyre
// Due 9/9/2021
// a simple calculator that outputs the sin, cos, sqrt, ln, and their inverses of a number

import java.awt.*;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

class Calculator extends JFrame
                    implements ActionListener, DocumentListener
{
        // declarations of everything
        JPanel inputPanel;
        JPanel outputTextFieldPanel;
        JPanel operatorPanel;
    
        ButtonGroup radioOption;
        JButton clearButton;
        JCheckBox inverseCheck;
        JRadioButton sqrtRadio;
        JRadioButton sinRadio;
        JRadioButton cosRadio;
        JRadioButton lnButton;
    
        JTextField input;
        JTextField output;

    // constructor
    Calculator()
    {
        // JPanels for input, output, and the operators
        inputPanel = new JPanel();
        outputTextFieldPanel = new JPanel();
        operatorPanel = new JPanel();


        // Radio Buttons for operations
        // addActionListener(this) to listen for button presses
        // setActionCommand("Selection") to set the command name for the action event to use later
        // set sqrt as the default button 

        sqrtRadio = new JRadioButton("Square Root");
        sqrtRadio.addActionListener(this);
        sqrtRadio.setActionCommand("Selection");
        sqrtRadio.setSelected(true);                                 
        
        sinRadio = new JRadioButton("Sin");
        sinRadio.addActionListener(this);
        sinRadio.setActionCommand("Selection");
        
        cosRadio = new JRadioButton("Cos");
        cosRadio.addActionListener(this);
        cosRadio.setActionCommand("Selection");
        
        lnButton = new JRadioButton("Ln");
        lnButton.addActionListener(this);
        lnButton.setActionCommand("Selection");
        

        // JButton to clear the input
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        clearButton.setActionCommand("Clear");


        // check box to select the inverse of the operation
        inverseCheck = new JCheckBox("Inverse");
        inverseCheck.addActionListener(this);
        inverseCheck.setActionCommand("Selection");


        // makes it to where you can only select one radiobutton
        radioOption = new ButtonGroup();


        // text fields for input and output; output won't allow users to input text
        input = new JTextField(8);
        input.getDocument().addDocumentListener(this);

        output = new JTextField(16);
        output.setEditable(false);    
        

        // add the buttons to the panels
        operatorPanel.add(sqrtRadio);
        operatorPanel.add(sinRadio);
        operatorPanel.add(cosRadio);
        operatorPanel.add(lnButton);
        operatorPanel.add(inverseCheck);


        // add the buttons to the button group
        radioOption.add(sqrtRadio);
        radioOption.add(sinRadio);
        radioOption.add(cosRadio);
        radioOption.add(lnButton);


        // add the input area and clear button to the panels
        inputPanel.add(input);
        inputPanel.add(clearButton);


        // add the output to the output panel
        outputTextFieldPanel.add(output);
        radioOption = new ButtonGroup();


        // add all the panels
        add(inputPanel, BorderLayout.CENTER);
        add(outputTextFieldPanel, BorderLayout.SOUTH);
        add(operatorPanel, BorderLayout.EAST);


        // set everything up
        setupMainFrame();

    } // end constructor

    //---------------------------------------------------------------------------------------------------------------

    // from Professor Larue - set up the window area and size
    public void setupMainFrame()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        setSize(d.width / 2, d.height / 4);
        setLocation(d.width / 4, d.height / 4);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    //---------------------------------------------------------------------------------------------------------------

    // performs actions based on button presses
    public void actionPerformed(ActionEvent e)
    {
        // if the action is a button selection then update the output text box
        if (e.getActionCommand().equals("Selection"))
        {
            update();
        }

        // if the action is the click of the clear button then clear both text fields
        else if(e.getSource() == clearButton)
        {
            input.setText("");
            output.setText("");
        }
    }

    //---------------------------------------------------------------------------------------------------------------

    // method to update the output box and check if input is valid
    public void update()
    {
        double enteredValue = 0;                            // to hold the text in the field to test if it's valid
        boolean isGood = true;                              // to see if the text in the field is valid
        boolean inverse = inverseCheck.isSelected();        // to see if the inverse button is checked

        // if the input has text in it
        if(!input.getText().trim().equals(""))
        {
            // see if the text is valid (not a character)
            try
            {
                enteredValue = Double.valueOf(input.getText());
            }

            // if it's invalid, throw an error in the output box
            catch(NumberFormatException e)
            {
                output.setText("Error");
                isGood = false;
            }

            // calculations if the text in the field is valid - outputs the result of the math
            if(isGood == true)
            {
                // square root calculations and output
                if(sqrtRadio.isSelected())
                {
                    output.setText(sqrtCalc(enteredValue, inverse));
                }

                // sin calculations and output
                else if(sinRadio.isSelected())
                {
                    output.setText(sinCalc(enteredValue, inverse));
                }

                // cosine calculations and output
                else if(cosRadio.isSelected())
                {
                    output.setText(cosCalc(enteredValue, inverse));
                }

                // ln calculations and output
                else if(lnButton.isSelected())
                {
                    output.setText(lnCalc(enteredValue, inverse));
                }

            } // end if isGood == true
        } // end if(!input.getText().trim().equals(""))

        // is the textbox is blank, the output will be blank as well
        else
        {
            output.setText("");
        }

    } // end public void update

    //---------------------------------------------------------------------------------------------------------------
    
    // method to calculate the square root value and its inverse
    private String sqrtCalc(double h, boolean inverse)
    {
        // do the inverse which is the power
        if(inverse == true)
        {
            return Double.toString(Math.pow(h, 2));
        }

        // so we're not taking the square root of a negative number
        if(h >= 0)
        {
            return Double.toString(Math.sqrt(h));
        }

        // if we are taking the square root of a negative, throw an error
        return "Error";
    }

    //------------------------------------------------------------------------------------------------------------------

    // method to calculate the sine and its inverse
    private String sinCalc(double h, boolean inverse)
    {
        // the inverse, Arcsine
        if(inverse == true)
        {
            // as long as h is between -1 and 1
            if(h >= -1 && h <= 1)
            {
                return Double.toString(Math.asin(h));
            }

            // if it's not between -1 and 1, throw an error
            else
                 return "Error";
        }

        // else return the sine of the number
        return Double.toString(Math.sin(h));
    }

    //------------------------------------------------------------------------------------------------------------------

    // method to calculate the cos and its inverse
    private String cosCalc(double h, boolean inverse)
    {
        if(inverse == true)
        {
            if(h >= -1 && h <= 1)
            {
                return Double.toString(Math.acos(h));
            }
            else
                return "Error";
        }

        // return the cosine of the number
        return Double.toString(Math.cos(h));
    }

    //------------------------------------------------------------------------------------------------------------------

    private String lnCalc(double h, boolean inverse)
    {
        if(inverse == true)
        {
            return Double.toString(Math.exp(h));
        }

        if(h > 0)
        {
            return Double.toString(Math.log(h));
        }

        else
            return "Error";
    }

    //------------------------------------------------------------------------------------------------------------------

    public void insertUpdate(DocumentEvent e)
    {
        if(e.getDocument().equals(input.getDocument()))
        {
            update();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void removeUpdate(DocumentEvent e)
    {
        if(e.getDocument().equals(input.getDocument()))
        {
            update();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void changedUpdate(DocumentEvent e)
    {
        if(e.getDocument().equals(input.getDocument()))
        {
            update();
        }
    }

} // end class