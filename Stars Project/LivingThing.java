// Abigail McIntyre
// Project 3 - Moving Stars
// Due 11/11/2021
// an abstract class that represents an object that can move in two dimensions

import java.awt.*;
import java.util.Vector;
import javax.swing.JPanel;

abstract class LivingThing 
{
    int xPosition, yPosition;                       // holds the xPosition and yPosition of the object                                      
    int targetX;                                    // holds the x coordinates of the mouse position
    int targetY;                                    // holds the y coordinates of the mouse position
    int outerRadius;                                // the outer radius of the star, so the length from the middle to the outer point
    int innerRadius;                                // the inner radius of the star, so the length from the middle to the inner point
    int lifetime;                                   // holds the lifetime value of each star
    int maxLifetime;                                // holds the maximum lifetime of the star
    int numPointyThings;                            // the number of spikes on the star
    int opacity;                                    // the opacity of the object

    int[] xCoord, yCoord;                           // the array of x coordinates and y coordinates on the star                                  

    static int randomColorNumber;                   // holds the number for the color that each star will be
    static int red;                                 // holds the red value for the color of the star
    static int green;                               // holds the green value for the color of the star
    static int blue;                                // holds the blue value for the color of the star

    double orientationAngle;                        // the angle in which the object will be rotating
    double xSpeed, ySpeed;                          // the speed in which the object is moving across the x-axis and y-axis                                
    double alpha;                                   // the angle in which the star points will be drawn
    double deltaAlpha;                              // the second angle for the second line of the star point
    double xAcceleration, yAcceleration;
    double angularSpeed;                            // the speed in which the object is rotating
    double angularAcceleration;                     // the acceleration for the speed in which the object is rotating
    double timeScalar;                              // the value comes from the slider, changes deltaScaledMillis
    double distFromMouse;                           // holds the distance of the stars from the mouse when chaseMouse is true
    double gravityPercent;                          // the percentage of gravity that will be multiplied by the ySpeed to diminish bounces
    double conservationOfEnergy;                    // subtracted from the percent gravity with each bounce

    boolean idleMode;                               // the mode for the program in which the stars stay in place and rotate - the variable checks whether its selected or not
    boolean chaseMouse;                             // the mode in which the stars will chase the mouse
    boolean fallBounce;                             // the mode in which existing stars will fall to the ground with diminishing bounces until they settle
    boolean randomMotion;                           // the mode in which stars will just randomly bounce around the screen
    boolean gravity;                                // to check if the gravity button is selected

    Color randomColor;                              // the color for each object                            
    JPanel drawingPanel;                            // the panel with the moving objects

    Vector<LivingThing> things;                     // the vector of objects
    Vector<MortalityListener> mortalityListeners;   // the vector of mortality listeners

    // ============================================================================================================================

    public LivingThing()
    {
        conservationOfEnergy = .01;
        timeScalar = 50;
        gravityPercent = 1;
        opacity = 255;
        maxLifetime = 5000;
        mortalityListeners = new Vector<MortalityListener>();
    }

    // ============================================================================================================================

    // calls all the methods to update everything in the object every time the timer goes off
    void update(DrawingPanel panel)
    {
        // --------------------------------------------------------------------------
        if(idleMode == true)
        {
            updateVitality();
            double deltaScaledMillis = (timeScalar / 100);
            updateAngularVelocity(deltaScaledMillis);
            updateAngle(deltaScaledMillis);
            updateOrientation(deltaScaledMillis);
        }

        // --------------------------------------------------------------------------

        else if(randomMotion == true)
        {
            updateVitality();
            double deltaScaledMillis = (timeScalar / 100);
            updateLinearVelocity(deltaScaledMillis);
            reflectOffVerticalWall();
            reflectOffHorizontalWall();
            updateCurrentPosition(deltaScaledMillis);
            updateAngularVelocity(deltaScaledMillis);
            updateAngle(deltaScaledMillis);
            updateOrientation(deltaScaledMillis);
        }

        // --------------------------------------------------------------------------

        else if(fallBounce == true)
        {
            xAcceleration = 0;
            yAcceleration = 1.2;

            updateVitality();
            double deltaScaledMillis = (timeScalar / 100);
            updateLinearVelocity(deltaScaledMillis);
            reflectOffVerticalWall();
            reflectOffHorizontalWall();
            updateCurrentPosition(deltaScaledMillis);
            updateAngularVelocity(deltaScaledMillis);
            updateAngle(deltaScaledMillis);
            updateOrientation(deltaScaledMillis);
        }

        // --------------------------------------------------------------------------

        else // chase mouse
        {
            updateVitality();
            double deltaScaledMillis = (timeScalar / 100);
            mouseChase(deltaScaledMillis);
            reflectOffVerticalWall();
            reflectOffHorizontalWall();
            updateCurrentPosition(deltaScaledMillis);
            updateAngularVelocity(deltaScaledMillis);
            updateAngle(deltaScaledMillis);
            updateOrientation(deltaScaledMillis);
        }

        // --------------------------------------------------------------------------
    }

    // ============================================================================================================================

    public void mouseChase(double deltaScaledMillis)
    {
        // --------------------------------------------------------------------------

        if ((xPosition + yPosition) > (targetX + targetY) 
        || (xPosition + yPosition) < (targetX + targetY))
        {
            xSpeed = ((targetX - xPosition) * .05);
            ySpeed = ((targetY - yPosition) * .05);
        }

        // --------------------------------------------------------------------------

        // if the target is out of bounds, set it to within
        if(targetX <= outerRadius || targetX >= drawingPanel.getSize().getWidth() - outerRadius)
        {
            // if the target is greater than or equal to where the square would hit the edge of the screen, reset the target to the edge
            if(targetX >= (drawingPanel.getWidth() - outerRadius))
            targetX = (drawingPanel.getWidth() - outerRadius);

            // if the target is less than or equal to zero, so off the screen, reset it to zero
            else if(targetX <= outerRadius)
                targetX = outerRadius + 2;
        }

        // --------------------------------------------------------------------------

        if(targetY <= outerRadius || targetY >= drawingPanel.getSize().getHeight() - outerRadius)
        {
            // if the target is greater than or equal to where the square would hit the edge of the screen, reset the target to the edge
            if(targetY >= (drawingPanel.getHeight() - outerRadius))
                targetY = (drawingPanel.getHeight() - outerRadius);

            // if the target is less than or equal to zero, so off the screen, reset it to zero
            else if(targetY <= outerRadius)
            {
                targetY = outerRadius + 2;
            }
        }

        // --------------------------------------------------------------------------
    }

    // ============================================================================================================================

    // update the current position of the x and y values
    void updateCurrentPosition(double deltaScaledMillis)
    {
        xPosition = (int)(xPosition + deltaScaledMillis * xSpeed);
        yPosition = (int)(yPosition + deltaScaledMillis * ySpeed);
    }

    // ============================================================================================================================

    // reflects the object to go the opposite way when it hits the side walls
    void reflectOffVerticalWall()
    {
        // if the xPosition is less than or equal to 0, so off the screen
        // if the xPosition is greater than or equal to the edge of the screen minus the size of the object
        if(xPosition <= outerRadius || xPosition >= drawingPanel.getSize().getWidth() - outerRadius)
        {
            // if the position is greater than or equal to where the square would hit the edge of the screen, reset the position to the edge
            if(xPosition >= (drawingPanel.getWidth() - outerRadius))
                xPosition = (drawingPanel.getWidth() - outerRadius);

            // if the positin is less than or equal to zero, so off the screen, reset it to zero
            else if(xPosition <= outerRadius)
                xPosition = outerRadius + 2;

            // change the object to go the other way
            xSpeed *= -1;
        }
    }

    // ============================================================================================================================

    // reflects the object to go the opposite way when it hits the top walls
    void reflectOffHorizontalWall()
    {
        // --------------------------------------------------------------------------

        // if the yPosition is less than or equal to 0, so off the screen
        // if the yPosition is greater than or equal to the edge of the screen minus the size of the object
        if(yPosition <= outerRadius || yPosition >= drawingPanel.getSize().getHeight() - outerRadius)
        {
            // if the position is greater than or equal to where the square would hit the edge of the screen, reset the position to the edge
            if(yPosition >= (drawingPanel.getHeight() - outerRadius))
                yPosition = (drawingPanel.getHeight() - outerRadius);

            // if the position is less than or equal to zero, so off the screen, reset it to zero
            else if(yPosition <= outerRadius)
            {
                yPosition = outerRadius + 2;
            }

            // --------------------------------------------------------------------------------------------------------------------

            // if the fall n bounce mode is on and the percentage of gravity isn't negative, decrement the percent gravity and set the speed
            if (fallBounce == true && gravityPercent > 0)
            {
                gravityPercent -= conservationOfEnergy;
                ySpeed = ySpeed * gravityPercent;
            }

            // if the gravity percent has reached zero, stop bouncing
            else if (gravityPercent <= 0)
            {
                ySpeed = ySpeed * gravityPercent;
                yAcceleration = 0;
            }

            // change the object to go the other way
            ySpeed *= -1;
        }

        // --------------------------------------------------------------------------
    }

    // ============================================================================================================================

    void updateLinearVelocity(double deltaScaledMillis)
    {
        xSpeed = xSpeed + deltaScaledMillis * xAcceleration;
        ySpeed = ySpeed + deltaScaledMillis * yAcceleration;
    }

    // ============================================================================================================================

    void updateOrientation(double deltaScaledMillis)
    {
        double deltaAlpha = Math.PI / numPointyThings;

        for(int i = 0; i < numPointyThings * 2; i++)
        {
            if(i % 2 == 0)
            {
                xCoord[i] = (int)(xPosition + innerRadius * Math.cos(alpha));
                yCoord[i] = (int)(yPosition + innerRadius * Math.sin(alpha));
            }

            else
            {
                xCoord[i] = (int)(xPosition + outerRadius * Math.cos(alpha));
                yCoord[i] = (int)(yPosition + outerRadius * Math.sin(alpha));
            }

            alpha += deltaAlpha;
        }
    }

    // ============================================================================================================================

    void updateAngle(double deltaScaledMillis)
    {
        alpha = orientationAngle + angularSpeed * deltaScaledMillis;
    }

    // ============================================================================================================================

    void updateAngularVelocity(double deltaScaledMillis)
    {
        angularSpeed = angularSpeed + deltaScaledMillis * angularAcceleration;
    }

    // ============================================================================================================================
    
    // updates how long the stars been alive, kills it if its been alive long enough
    void updateVitality()
    {
        lifetime--;

        if(lifetime <= 0)
        {
            // a death event
            MortalityEvent mortalityEvent = new MortalityEvent(this, true);

            for(int i = 0; i < mortalityListeners.size(); i++)
            {
                mortalityListeners.get(i).lifeOccured(mortalityEvent);
            }
        }
    }

    // ============================================================================================================================

    void addMortalityListener(MortalityListener m)
    {
        mortalityListeners.add(m);
    }

    // =====================================================================================================================

    abstract void draw(Graphics2D g);
    
}
