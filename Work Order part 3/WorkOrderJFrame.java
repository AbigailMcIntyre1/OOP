// Abigail McIntyre
// Due 9/16/2021

// contains: 1.) a JTable for showing a list of work orders, backed by the MyTableModel subclass. 2.) Button for loading from a file using JFileChooser
//           3.) Button for Saving the current list of work orders to a file using a JFileChooser 4.) Button for adding new work orders to the list
//           5.) Button for editing a work order selected in the JTable                           6.) Button for deleting work orders selected in the JTable
//           7.) Button for exiting the program                                                   8.) Button for printing the file
//           9.) A menu bar containing those buttons                                              10.) Ability to right-click and have a popupMenu appear
//           11.) Drag-n-drop a file to load it into the program

import java.io.*;
import java.util.Calendar;
import java.awt.*;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.JFileChooser;
import java.awt.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.print.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

class WorkOrderJFrame extends JFrame
                      implements ActionListener, ListSelectionListener, DataManager, MouseListener, DropTargetListener
{
    // ----------- declarations --------------------------------------------------------

    // -------- JPanels --------------------------------------
    JPanel mainPanel;
    JPanel bottomPanel;  

    // -------- JButtons -------------------------------------
    JButton exitButton;
    JButton deleteJButton;
    JButton editJButton;
    JButton saveAsJButton;
    JButton deleteAllJButton;  

    // -------- JMenuItems -----------------------------------  
    JMenuItem deleteButton;
    JMenuItem deleteAllButton;
    JMenuItem editButton;
    JMenuItem completePopup;   

    // ------- File Stuff ------------------------------------
    int response;
    File inFile;                                    
    JFileChooser chooser;
    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    FileOutputStream fos;
    
    // ------- Popup Menu ------------------------------------
    JPopupMenu popupMenu;
    MouseListener popupListener;
    Point mousePosition;

    // ------- JTable ----------------------------------------
    JTable table;
    JScrollPane myTableScroller;
    MyTableModel tableModel;
    TableRowSorter<MyTableModel> rowSorter;
    TableColumnModel colModel;  
    DropTarget dropTarget;
    Boolean modificationsMade;
    //-----------------------------------------------------------------------------------


    // ============ default constructor ================================================
    WorkOrderJFrame() 
    {
        modificationsMade = false;
        chooser = new JFileChooser(".");                                                    // sets the directory to the current working directory

        // ----- JTable Setup ----------------------
        colModel = new DefaultTableColumnModel();                                           // create a column model

        tableModel = new MyTableModel();                                                    // create a tablemodel
        tableModel.addTableModelListener(table);                                            // add a listener to get selected rows and stuff
        
        table = new JTable(tableModel);                                                     // create a JTable using the table model we set up
        table.setFont(new Font("Courier New", Font.BOLD, 14));                              // set the font and stuff
        table.getSelectionModel().addListSelectionListener(this);

        rowSorter = new TableRowSorter<MyTableModel>(tableModel);                           // to sort by rows
        table.setRowSorter(rowSorter);

        table.setColumnModel(getColumnModel());                                             // set the model for the columns

        myTableScroller = new JScrollPane(table);                                            // add a scrollbar
        table.setPreferredScrollableViewportSize(new Dimension(1150, 1050));                 // set the size of the table
        myTableScroller.getViewport().setBackground(Color.LIGHT_GRAY);                       // set the background color of the table

        table.addMouseListener(this);                                                        // add a mouse listener to the table for the popup

        popupMenu = newPopupMenu();                                                          // create the popup
        // ------------------------------------------


        // ----- create panels ---------------------
        mainPanel = new JPanel();
        bottomPanel = new JPanel();
        // -----------------------------------------


        // ----- add the JButtons ------------------
        bottomPanel.add(newJButton("Load", "LOAD", this));
        bottomPanel.add(newJButton("Save", "SAVE", this));

        saveAsJButton = newJButton("Save As...", "SAVE_AS", this);
        bottomPanel.add(saveAsJButton);

        bottomPanel.add(newJButton("Add", "NEW", this));

        deleteJButton = newJButton("Delete", "DELETE", this);
        bottomPanel.add(deleteJButton);
        deleteJButton.setEnabled(false);                                                      // disabled until something is selected

        deleteAllJButton = newJButton("Delete All", "DELETE_ALL", this);
        bottomPanel.add(deleteAllJButton);
        deleteAllJButton.setEnabled(false);

        editJButton = newJButton("Edit", "EDIT", this);
        bottomPanel.add(editJButton);
        editJButton.setEnabled(false);                                                        // disabled until something is selected

        bottomPanel.add(newJButton("Print", "PRINT", this));
        bottomPanel.add(newJButton("Exit", "EXIT", this));
        // ------------------------------------------


        // ---- place the panel and scroller ---------
        add(mainPanel, BorderLayout.NORTH);                                             // has the fileChosenLabel
        add(bottomPanel, BorderLayout.SOUTH);                                           // has the exit button
        add(myTableScroller, BorderLayout.WEST);                                        // add the scroll bar         
        // -------------------------------------------

        dropTarget = new DropTarget(myTableScroller, this);                             // for dropping files - gives it the component and listener

        // ----- add random --------------------------
        // for(int i = 0; i < 40; i++)
            // tableModel.addElement(WorkOrder.getRandom());
        // -------------------------------------------


        // ----- set everything up -------------------
        setJMenuBar(newMenuBar());
        setupMainFrame();
        // -------------------------------------------

    } // end default constructor

    // =========================================================================

    //  Creates the JPopupMenu 
    public JPopupMenu newPopupMenu()
    {
        JMenuItem menuItem;

        popupMenu = new JPopupMenu();

        // ------- edit button --------------------------
        menuItem = new JMenuItem("Edit");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("EDIT");
        popupMenu.add(menuItem);

        // ------- delete button --------------------------
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        menuItem.setActionCommand("DELETE");
        popupMenu.add(menuItem);

        // ------- completed button ---------------------
        completePopup = new JMenuItem("Mark as completed");
        completePopup.addActionListener(this);
        completePopup.setActionCommand("COMPLETED");
        popupMenu.add(completePopup);

        return popupMenu;
    }

    // ======================================================================================

    //  Adds the JMenuBar and JMenus 
    private JMenuBar newMenuBar() 
    {
        JMenuBar menuBar;
        JMenu subMenu;

        menuBar = new JMenuBar();                           // create a new JMenuBar to store File and Item in

        // --------------- SubMenu for File --------------------------

        subMenu = new JMenu("File", true);                  // set up a menu for a dropdown box named File
        subMenu.setMnemonic('F');                           // set the shortkey to F

        // add the Load, Save, and Save as buttons with their labels, action commands,
        // menu listeners, shortkeys, keycodes, and tooltiptext

        subMenu.add(newItem("Load", "LOAD", this, KeyEvent.VK_L, KeyEvent.VK_L, "Load an existing file."));             
        subMenu.add(newItem("Save", "SAVE", this, KeyEvent.VK_S, KeyEvent.VK_S, "Save a file."));                       
        subMenu.add(newItem("Save as...", "SAVE_AS", this, KeyEvent.VK_A, KeyEvent.VK_A, "Save a file as..."));         
        subMenu.add(newItem("Print", "PRINT", this, KeyEvent.VK_P, KeyEvent.VK_P, "Print the file"));                   

        menuBar.add(subMenu);                               
        // ----------------------------------------------------------


        // --------------- SubMenu for Item --------------------------

        subMenu = new JMenu("Item");                        // set up a menu for a dropdown box named Item
        subMenu.setMnemonic('I');                           // set the shortkey to I

        // add the New, Delete, and Delete All buttons with their labels, action
        // commands, menu listeners, shortkeys, keycodes, and tooltiptext

        subMenu.add(newItem("New", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "Add a new item."));                      

        // make a delete button, add it to the subMenu, and then disable it
        deleteButton = newItem("Delete", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete an item.");
        subMenu.add(deleteButton);
        deleteButton.setEnabled(false);                     // disabled until something is selected

        deleteAllButton = newItem("Delete All", "DELETE_ALL", this, KeyEvent.VK_K, KeyEvent.VK_K, "Delete all items.");
        subMenu.add(deleteAllButton);  
        deleteAllButton.setEnabled(false);
        menuBar.add(subMenu);                               

        editButton = newItem("Edit", "EDIT", this, KeyEvent.VK_E, KeyEvent.VK_E, "Edit item");
        subMenu.add(editButton);
        editButton.setEnabled(false);                       // disabled until something is selected

        // -------------------------------------------------------------

        return menuBar;

    } // end newMenuBar
    
    // ====================================================================================

    //  from Professor Larue - creates a new JMenuItem 
    private JMenuItem newItem(String label, String actionCommand, ActionListener menuListener, int mnemonic,
            int keyCode, String toolTipText) 
    {
        JMenuItem m;

        m = new JMenuItem(label, mnemonic);
        m.setAccelerator(KeyStroke.getKeyStroke(keyCode, KeyEvent.ALT_DOWN_MASK));              // set the keyboard shortcut
        m.setToolTipText(toolTipText);                                                          // set the displayed text when you hover over the button
        m.setActionCommand(actionCommand);                                                      // set the action command
        m.addActionListener(menuListener);                                                      // set the action listener

        return m;
    }
    
    // ======================================================================================

    //  sets up JButtons 
    public JButton newJButton(String label, String actionCommand, ActionListener menuListener)
    {
        JButton m;

        m = new JButton(label);
        m.setActionCommand(actionCommand);                                                      // set the action command
        m.addActionListener(menuListener);                                                      // set the action listener

        return m;
    }
    
    // ======================================================================================

    //  when buttons are pressed 
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getActionCommand().equals("LOAD"))
            load();

        else if(e.getActionCommand().equals("SAVE")) 
        {
            if (inFile == null)                                 // if a file hasn't been opened already, perform a save as
                saveAsJButton.doClick();
            else
                save();
        }

        else if(e.getActionCommand().equals("SAVE_AS"))
            saveAs();
            
        else if (e.getActionCommand().equals("NEW"))
            newItem();

        else if(e.getActionCommand().equals("DELETE"))
            delete();

        else if(e.getActionCommand().equals("DELETE_ALL"))
            deleteAll();
        
        else if(e.getActionCommand().equals("PRINT"))
            print();

        else if (e.getActionCommand().equals("EXIT"))
            exit();

        else if (e.getActionCommand().equals("EDIT"))
            editItem();

        else if (e.getActionCommand().equals("COMPLETED"))
            itemCompleted();

        else
            JOptionPane.showMessageDialog(null, "Error: action is " + e.getActionCommand());
    }

    // ======================================================================================

    public void exit()
    {
        // if there are unsaved changes, show the dialog
        if(modificationsMade == true)
        {
            int result = JOptionPane.showConfirmDialog(null, "Unsaved changes, exit without saving?", "Please Confirm", JOptionPane.YES_NO_OPTION);
            switch (result) 
            {
                case JOptionPane.YES_OPTION:
                System.exit(0);
                break;
                case JOptionPane.NO_OPTION:
                System.out.println("No");
                break;
            }
        }
        // if there aren't, exit the program
        else
            System.exit(0);
    }
    
    // ======================================================================================

    public void load() 
    {
        int option = chooser.showOpenDialog(this);              // get the file selected from the user

        if(option == JFileChooser.APPROVE_OPTION)               // if the user pressed open
        {                  
            // if there are unsaved changes to the file
            if(modificationsMade == true)
            {
                int result = JOptionPane.showConfirmDialog(null, "Unsaved changes, replace without saving?", "Please Confirm", JOptionPane.YES_NO_OPTION);
                switch (result) 
                {
                    case JOptionPane.YES_OPTION:
                    try 
                    {
                        for( int i = tableModel.getRowCount() - 1; i >= 0; i-- )
                            tableModel.removeElementAt(i);                                    // in order to reset the file before loading
        
                        inFile = chooser.getSelectedFile();
        
                        fis = new FileInputStream(inFile);             
                        dis = new DataInputStream(fis);                 
        
                        tableModel.loadFrom(dis);                       // call the method in the MyTableModel
        
                        dis.close();                                    // close the input stream
                        fis.close();                                    // close the file input stream
        
                        tableModel.fireTableDataChanged();
                        modificationsMade = false;
                    } 
                    catch(IOException o)
                    {
                        inFile = null;
                        JOptionPane.showMessageDialog(this, "Error opening file.");
                    }
                    break;

                    case JOptionPane.NO_OPTION:
                    System.out.println("No");
                    break;
                }

            }

            // if there are no unsaved changes to the file
            else
            {
                try 
                {
                    for( int i = tableModel.getRowCount() - 1; i >= 0; i-- )
                        tableModel.removeElementAt(i);                                    // in order to reset the file before loading
    
                    inFile = chooser.getSelectedFile();
    
                    fis = new FileInputStream(inFile);             
                    dis = new DataInputStream(fis);                 
    
                    tableModel.loadFrom(dis);                       // call the method in the MyTableModel
    
                    dis.close();                                    // close the input stream
                    fis.close();                                    // close the file input stream
    
                    tableModel.fireTableDataChanged();
                    modificationsMade = false;
                } 
                catch(IOException o)
                {
                    inFile = null;
                    JOptionPane.showMessageDialog(this, "Error opening file.");
                }
            }
        }

        deleteAllJButton.setEnabled(tableModel.getRowCount() > 0);                  // jbutton
        deleteAllButton.setEnabled(tableModel.getRowCount() > 0);                   // menu item

    } // end load
    
    // ======================================================================================

    //  saves the current file open 
    public void save() 
    {
        if(inFile != null)                                          // if a file has already been chosen
        {                                     
            try 
            {
                fos = new FileOutputStream(inFile);                
                dos = new DataOutputStream(fos);   

                tableModel.saveTo(dos);                             // call the method in the MyTableModel class

                dos.close();
                fos.close();
                modificationsMade = false;
            } 
            catch (IOException o)
            {
                JOptionPane.showMessageDialog(this, "Error saving file.");               
            }
        } 
        else                                                         // if a file hasn't been chosen
            saveAs();                                        
    }
    
    // ======================================================================================

    //  saves the file as a new file 
    public void saveAs() 
    {            
        int response = chooser.showSaveDialog(this);

        if(response == JFileChooser.APPROVE_OPTION)                 // if the user presses ok
        {                  
            try 
            {
                inFile = chooser.getSelectedFile();

                fos = new FileOutputStream(inFile);             
                dos = new DataOutputStream(fos);                   

                tableModel.saveTo(dos);                             // method in MyTableModel class

                dos.close();                                        
                fos.close(); 
                modificationsMade = false;                                       
            } 
            catch (IOException o)
            {
                JOptionPane.showMessageDialog(this, "Error saving file!");
            }
        }
    } // end save as
    
    // ======================================================================================

    //  delete a JList item 
    public void delete() 
    {
        int[] selectedIndices = table.getSelectedRows();                                // array that holds the selected indices

        for (int k = selectedIndices.length - 1; k >= 0; k--)
            tableModel.removeElementAt(selectedIndices[k]);
        
        deleteAllJButton.setEnabled(tableModel.getRowCount() > 0);                      // jbutton
        deleteAllButton.setEnabled(tableModel.getRowCount() > 0);                       // menu item
        modificationsMade = true;                                                       // unsaved modifications have been made
    }
    
    // ======================================================================================

    //  deletes all rows 
    public void deleteAll()
    {
        int result = JOptionPane.showConfirmDialog(null, "Unsaved changes, delete all?", "Please Confirm", JOptionPane.YES_NO_OPTION);
        switch (result) 
        {
            case JOptionPane.YES_OPTION:

            for( int i = tableModel.getRowCount() - 1; i >= 0; i-- )
                tableModel.removeElementAt(i);

            deleteAllJButton.setEnabled(tableModel.getRowCount() > 0);              // jbutton
            deleteAllButton.setEnabled(tableModel.getRowCount() > 0);               // menu item

            modificationsMade = true;
            break;

            case JOptionPane.NO_OPTION:
            System.out.println("No");
            break;
        }
    }
    
    // ======================================================================================

    //  create a new JList item 
    public void newItem() 
    {
        int rowCount = tableModel.getRowCount();

        WorkOrderJDialog add = new WorkOrderJDialog(this);

        if(tableModel.getRowCount() > rowCount)
        {
            deleteAllJButton.setEnabled(tableModel.getRowCount() > 0);              // jbutton
            deleteAllButton.setEnabled(tableModel.getRowCount() > 0);               // menu item
            modificationsMade = true;                                               // unsaved modifications have been made
        }
    }
    
    // ======================================================================================

    // print the JList
    public void print()
    {
        try
        {
            table.print();
        }
        catch(PrinterException ee)
        {
            System.out.println("Caught a printer exception.");
            ee.printStackTrace();
        }
    }
    
    // ======================================================================================

    // mark the work order as completed using today's date
    public void itemCompleted()
    {
        WorkOrder selectedOrder = tableModel.getItemAt(table.rowAtPoint(mousePosition));
            if(selectedOrder.dateFulfilled != 0)
            {
                int result = JOptionPane.showConfirmDialog(null, "Replace date with current date?", "Please Confirm", JOptionPane.YES_NO_OPTION);
                switch (result) 
                {
                    case JOptionPane.YES_OPTION:
                    selectedOrder.dateFulfilled = Calendar.getInstance().getTimeInMillis();
                    modificationsMade = true;
                    break;
                    case JOptionPane.NO_OPTION:
                    System.out.println("No");
                    break;
                }
            }
                
    }
    
    // ======================================================================================

    public void setupMainFrame() 
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        setSize(80 * d.width / 100, 60 * d.height / 100);                   // 60 and 50 is the screen percentage to take up
        setLocation(d.width / 8, d.height / 4);                  

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("Work Order");                                             // for the title bar 

        setVisible(true);
    }
    
    // ======================================================================================

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getSource() == table.getSelectionModel())                     // enable the delete and edit buttons
        {
            deleteButton.setEnabled(table.getSelectedRows().length >= 0);
            deleteJButton.setEnabled(table.getSelectedRows().length >= 0);
            editButton.setEnabled(table.getSelectedRows().length == 1);
            editJButton.setEnabled(table.getSelectedRows().length == 1);
        }
        
    }
    
    // ======================================================================================

    public void ReplaceItem(WorkOrder newOrder, int index)
    {
        tableModel.ReplaceItem(newOrder, index);
    }
    
    // ======================================================================================

    public void AddItem(WorkOrder workOrder)
    {
        tableModel.addElement(workOrder);
    }
    
    // ======================================================================================

    public void editItem()
    {
        WorkOrderJDialog editDialog;
        WorkOrder editedOrder;

        editedOrder = tableModel.getElement(table.convertRowIndexToModel(table.rowAtPoint(mousePosition)));

        // editedOrder = tableModel.getDataAt(table.getSelectedRow());
        editDialog = new WorkOrderJDialog(this, editedOrder, table.getSelectedRow());
        modificationsMade = true;
    }
    
    // ======================================================================================

    TableColumnModel getColumnModel()
    {
       TableColumn col;
       DefaultTableColumnModel colModel;

       colModel = new DefaultTableColumnModel();

       col = new TableColumn(0);       // 0 is the column index
       col.setPreferredWidth(10);
       col.setMinWidth(10);
       col.setHeaderValue("Name");
       colModel.addColumn(col);

       col = new TableColumn(1);
       col.setPreferredWidth(10);
       col.setMinWidth(10);
       col.setHeaderValue("Department");
       colModel.addColumn(col);

       col = new TableColumn(2);
       col.setPreferredWidth(100);
       col.setMinWidth(100);
       col.setHeaderValue("Description");
       colModel.addColumn(col);

       col = new TableColumn(3);
       col.setPreferredWidth(5);
       col.setMinWidth(5);
       col.setHeaderValue("Billing Rate");
       colModel.addColumn(col);

       col = new TableColumn(4);
       col.setPreferredWidth(10);
       col.setMinWidth(20);
       col.setHeaderValue("Date Requested");
       colModel.addColumn(col);

       col = new TableColumn(5);
       col.setPreferredWidth(10);
       col.setMinWidth(20);
       col.setHeaderValue("Date Fulfilled");
       colModel.addColumn(col);

       return colModel;
    }

    // ======================================================================================

    public void mouseClicked(MouseEvent e)
    {
        maybeShowPopup(e);

        if(e.getClickCount() == 2)                      // if the user double clicks - edit
        {
            editButton.doClick();
        }
    }
    
    // ======================================================================================

    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    } 

    //======================================================================================

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    //======================================================================================

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //======================================================================================

    void maybeShowPopup(MouseEvent e)
    {
        if(e.isPopupTrigger())
        {
            mousePosition = e.getPoint();
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    // ======================================================================================

    public void dragEnter(DropTargetDragEvent dtde)
    {
        mainPanel.setBackground(Color.GRAY);
    }

    // ======================================================================================

    public void dragExit(DropTargetEvent dte)
    {
        mainPanel.setBackground(Color.LIGHT_GRAY);
    }

    // ======================================================================================

    public void drop(DropTargetDropEvent dtde)
    {
        java.util.List<File> fileList;
        Transferable transferableData;

        mainPanel.setBackground(Color.WHITE);

        transferableData = dtde.getTransferable();

        // if modifications have been made and not saved
        if (modificationsMade == true)
        {
            int result = JOptionPane.showConfirmDialog(null, "Unsaved changes, replace without saving?", "Please Confirm", JOptionPane.YES_NO_OPTION);
            switch (result) 
            {
                // ------------ case 1 - yes -----------------------------------------------------
                case JOptionPane.YES_OPTION:
                try
                {
                    if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                    {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        
                        fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
        
                        for( int i = tableModel.getRowCount() - 1; i >= 0; i-- )
                            tableModel.removeElementAt(i);                                    // in order to reset the file before loading
        
                        inFile = fileList.get(0);                       // get the first file (if multiple are selected)
        
                        fis = new FileInputStream(inFile);             
                        dis = new DataInputStream(fis);                 
        
                        tableModel.loadFrom(dis);                       // call the method in the MyTableModel
        
                        dis.close();                                    // close the input stream
                        fis.close();                                    // close the file input stream
        
                        deleteAllJButton.setEnabled(true);              // jbutton
                        deleteAllButton.setEnabled(true);               // menu item
    
                        modificationsMade = false;
    
                        tableModel.fireTableDataChanged();
                    }
    
                    else
                        System.out.println("File list flavor not supported.");
                }
    
                catch(UnsupportedFlavorException ufe)
                {
                    System.out.println("Unsupported flavor found!");
                    ufe.printStackTrace();
                }
        
                catch(IOException ioe)
                {
                    System.out.println("IOException found getting transferable data!");
                    ioe.printStackTrace();
                }
    
                break;
    
                // ------- case 2 - no -----------------------------------------------------------------
                case JOptionPane.NO_OPTION:
                System.out.println("No");
                break;
            }
        }

        // if there haven't been any modifications made
        else
        {
            try
            {
                if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
        
                    fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
        
                    for( int i = tableModel.getRowCount() - 1; i >= 0; i-- )
                        tableModel.removeElementAt(i);                                    // in order to reset the file before loading
        
                    inFile = fileList.get(0);                       // get the first file (if multiple are selected)
        
                    fis = new FileInputStream(inFile);             
                    dis = new DataInputStream(fis);                 
        
                    tableModel.loadFrom(dis);                       // call the method in the MyTableModel
        
                    dis.close();                                    // close the input stream
                    fis.close();                                    // close the file input stream
        
                    deleteAllJButton.setEnabled(true);              // jbutton
                    deleteAllButton.setEnabled(true);               // menu item
    
                    modificationsMade = false;
    
                    tableModel.fireTableDataChanged();
                }
    
                else
                    System.out.println("File list flavor not supported.");
            }
    
            catch(UnsupportedFlavorException ufe)
            {
                System.out.println("Unsupported flavor found!");
                ufe.printStackTrace();
            }
        
            catch(IOException ioe)
            {
                System.out.println("IOException found getting transferable data!");
                ioe.printStackTrace();
            }
    
        }
    }

    // ======================================================================================

    public void dropActionChanged(DropTargetDragEvent dtde) {}
    public void dragOver(DropTargetDragEvent dtde) {}

    // ======================================================================================

} // end class