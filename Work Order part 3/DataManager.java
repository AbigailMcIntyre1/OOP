// Abigail McIntyre
// Work Order Project
// 10/16/2021

// an interface which requires the implementer to provide a method for adding a work order to a list, 
// and a method for replacing a work order in a list

public interface DataManager 
{
    public void AddItem(WorkOrder workOrder);
    public void ReplaceItem(WorkOrder workOrder2, int index);
}