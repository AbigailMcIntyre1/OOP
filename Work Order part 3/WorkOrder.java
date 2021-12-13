// Abigail McIntyre
// Work Order Project
// 10/16/2021

// represents one data record, is "smart", knows how to store itself on a DataOutputStream, how to construct itself from a DataInputStream, 
// and how to produce an instance of itself using random data

import java.text.SimpleDateFormat;
import java.util.Random;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class WorkOrder 
{
    public String name;
    public String department;
    public String description;
    
    public long dateRequested;
    public long dateFulfilled;
    public float billingRate;
    static Random rand = new Random();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    //=========== Initializing constructor ================                         a.) in project instructions - example in 9.0
    
    public WorkOrder(String name, String department, Date requested, Date fulfilled, String description, float billingRate)
    { 
        this.name = name;
        this.department = department;
        this.dateRequested = requested.getTime();
        this.dateFulfilled = fulfilled.getTime();
        this.description = description;
        this.billingRate = billingRate;
    }

    //=========== Constructing from a stream ==============                         d.) in project instructions - example in 9.0

    public WorkOrder(DataInputStream dis)
    {
        try
        {
            // read the user input into a data input stream
            this.name = dis.readUTF();                              
            this.department = dis.readUTF();
            this.description = dis.readUTF();
            this.billingRate = dis.readFloat();
            this.dateRequested = dis.readLong();
            this.dateFulfilled = dis.readLong();
        } 
        catch (IOException e)
        {
            System.out.println("Error opening from file");
            System.out.println(e.getStackTrace());
        }
    }

    //========== construct a random record ================                         b.) in project instructions - example in 9.0

    public static WorkOrder getRandom()
    {
        String randName;
        String randDeparment;
        String randDescription;

        Date randDateRequested;
        Date randDateFulfilled;

        float randBillingRate;

        // set the random string's values
        final String[] randNames = {"Alec", "Abie", "Adie", "Lexie", "Devin"};
        final String[] randDepartments = {"SALES", "HARDWARE", "ELECTRONICS"};
        final String[] randDescriptions = {"Not fully sure what should go here.", "Because I'm not sure what a work order is.", 
                              "This is a description of a work order.", "Here's your work order.", "Insert description here."};

        WorkOrder tmpOrder;

        // get the random strings
        randName = randNames[rand.nextInt(randNames.length)];
        randDeparment = randDepartments[rand.nextInt(randDepartments.length)];
        randDescription = randDescriptions[rand.nextInt(randDescriptions.length)];


        // **get random dates to add**

        // for testing purposes
        //int r = 20000;
        //randDateRequested = new Date(r + rand.nextInt(r));
        //randDateFulfilled = new Date((r + 3) + rand.nextInt(r));
        
        randDateFulfilled = new Date(Math.abs(System.currentTimeMillis() - rand.nextInt()));
        randDateRequested = new Date(Math.abs(System.currentTimeMillis() - rand.nextInt()));


        // get random billing rate
        randBillingRate = (float)(4.2 + rand.nextFloat() * (15.8 - 4.2));

        // set up the work order
        tmpOrder = new WorkOrder(randName, randDeparment, randDateRequested, randDateFulfilled, randDescription, randBillingRate);

        // return the new work order
        return tmpOrder;
    }
    //=====================================================


    // method which will dump all of the fields on the console (to facilitate debugging)           c.) in project instructions
    public void print()
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

        System.out.println("Work Order:");
        System.out.println("--------------------");                                                 // just to separate things a bit
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("Description: " + description);
        System.out.println("Billing Rate: " + billingRate);
        System.out.println("Date Requested: " + dateFormatter.format(dateRequested));              // format the dates with SimpleDateFormat
        System.out.println("Date Fulfilled: " + dateFormatter.format(dateFulfilled));
        System.out.println("--------------------");
    }

    // method which will store the class in a DataOutputStream                                      d.) in project instructions - example in 9.0
    public void writeTo(DataOutputStream dos)
    {
        try
        {
            dos.writeUTF(name);
            dos.writeUTF(department);
            dos.writeUTF(description);
            dos.writeFloat(billingRate);
            dos.writeLong(dateRequested);
            dos.writeLong(dateFulfilled);
        } 
        catch(IOException e)
        {
            System.out.println("Error saving to file");
        }
    }
    
    //=====================================================

    public String toString()
    {
        return "Name: " + this.name + "     Department: " + this.department + "     Description: " + this.description + 
                "     Billing Rate: " + this.billingRate + "     Date Requested: " +  formatter.format(this.dateRequested) + 
                "     Date Fulfilled: " + formatter.format(this.dateFulfilled);
    }

     //=====================================================

    public static int getFieldCount()
    {
        return 6;
    }

    //=====================================================
}