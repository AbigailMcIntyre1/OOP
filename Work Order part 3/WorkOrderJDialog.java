// Abigail McIntyre
// Work Order Project
// 10/16/2021

// a JDialog subclass which allows for entering or editing a work order
//    a.) Provides two constructors, one for entering a new work order, and one for editing an existing work order.
//    b.) Provides validation (field by field, and also consistency check and required field check).

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import java.awt.Dialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.util.Date;

public class WorkOrderJDialog extends JDialog 
                              implements ActionListener 
{
    JButton submitButton;
    JButton cancelButton;
    JButton addStayOpenButton;

    JPanel inputPanel;                                                      // holds the text fields and labels
    JPanel buttonsPanel;                                                    // holds the submit and cancel buttons on the bottom

    GroupLayout inputLayout;

    // labels beside the text fields
    JLabel nameInputLabel;
    JLabel departmentInputLabel;
    JLabel descriptionInputLabel;
    JLabel billingRateInputLabel;
    JLabel dateRequestedLabel;
    JLabel dateFulfilledLabel;

    // labels for errors
    JLabel nameInputError;
    JLabel descriptionInputError;
    JLabel billingRateError;
    JLabel dateRequestedError;
    JLabel dateFulfilledError;

    // text fields for user input
    JTextField nameInput;
    JComboBox<String> departmentInput;
    JTextField descriptionInput;
    JTextField billingRateInput;
    JTextField dateRequestedInput;
    JTextField dateFulfilledInput;

    // work order stuff
    WorkOrder editedWorkOrder;
    DataManager dataManager;
    int index;                                                           // to replace the right work order when editing

    boolean isEditing;                                                   // to see if the user is editing a work order

    // =====================================================================================================================

    // constructor to add a new work order
    public WorkOrderJDialog(DataManager dataManager)
    { 
        this.dataManager = dataManager;
        isEditing = false;

        createLayout();
        setUpDialogBox();
    }
    
    // =====================================================================================================================

    // constructor for editing a work order
    public WorkOrderJDialog(DataManager dataManager, WorkOrder editedWorkOrder, int index)
    { 
        DateFormat dateFormat;

        this.index = index;
        this.dataManager = dataManager;
        this.editedWorkOrder = editedWorkOrder;

        isEditing = true;

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        createLayout();

        nameInput.setText(editedWorkOrder.name);
        departmentInput.setSelectedItem(editedWorkOrder.department);
        descriptionInput.setText(editedWorkOrder.description);
        billingRateInput.setText(Float.toString(editedWorkOrder.billingRate));
        dateRequestedInput.setText(dateFormat.format(editedWorkOrder.dateRequested));
        dateFulfilledInput.setText(dateFormat.format(editedWorkOrder.dateFulfilled));

        setUpDialogBox();
    }

    // =====================================================================================================================

    // so that we don't have to have the same code in multiple constructors
    private void createLayout()
    {
        // ----------------- JButtons -----------------------------------
        submitButton = new JButton("Add");                                          // add the input to the JTable
        submitButton.addActionListener(this);

        addStayOpenButton = new JButton("Add and Stay Open");                       // add the input to the JTable but keep JDialog open
        addStayOpenButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");                                       // cancel the input
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        // --------- JPanels ---------------------------------------------
        inputPanel = new JPanel();
        buttonsPanel = new JPanel();
        buttonsPanel.add(submitButton);
        buttonsPanel.add(addStayOpenButton);
        buttonsPanel.add(cancelButton);

        // ----------- labels ---------------------------------------------
        nameInputLabel = new JLabel("Name");
        departmentInputLabel = new JLabel("Department");
        descriptionInputLabel = new JLabel("Description");
        billingRateInputLabel = new JLabel("Billing rate");
        dateRequestedLabel = new JLabel("Date requested");
        dateFulfilledLabel = new JLabel("Date fulfilled");
        
        // ----------- error labels - not visible yet ----------------------
        nameInputError = makeJLabel("Name Invalid", false);
        descriptionInputError = makeJLabel("Description invalid", false);
        billingRateError = makeJLabel("Billing rate invalid", false);
        dateRequestedError = makeJLabel("Request date invalid", false);
        dateFulfilledError = makeJLabel("Fulfillment date invalid", false);

        // ---------------- text fields -------------------------------------
        nameInput = new JTextField(15);
        nameInput.setInputVerifier(new Verifier(0, nameInputError));
        
        String[] departments = {"SALES", "HARDWARE", "ELECTRONICS"};
        departmentInput = new JComboBox<String>(departments);

        descriptionInput = new JTextField(15);
        descriptionInput.setInputVerifier(new Verifier(2, descriptionInputError));

        billingRateInput = new JTextField(15);
        billingRateInput.setInputVerifier(new Verifier(3, billingRateError));

        dateRequestedInput = new JTextField(15);
        dateRequestedInput.setInputVerifier(new Verifier(4, dateRequestedError));
        
        dateFulfilledInput = new JTextField(15);
        dateFulfilledInput.setInputVerifier(new Verifier(5, dateFulfilledError));
        
        // ----------------- Group Layout --------------------------------------
        inputLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        // ------------ group layout stuff - horizontal group ------------------
        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();

        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(nameInputLabel)
                    .addComponent(departmentInputLabel)
                    .addComponent(descriptionInputLabel)
                    .addComponent(billingRateInputLabel)
                    .addComponent(dateRequestedLabel)
                    .addComponent(dateFulfilledLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(nameInput)
                    .addComponent(departmentInput)
                    .addComponent(descriptionInput)
                    .addComponent(billingRateInput)
                    .addComponent(dateRequestedInput)
                    .addComponent(dateFulfilledInput));
        hGroup.addGroup(inputLayout.createParallelGroup()
                    .addComponent(nameInputError)
                    .addComponent(descriptionInputError)
                    .addComponent(billingRateError)
                    .addComponent(dateRequestedError)
                    .addComponent(dateFulfilledError));
        inputLayout.setHorizontalGroup(hGroup);

        // ---------- group layout stuff - vertical group ---------------------
        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();

        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(nameInputLabel)
                    .addComponent(nameInput)
                    .addComponent(nameInputError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(departmentInputLabel)
                    .addComponent(departmentInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(descriptionInputLabel)
                    .addComponent(descriptionInput)
                    .addComponent(descriptionInputError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(billingRateInputLabel)
                    .addComponent(billingRateInput)
                    .addComponent(billingRateError));
                    vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(dateRequestedLabel)
                    .addComponent(dateRequestedInput)
                    .addComponent(dateRequestedError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(dateFulfilledLabel)
                    .addComponent(dateFulfilledInput)
                    .addComponent(dateFulfilledError));
        inputLayout.setVerticalGroup(vGroup);

        // ------------- add the panels ---------------------------------------
        add(inputPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    // =====================================================================================================================
    
    // for the submit and cancel buttons
    public void actionPerformed(ActionEvent e)
    {
        // ----------- cancel ----------------------------------------
        if(e.getSource() == cancelButton)                                           // if the source is the cancel button then cancel the JDialog
            dispose();

        // ----------- submit ----------------------------------------
        else if(e.getSource() == submitButton)                                      // if the source is the submit button then validate the input,
        {                                                                           // and if it's good, then see if the user is adding or editing
            boolean isGood = validateInput();                                       // validate

            if(isGood)
            {
                //if the user is editing a work order
                if(isEditing)
                { 
                    WorkOrder returnOrder = setUpReturnOrder();
                    dataManager.ReplaceItem(returnOrder, index);
                } 
                //if the user is adding a work order
                else 
                {        
                    WorkOrder returnOrder = setUpReturnOrder();
                    dataManager.AddItem(returnOrder);
                }

                dispose();                                                           // get rid of the JDialog box
            }
        }

        // ----------- submit and stay open  ---------------------------
        else if(e.getSource() == addStayOpenButton)
        {
            boolean isGood = validateInput();                                       

            if(isGood)
            {
                //if the user is editing a work order
                if(isEditing)
                { 
                    WorkOrder returnOrder = setUpReturnOrder();
                    dataManager.ReplaceItem(returnOrder, index);
                } 
                //if the user is adding a work order
                else 
                {        
                    WorkOrder returnOrder = setUpReturnOrder();
                    dataManager.AddItem(returnOrder);
                }

                // return all the textfields to blank
                nameInput.setText("");
                nameInput.requestFocus();
                descriptionInput.setText("");
                billingRateInput.setText("");
                dateRequestedInput.setText("");
                dateFulfilledInput.setText("");

            }
        }
    }

    // =====================================================================================================================

    // validate the user input
    private boolean validateInput()
    {
        boolean isValid = true;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // --------------- Check name -----------------------------------
        if(nameInput.getText().trim().equals(""))                                           // if name is empty, display the error and return "mouse"
        {                                                                                   // to that field
            isValid = false;             
            nameInputError.setVisible(true);
            nameInput.requestFocus();
        }

        // -------------- Check billing rate ----------------------------
        try 
        {   
            float billingRate;

            billingRate = Float.parseFloat(billingRateInput.getText());

            if(!(billingRate >= 4.2 && billingRate <= 15.8))                                // if billingRate isn't in the range of 5.6 to 12.8
            {                                               
                isValid = false;                                                            // it's not valid

                billingRateError.setVisible(true);                                          // display the error
                billingRateInput.requestFocus();                                            // set the text field focus
            }
        } 
        catch (NumberFormatException e)
        {
            isValid = false;
            billingRateError.setVisible(true);
            billingRateInput.requestFocus();
        }

        // -------------- Check the dates --------------------------------
        try
        {                                        
            long dateRequested = dateFormat.parse(dateRequestedInput.getText()).getTime();         // parse the requested date
            long dateFulfilled = dateFormat.parse(dateFulfilledInput.getText()).getTime();         // parse the fulfilled date

            if(dateFulfilled < dateRequested)                                                      // if the date fulfilled is before the request
            {
                isValid = false;                                                                   // it's not valid
                dateFulfilledError.setVisible(true);                                               // set both errors to visible
                dateRequestedError.setVisible(true);
                dateRequestedInput.requestFocus();                                                 // set the text field focus to that field
            }
        } 
        // display the errors and stuff like before if there was a problem parsing
        catch(ParseException e)
        {
            isValid = false;
            dateFulfilledError.setVisible(true);
            dateRequestedError.setVisible(true);
            dateRequestedInput.requestFocus();
        }

        return isValid;
    }

    // =====================================================================================================================

    // set up the work order to return
    private WorkOrder setUpReturnOrder()
    {
        DateFormat dateFormat;

        String returnName;
        String returnDepartment;
        String returnDescription;
        float returnBillingRate;
        Date returnRequestedDate;
        Date returnFulfilledDate;

        returnName = nameInput.getText();
        returnDepartment = departmentInput.getSelectedItem().toString();                    // turn it into a string
        returnDescription = descriptionInput.getText();
        returnBillingRate = Float.parseFloat(billingRateInput.getText());                   // turn it into a float
        returnRequestedDate = new Date();
        returnFulfilledDate = new Date();

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try 
        {
            returnRequestedDate = dateFormat.parse(dateRequestedInput.getText());           // format the date  
            returnFulfilledDate = dateFormat.parse(dateFulfilledInput.getText());           // format the date
        } 
        catch (ParseException e)
        {
            System.out.println("Error while parsing the dates");
        }

        return new WorkOrder(returnName, returnDepartment, returnRequestedDate, returnFulfilledDate, returnDescription, returnBillingRate);
    }

    // =====================================================================================================================

    // set up the JLabel
    private JLabel makeJLabel(String text, boolean visibility)
    {
        JLabel label;

        label = new JLabel(text);
        label.setVisible(visibility);
    
        return label;
    }

    // =====================================================================================================================

    // set up the JDialog
    private void setUpDialogBox()
    {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        setSize(40 * d.width / 100, 40 * d.height / 100);                   // 40 is the screen percentage to take up
        setLocation(d.width / 4, d.height / 4);

        setTitle("Work Order");

        setVisible(true);
    }
    // =====================================================================================================================
}