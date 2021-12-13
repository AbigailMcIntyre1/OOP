// Abigail McIntyre
// Project 3 - Moving Stars
// Due 11/11/2021

public class MortalityEvent 
{
    LivingThing thing;
    boolean isDeathEvent;
    
    MortalityEvent(LivingThing thing, boolean isDeathEvent)
    {
        this.thing = thing;
        this.isDeathEvent = isDeathEvent;
    }

    // returns the source of the mortality event
    LivingThing getSource()
    {
        return thing;
    }
}