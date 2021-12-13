// Abigail McIntyre
// Project 3 - Moving Stars
// Due 11/11/2021
// a JPanel subclass that:
// a.) has a reference to a Vector of LivingThings (where LivingThings is the abstract class)
// b.) overrides the paintComponent(...)
//      i.) clears the panel
//     ii.) uses a loop to call .draw(...) on each of the LivingThings in the vector

import java.awt.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;
import java.awt.Toolkit;

public class DrawingPanel extends JPanel
{
    LivingThing thing;
    Color backgroundColor;
    Graphics2D g2D;
    Vector<LivingThing> livingThing;

    // ============================================================================================================================

    public DrawingPanel(Vector<LivingThing> livingThing)
    {
        this.livingThing = livingThing;

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        setPreferredSize(new Dimension(d.width, 70 * d.height / 100));              // set the dimensions of the panel

        backgroundColor = new Color(250, 229, 207);                                 // make the background color
        setBackground(backgroundColor);                                             // set the background color
    }

    // ============================================================================================================================

    @Override
    protected void paintComponent(Graphics g)
    {
        if(StarsJFrame.clearPaintComponent.isSelected() == true)
            super.paintComponent(g);                                                // clear the screen
        
        g2D = (Graphics2D) g;                                                       // set the object to a 2D object

        for(int i = 0; i < livingThing.size(); i++)                                 // for each object in the vector
        {
            thing = livingThing.get(i);                                             // get the object
            thing.draw(g2D);                                                        // draw the object
        }
    }

    // ============================================================================================================================
    
}
