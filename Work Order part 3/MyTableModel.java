// Abigail McIntyre
// Work Order Project
// 10/16/2021

// a subclass of TableModel which "has-a" pointer to an instance of the DefaultListModel subclass

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.table.AbstractTableModel;

class MyTableModel extends AbstractTableModel
                    implements DataManager 
{
    WorkOrderListModel list;

    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    FileOutputStream fos;

    JFileChooser chooser;                                   // a file chooser
    File inFile;                                            // holds the file name of the chosen file

    // =================================================================================================================

    public MyTableModel()                                   // default constructor
    {
        list = new WorkOrderListModel();
    }

    // =================================================================================================================
    
    public void addElement(WorkOrder data)                  // to add an element to the list
    {
        list.addElement(data);
        fireTableDataChanged();
    }

    // =================================================================================================================

    public void removeElementAt(int index)                  // to remove an element at a specific index
    {
        list.removeElementAt(index);
        fireTableDataChanged();
    }

    // =================================================================================================================

    public WorkOrder getDataAt(int index)                   // returns the data at the specified index
    {
        return (WorkOrder)list.getElementAt(index);
    }

    // =================================================================================================================

    public WorkOrder getItemAt(int index)                   // returns the element at the specified position in the list
    {
        return list.get(index);
    }

    // =================================================================================================================

    public int getRowCount()                                // returns the number of rows in the list
    {
        return list.getSize();
    }

    // =================================================================================================================

    public int getColumnCount()                             // returns the number of columns in the list
    {
        return 6;
    }

    // =================================================================================================================

    @Override
    public Object getValueAt(int row, int col)              // returns the data in the specified cell in the list
    {
        WorkOrder data;
        SimpleDateFormat dateFormatter;

        data = getDataAt(row);
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

        if(col == 0)
            return data.name;

        else if(col == 1)
            return data.department;

        else if(col == 2)
            return data.description;
        
        else if(col == 3)
            return data.billingRate;

        else if(col == 4)
            return dateFormatter.format(data.dateRequested);

        else if(col == 5)
            return dateFormatter.format(data.dateFulfilled);

        else
        {
            System.out.println("Unexpected error in getValueAt() of MyList class.");
            System.out.println("No field " + col + " exists.");
            System.exit(1);
            return null;
        }
    }

    // =================================================================================================================

    public Class getColumnClass(int col)                    // returns the class type for each column
    {
        if(col == 0)
            return String.class;

        else if(col == 1)
            return String.class;
        
        else if(col == 2)
            return String.class;

        else if(col == 3)
            return Double.class;
        
        else if(col == 4)
            return String.class;
        
        else 
            return String.class;
    }

    // =================================================================================================================

    public void saveTo(DataOutputStream dos)                // save to a file (from interface)
    {
        list.saveTo(dos);
    }

    // =================================================================================================================

    public void loadFrom(DataInputStream dis)               // load from a file (from interface)
    {
        list.load(dis);
    }

    // =================================================================================================================

    public void AddItem(WorkOrder workOrder)                // to add an item to the work order
    {
        addElement(workOrder);
    }

    // =================================================================================================================

    public void ReplaceItem(WorkOrder workOrder2, int index)    // to replace an item in the work order
    {
        list.set(index, workOrder2);
        fireTableDataChanged();
    }

    // =================================================================================================================

    public WorkOrder getElement(int row)
    {
        return list.get(row);
    }

    // =================================================================================================================

} // end WorkOrderMyTableModel class