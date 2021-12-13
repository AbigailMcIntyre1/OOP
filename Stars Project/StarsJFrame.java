// Abigail McIntyre
// Project 3 - Moving Stars
// Due 11/11/2021
// JFrame Subclass that:
// a.) constructs an empty Vector of Living Things     b.) constructs an instance of DrawingPanel
// c.) implements ActionListener                       d.) constructs a button panel with a button for adding a new LivingThing to the DrawingPanel's vector
// e.) constructs a javax.swing.Timer object with an action command and started near the end of the JFrame constructor
// f.) adds a new random instance of LivingThing to the Vector when the button is clicked (in ActionPerformed)
// g.) calls update() on each of the LivingThings in the Vector when a timer event is received (in ActionPerformed)

import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Vector;
import java.awt.*;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.GroupLayout.Alignment;
import java.awt.Color;
import javax.swing.JSlider;
import java.awt.BorderLayout;

public class StarsJFrame extends JFrame
                         implements ActionListener, ChangeListener, MortalityListener, MouseListener, MouseMotionListener
{
    DrawingPanel drawingPanel;                          // panel that will be repainted
    JPanel optionsPanel;                                // panel that holds all the buttons

    JButton addButton;                                  // adds a single object                            
    JButton exitButton;                                 // exits the program                            
    JButton clearButton;                                // clears all the stars from the screen                          
    JButton pauseButton;                                // pauses everything (stops the timer)
    JButton resumeButton;                               // resumes everything (resumes the timer)
    JButton addLotsButton;                              // adds the amount of objects that the user set the slider to
    JButton randomButton;                               // randomizes the speeds

    ButtonGroup group;                                  // a group for the program modes
    GroupLayout inputLayout;                            // the layout for all the buttons on the side of the screen

    JRadioButton idleButton;                            // the mode where the objects only rotate
    JRadioButton chaseMouseButton;                      // the mode where the objects chase the mouse
    JRadioButton fallBounceButton;                      // the mode where gravity will affect the objects
    JRadioButton randomMotionButton;                    // the objects will randomly bounce around the screen

    JLabel accelerationSliderLabel;                     // a label for the acceleration slider
    JLabel conservationEnergyLabel;                     
    JLabel animationSpeedLabel;
    JLabel lifetimeLabel;
    JLabel modesLabel;
    JLabel spacingLabel;                                // separates the buttons a little bit because it's empty
    JLabel amountToAddLabel;

    JSlider accelerationSlider;                         // slider for the deltaScaledMillis
    JSlider conservationEnergySlider;                   // slider for the amount subtracted from the percent each time the object hits the ground
    JSlider animationSpeedSlider;                       // slider that changes the amount of time that the timer waits between updates
    JSlider lifetimeSlider;                             // slider for the length of the lifetime of each object
    JSlider amountSlider;                               // slider for the amount of objects to be added when add lots is clicked
    Random rand;

    static JCheckBox clearPaintComponent;               // checkbox that turns clear paint component on and off

    Timer timer;                                        // timer that sends events every so many milliseconds
    Vector<LivingThing> livingThing;                    // a vector that holds LivingThings

    int delay;                                          // the variable that tells the timer how often to update (default is 10 but value comes from slider)
    int amountToAdd;                                    // holds the amount of objects to add (value comes from slider)                      

    boolean clearIsSelected;                            // holds if the clear paint component checkbox is selected

    // =====================================================================================================================

    StarsJFrame()
    {
        livingThing = new Vector<LivingThing>();                             // a vector that holds LivingThings
        clearIsSelected = true;                                              // set the clear paint component to default selected
        rand = new Random();

        // orange theme
        /*
        Color backgroundColor = new Color(224, 147, 65, 255);
        Color buttonColor = new Color(255, 198, 138);
        Color textColor = new Color(99, 48, 7);
        Color buttonTextColor = new Color(99, 48, 7);
        */

        // blue theme
        Color backgroundColor = new Color(145, 189, 180, 255);
        Color buttonColor = new Color(245, 255, 253);
        Color textColor = new Color(42, 71, 66);
        Color buttonTextColor = new Color(42, 71, 66);

        // -------- Panels ----------------------------------------------------------------------------------------------
        drawingPanel = new DrawingPanel(livingThing);                        // create the panel that will be repainted
        drawingPanel.addMouseListener(this);
        drawingPanel.addMouseMotionListener(this);
        optionsPanel = new JPanel();                                         // create the panel that will hold the buttons

        // -------- JButtons --------------------------------------------------------------------------------------------
        addButton = newJButton("Add", buttonTextColor, this, "ADD", buttonColor);
        addLotsButton = newJButton("Add lots", buttonTextColor, this, "ADD_LOTS", buttonColor);
        randomButton = newJButton("Random", buttonTextColor, this, "RANDOM", buttonColor);
        exitButton = newJButton("Exit", buttonTextColor, this, "EXIT", buttonColor);
        clearButton = newJButton("Clear", buttonTextColor, this, "CLEAR", buttonColor);
        pauseButton = newJButton("Pause", buttonTextColor, this, "PAUSE", buttonColor);
        resumeButton = newJButton("Resume", buttonTextColor, this, "RESUME", buttonColor);

        // -------- JCheckBox -----------------------------------------------------------------------------------------
        clearPaintComponent = new JCheckBox("Screen Clearing");
        clearPaintComponent.setForeground(textColor);
        clearPaintComponent.addActionListener(this);
        clearPaintComponent.setActionCommand("SCREEN_CLEARING");
        clearPaintComponent.setSelected(true);
        clearPaintComponent.setBackground(backgroundColor);

        // ------- Radio Buttons --------------------------------------------------------------------------------------------                                     
        idleButton = newJRadioButton("Idle", textColor, this, "IDLE", backgroundColor);
        chaseMouseButton = newJRadioButton("Chase Mouse", textColor, this, "CHASE_MOUSE", backgroundColor);
        fallBounceButton = newJRadioButton("Fall 'n Bounce", textColor, this, "FALL_BOUNCE", backgroundColor);
        randomMotionButton = newJRadioButton("Random Motion", textColor, this, "RANDOM_MOTION", backgroundColor);
        randomMotionButton.setSelected(true);

        // add them to a group so that only one can be selected at one time
        group = new ButtonGroup();
        group.add(idleButton);
        group.add(chaseMouseButton);
        group.add(fallBounceButton);
        group.add(randomMotionButton);

        // -------- JLabels -- ----------------------------------------------------------------------------------------------
        modesLabel = new JLabel("Program Modes: ");
        modesLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        modesLabel.setForeground(textColor);

        spacingLabel = new JLabel("  ");

        accelerationSliderLabel = new JLabel("Acceleration: ");
        accelerationSliderLabel.setForeground(textColor);

        conservationEnergyLabel = new JLabel("Conservation of Energy: ");
        conservationEnergyLabel.setForeground(textColor);

        animationSpeedLabel = new JLabel("Animation Speed: ");
        animationSpeedLabel.setForeground(textColor);

        lifetimeLabel = new JLabel("Lifetime: ");
        lifetimeLabel.setForeground(textColor);

        amountToAddLabel = new JLabel("Add Lots Amount: ");
        amountToAddLabel.setForeground(textColor);
        
        // -------- JSliders ----------------------------------------------------------------------------------------------
        accelerationSlider = new JSlider(1, 100, 50);
        accelerationSlider.addChangeListener(this);
        accelerationSlider.setBackground(backgroundColor);

        conservationEnergySlider = new JSlider(1, 20);
        conservationEnergySlider.addChangeListener(this);
        conservationEnergySlider.setBackground(backgroundColor);

        animationSpeedSlider = new JSlider(10, 100, 10);
        animationSpeedSlider.addChangeListener(this);
        animationSpeedSlider.setBackground(backgroundColor);
        delay = 10;

        lifetimeSlider = new JSlider(300, 5000, 2650);
        lifetimeSlider.addChangeListener(this);
        lifetimeSlider.setBackground(backgroundColor);

        amountSlider = new JSlider(2, 100, 50);
        amountSlider.addChangeListener(this);
        amountSlider.setBackground(backgroundColor);

        // ------- Layout -------------------------------------------------------------------------------------------------

        // layout for the panel
        inputLayout = new GroupLayout(optionsPanel);
        optionsPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        // ------- horizontal group -------------
        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(modesLabel)
                    .addComponent(idleButton)
                    .addComponent(chaseMouseButton)
                    .addComponent(fallBounceButton)
                    .addComponent(randomMotionButton)
                    .addComponent(clearPaintComponent)
                    .addComponent(spacingLabel)
                    .addComponent(amountToAddLabel)
                    .addComponent(amountSlider)
                    .addComponent(animationSpeedLabel)
                    .addComponent(animationSpeedSlider)
                    .addComponent(lifetimeLabel)
                    .addComponent(lifetimeSlider)
                    .addComponent(accelerationSliderLabel)
                    .addComponent(accelerationSlider)
                    .addComponent(conservationEnergyLabel)
                    .addComponent(conservationEnergySlider)
                    .addComponent(animationSpeedLabel)
                    .addComponent(animationSpeedSlider)
                    .addComponent(addButton)
                    .addComponent(addLotsButton)
                    .addComponent(pauseButton)
                    .addComponent(resumeButton)
                    .addComponent(clearButton)
                    .addComponent(randomButton));
        inputLayout.setHorizontalGroup(hGroup);

        // --------- vertical group --------------
        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(modesLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(idleButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(chaseMouseButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(fallBounceButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(randomMotionButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                addComponent(clearPaintComponent));
        inputLayout.setVerticalGroup(vGroup);
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(spacingLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(spacingLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(amountToAddLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(amountSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(animationSpeedLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(animationSpeedSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(lifetimeLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(lifetimeSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(accelerationSliderLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(accelerationSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(conservationEnergyLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(conservationEnergySlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(animationSpeedLabel));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(animationSpeedSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(addButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(addLotsButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(pauseButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(resumeButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(clearButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(randomButton));
        optionsPanel.setBackground(backgroundColor);
        
        // add the panels
        add(optionsPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);

        // -------- Timer ----------------------------------------------------------------------------------------------
        timer = new Timer(delay, new ActionListener() 
        {
            @Override
            public void actionPerformed(final ActionEvent e) 
            {
                // for every object in the vector, call update
                for(int i = 0; i < livingThing.size(); i++)
                {
                    LivingThing thing = livingThing.get(i);
                    thing.update(drawingPanel);
                }

                drawingPanel.repaint();         // then repaint the screen
            }
        });

        // start the timer
        timer.start();      

        // set everything up
        setupMainFrame();
    }

    // =====================================================================================================================

    public void actionPerformed(ActionEvent e) 
    {
        // -------------------------------------------------------------------

        // adds objects to the vector
        if (e.getActionCommand().equals("ADD"))                                     // adds one object
        {
            LivingThing livingThing1 = DefaultLivingThing.getRandom(drawingPanel);
            livingThing1.addMortalityListener(this);
            livingThing1.lifetime = lifetimeSlider.getValue();
            livingThing1.conservationOfEnergy = conservationEnergySlider.getValue() / 100.0;

            if(idleButton.isSelected())
                livingThing1.idleMode = true;
            
            if(randomMotionButton.isSelected())
                livingThing1.randomMotion = true;

            if(fallBounceButton.isSelected())
                livingThing1.fallBounce = true;

            if(chaseMouseButton.isSelected())
            {
                livingThing1.chaseMouse = true;
                livingThing1.xPosition = drawingPanel.getWidth() / 2;
                livingThing1.yPosition = drawingPanel.getHeight() / 2;
            }

            livingThing.add(livingThing1);
        }

        // -------------------------------------------------------------------

        if (e.getActionCommand().equals("ADD_LOTS"))                                // adds however many objects the slider has selected
        {
            amountToAdd = amountSlider.getValue();

            for (int x = 0; x < amountToAdd; x++)
            {
                LivingThing livingThing1 = DefaultLivingThing.getRandom(drawingPanel);
                livingThing1.addMortalityListener(this);
                livingThing1.lifetime = lifetimeSlider.getValue();
                livingThing1.conservationOfEnergy = conservationEnergySlider.getValue() / 100.0;

                if(idleButton.isSelected())
                    livingThing1.idleMode = true;

                if(randomMotionButton.isSelected())
                    livingThing1.randomMotion = true;

                if(fallBounceButton.isSelected())
                    livingThing1.fallBounce = true;
    
                if(chaseMouseButton.isSelected())
                {
                    livingThing1.chaseMouse = true;
                    livingThing1.xPosition = drawingPanel.getWidth() / 2;
                    livingThing1.yPosition = drawingPanel.getHeight() / 2;
                }

                livingThing.add(livingThing1);
            }
        }

        // -------------------------------------------------------------------

        if (e.getActionCommand().equals("RANDOM"))                                  // randomly changes the x and y speed
        {
            // for each object in the vector, change the speed to a random speed
            for(int i = 0; i < livingThing.size(); i++)
            {
                LivingThing thing = livingThing.get(i);
                Random rand = new Random();

                thing.xSpeed = rand.nextInt(51) - 25;                                // a speed from -25 to 25
                thing.ySpeed = rand.nextInt(51) - 25;                                // a speed from -25 to 25
            }
        }

        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("CLEAR"))                                    // clears all objects from the vector
           livingThing.clear();
        
        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("PAUSE"))                                    // pauses the timer
            timer.stop();

        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("RESUME"))                                   // resumes the timer
            timer.start();

        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("SCREEN_CLEARING"))                          // turns clear paint component on and off
        {
            if(clearPaintComponent.isSelected())
                clearIsSelected = true;
            
            if(!clearPaintComponent.isSelected())
                clearIsSelected = false;
        }

        // ------------------------------------------------------------------

        if(e.getActionCommand().equals("IDLE"))                                     // sets the program mode to have the objects only rotate
        {
            if(idleButton.isSelected())
            {
                for(int i = 0; i < livingThing.size(); i++)
                {
                    LivingThing thing = livingThing.get(i);
                    thing.idleMode = true;
                    thing.randomMotion = false;
                    thing.fallBounce = false;
                    thing.chaseMouse = false;
                }
            }
        }
        
        // -------------------------------------------------------------------
             
        if(e.getActionCommand().equals("RANDOM_MOTION"))                            // sets the program mode to have the objects randomly bounce around the screen
        {
            if(randomMotionButton.isSelected())
            {
                for(int i = 0; i < livingThing.size(); i++)
                {
                    LivingThing thing = livingThing.get(i);

                    thing.xSpeed = rand.nextInt(26) - 15;                                // a speed from -10 to 10
                    thing.ySpeed = rand.nextInt(26) - 15;                                // a speed from -10 to 10
            
                    thing.xAcceleration = 0;                                             // set the xAcceleration
                    thing.yAcceleration = 0;                                             // set the yAcceleration

                    thing.randomMotion = true;
                    thing.idleMode = false;
                    thing.fallBounce = false;
                    thing.chaseMouse = false;
                }
            }
        }

        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("FALL_BOUNCE"))                              // sets the program mode to have the objects be affected by gravity
        {
            if(fallBounceButton.isSelected())
            {
                for(int i = 0; i < livingThing.size(); i++)
                {
                    LivingThing thing = livingThing.get(i);
                    thing.fallBounce = true;
                    thing.randomMotion = false;
                    thing.idleMode = false;
                    thing.chaseMouse = false;
                }
            }
        }

        // -------------------------------------------------------------------

        if(e.getActionCommand().equals("CHASE_MOUSE"))                              // sets the program mode to have the objects chase the mouse
        {
            if(chaseMouseButton.isSelected())
            {
                for(int i = 0; i < livingThing.size(); i++)
                {
                    LivingThing thing = livingThing.get(i);
                    thing.chaseMouse = true;
                    thing.fallBounce = false;
                    thing.randomMotion = false;
                    thing.idleMode = false;
                }
            }
        }

        // -------------------------------------------------------------------
    }

    // =====================================================================================================================

    // for the sliders - changes the values of each variable
    @Override
    public void stateChanged(ChangeEvent e) 
    {
        // -------------------------------------------------------------------

        if(e.getSource() == animationSpeedSlider)
        {
            delay = animationSpeedSlider.getValue();

            timer.stop();

            // create a new timer with the slider value as the delay
            timer = new Timer(delay, new ActionListener() 
            {
                @Override
                public void actionPerformed(final ActionEvent e) 
                {
                    // for every object in the vector, call update
                    for(int i = 0; i < livingThing.size(); i++)
                    {
                        LivingThing thing = livingThing.get(i);
                        thing.update(drawingPanel);
                    }
    
                    drawingPanel.repaint();
                }
            });

            // start the new timer
            timer.start();
        }

        // -------------------------------------------------------------------

        if(e.getSource() == accelerationSlider)
        {
            for(int i = 0; i < livingThing.size(); i++)
            {
                LivingThing thing = livingThing.get(i);
                thing.timeScalar = accelerationSlider.getValue();
            }
        }

        // -------------------------------------------------------------------
    }

    // =====================================================================================================================

    public void lifeOccured(MortalityEvent e) 
    {
        // if a death event has occured, remove the object(s) from the vector
        if(e.isDeathEvent)
        {
            for(int i = 0; i < livingThing.size(); i++)
            {
                if(livingThing.elementAt(i) == e.getSource())           
                {
                    livingThing.removeElementAt(i);
                }
            }
        }
    }

    // =====================================================================================================================

    // creates a new JButton with the values to be added
    JButton newJButton(String name, Color buttonTextColor, ActionListener listener, String command, Color backgroundColor)
    {
        JButton button;
        button = new JButton(name);
        button.setForeground(buttonTextColor);
        button.addActionListener(listener);
        button.setActionCommand(command);
        button.setBackground(backgroundColor);
        return button;
    }

    // =====================================================================================================================

    // creates a new JRadioButton with the values to be added
    JRadioButton newJRadioButton(String name, Color buttonTextColor, ActionListener listener, String command, Color backgroundColor)
    {
        JRadioButton button;
        button = new JRadioButton(name);
        button.setForeground(buttonTextColor);
        button.addActionListener(listener);
        button.setActionCommand(command);
        button.setBackground(backgroundColor);
        return button;
    }

    // =====================================================================================================================

    // from Professor Larue - set up the window area and size
    public void setupMainFrame()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize(d.width - 100, d.height - 100);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Moving rectangles");                                             // for the title bar 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // =====================================================================================================================

    @Override
    public void mousePressed(MouseEvent e) 
    {
        for(int i = 0; i < livingThing.size(); i++)
        {
            LivingThing thing = livingThing.get(i);

            // set the target to the mouse position
            thing.targetX = e.getX();
            thing.targetY = e.getY();
        }
    }

    // =====================================================================================================================

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        for(int i = 0; i < livingThing.size(); i++)
        {
            LivingThing thing = livingThing.get(i);

            // set the new target for when the mouse is released to something within the radius * 7
            thing.targetX = e.getX() + (rand.nextInt((int)(thing.outerRadius * 7)) * (rand.nextBoolean() ? 1 : -1));
            thing.targetY = e.getY() + (rand.nextInt((int)(thing.outerRadius * 7)) * (rand.nextBoolean() ? 1 : -1));
        }
    }

    // =====================================================================================================================

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        for(int i = 0; i < livingThing.size(); i++)
        {
            LivingThing thing = livingThing.get(i);
            thing.targetX = e.getX();
            thing.targetY = e.getY();

            if(((thing.targetX + thing.targetY) - (thing.xPosition + thing.yPosition)) > thing.outerRadius
            || ((thing.targetX + thing.targetY) - (thing.xPosition + thing.yPosition)) < -thing.outerRadius)
            {
                thing.xSpeed = thing.targetX - thing.xPosition; 
                thing.ySpeed = thing.targetY - thing.yPosition; 
            }
        }
    }

    // =====================================================================================================================

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    // =====================================================================================================================

}
