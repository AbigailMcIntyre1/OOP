// Abigail McIntyre
// Project 3 - Moving Stars
// Due 11/11/2021
// subclass of LivingThing that can draw a star

import java.awt.*;
import java.util.Random;
import javax.swing.JPanel;

public class DefaultLivingThing extends LivingThing
{             
    // ============================================================================================================================

    // default constructor which will initialize the colors
    DefaultLivingThing(JPanel panel)
    {
        drawingPanel = panel;
    }

    // ============================================================================================================================

    // a static method for obtaining a random instance of the class which will be moving at a random speed in
    // a random direction with a random angular velocity and no linear acceleration and no rotational acceleration

    public static LivingThing getRandom(JPanel drawingPanel)
    {
        Random rand = new Random();                                          // initialize the random number

        DefaultLivingThing thing = new DefaultLivingThing(drawingPanel);     // create a living thing

        thing.xSpeed = rand.nextInt(26) - 15;                                // a speed from -10 to 10
        thing.ySpeed = rand.nextInt(26) - 15;                                // a speed from -10 to 10

        thing.xAcceleration = 0;                                             // set the xAcceleration
        thing.yAcceleration = 0;                                             // set the yAcceleration

        thing.orientationAngle = rand.nextFloat() * 360;                     // set the angle in which the star will be rotated
        thing.angularAcceleration = rand.nextFloat() / 2 + .05;              // the acceleration in which the star will rotate      
        thing.angularSpeed = (2.0 * Math.PI / 100.0);                        // set the speed in which is rotates

        thing.numPointyThings = rand.nextInt(10 - 6) + 6;                    // the number of points on the star - between 10 and 6

        thing.xCoord = new int[thing.numPointyThings * 2];                   // initialize the array with the number of points * 2 (for the inner radius points)
        thing.yCoord = new int[thing.numPointyThings * 2];                   // initialize the array with the number of points * 2 (for the inner radius points)

        thing.outerRadius = rand.nextInt(50 - 20) + 20;                      // set the outer radius to a random integer between 50 and 20
        thing.innerRadius = thing.outerRadius / 2;                           // set the inner radius to half the outer one

        thing.xPosition = rand.nextInt(drawingPanel.getWidth()) + thing.outerRadius;        // set the xPosition to somewhere in the screen width 
        thing.yPosition = rand.nextInt(drawingPanel.getHeight()) + thing.outerRadius;       // set the yPosition to somewhere in the screen height 

        LivingThing.randomColorNumber = rand.nextInt(6 - 1) + 1;             // generate a random number for the color between 1 and 6
        thing.randomColor = randomColor(randomColorNumber);                  // set the color

        thing.targetX = drawingPanel.getWidth() / 2;
        thing.targetY = drawingPanel.getHeight() / 2;

        return thing;
    }

     // ============================================================================================================================

     // predetermined set of colors that are chosen based on a random number between 1 and 6
     static Color randomColor(int randNum)
     {
        if (randNum == 1)
        {
            Color randColor = new Color(232, 183, 97, 255);
            return randColor;
        }

        if (randNum == 2)
        {
            Color randColor = new Color(224, 147, 65, 255);
            return randColor;
        }

        if (randNum == 3)
        {
            Color randColor = new Color(201, 101, 18, 255);
            return randColor;
        }

        if (randNum == 4)
        {
            Color randColor = new Color(130, 36, 14, 255);
            return randColor;
        }

        if (randNum == 5)
        {
            Color randColor = new Color(145, 189, 180, 255);
            return randColor;
        }

        // default cause it was getting mad 
        Color randColor = new Color(145, 189, 180, 255);
        return randColor;

     }
    
    // ============================================================================================================================

    // a method to draw the objects
    @Override
    void draw(Graphics2D g)
    {
        // if the current lifetime is 25% of the maximum lifetime it has
        // and then if the opacity - 255 / lifetime is greater than 0, because I kept getting an error that it was out of range
        if((lifetime <= 0.25 * maxLifetime) && (opacity - 255 / lifetime >= 0))
        {
            // opacity equals opacity minus the max opacity divided by lifetime
            opacity -= 255 / lifetime;
        }

        // get the colors that were used on the star and then change the opacity based on 
        randomColor = new Color(randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue(), opacity);

        g.setColor(randomColor);
        // g.drawPolygon(xCoord, yCoord, numPointyThings * 2);
        g.fillPolygon(xCoord, yCoord, numPointyThings * 2);
    }

    // ============================================================================================================================

}
