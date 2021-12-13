// Abigail McIntyre
// Work Order Project
// 10/16/2021

// a subclass of DefaultListModel that holds a list of work orders. Has a "store" method to store itself on a DataOutputStream and a 
// constructor to load itself from a DataInputStream

import java.io.*;
import javax.swing.DefaultListModel;

public class WorkOrderListModel extends DefaultListModel<WorkOrder> 
{

    // =================================================================================================================

    public void load(DataInputStream dis)                                           // to load from a file
    {
        try
        {
            while(dis.available() > 0)                                              // while there's still data
            {
                addElement(new WorkOrder(dis));                                     // adds elements to a new work order
            }

            dis.close();                                                            // close the file
        } 
        catch (IOException e)
        {
            System.out.println("Error reading from file");
        }
    }

    // =================================================================================================================

    public void saveTo(DataOutputStream dos)                                         // to save from a file
    {
        WorkOrder write;

        try 
        {
            for(int i = 0; i < size(); i++)                                         // while i is less that the number of elements in the list
            {
                write = get(i);                                                     // get the element at index i
                write.writeTo(dos);                                                 // write each element to the file
            }

            dos.close();                                                            // close the output stream
        } 
        catch(IOException e)
        {
            System.out.println("Error while writing to the file");
        }
    }

    // =================================================================================================================

}