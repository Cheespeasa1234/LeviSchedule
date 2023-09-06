// Written by Nathaniel Levison, March 2023
// License for this file at /LICENSE.txt

// Import Dependencies
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import lib.Array;

public class LeviSchedule extends JPanel implements MouseMotionListener, MouseListener {

    // Define the screen size for math reasons
    public static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int screenW = (int) screenDimension.getWidth();
    public static final int screenH = (int) screenDimension.getHeight();
    
    // Define the window size
    public static final int PREF_W = 150;
    public static final int PREF_H = screenH + 100;

    // define the schedule display size
    private int scheduleDisplayY = 200;
    private int scheduleDisplayH = 500;
    
    // define the tab open
    private int tabIdx = 0;

    // Objects for the fonts
    private Font font;
    private Font fontBold;
    private Font fontMedium;
    private Font fontSmall;
    private Font fontTiny;

    // Objects for the background colors etc
    private Color defaultBG1 = new Color(0, 201, 195, 130);
    private Color defaultBG2 = new Color(33, 118, 255, 130);
    private GradientPaint backgroundPaint = new GradientPaint(0, 0, defaultBG1, PREF_W, PREF_H, defaultBG2);

    private Array<String> daysOfWeek = new Array<String>(new String[] {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"});
    private Array<String> typesOfWeek = new Array<String>(new String[] {"default", "delayed", "off"});
    
    private int lastDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
    private String typeOfWeek = "default";

    // List the events and variables used in fs
    private Array<ScheduledEvent> events = new Array<ScheduledEvent>();
    private HashMap<String, String> variables = new HashMap<String, String>();

    // Will display text if we are looking at a different schedule than is today
    private String viewing = "";

    // Will change the open button to close button
    private boolean fileOpened = false;

    // Variables for the dotted line representing now on screen
    private double ratioElapsed = 0;
    private double percentElapsed = 0;
    private float dashPhase = 0.0f;
    private float[] dash = { 2.0f, 5.0f };

    JComboBox<String> switchSchedule, switchWeekType;
    JButton openSettingsButton, openFileButton;
    JPanel settingsGroup, buttonGroup;
    GridLayout experimentLayout;

    // constructs the settings panel
    private JPanel makeSettingsPanel() {
        // Define the settings panel
        settingsGroup = new JPanel();
        // Dropdown for the choosing of schedule
        switchSchedule = new JComboBox<String>(daysOfWeek.toArray());
        switchSchedule.setSelectedItem(daysOfWeek.get(lastDate < 7 ? lastDate : 0));
        switchSchedule.setBorder(new EmptyBorder(0, 0, 20, 0));
        switchSchedule.addActionListener(event -> {
            String chosenSchedule = (String) switchSchedule.getSelectedItem();
            int listIndexChosen = daysOfWeek.indexOf(chosenSchedule);

            loadToday(listIndexChosen < 7 ? listIndexChosen : 0);
            if (listIndexChosen != lastDate) {
                viewing = daysOfWeek.get(listIndexChosen);
            } else {
                viewing = "";
            }
        });
        
        switchWeekType = new JComboBox<String>(typesOfWeek.toArray());
        switchWeekType.setSelectedItem(typeOfWeek);
        switchWeekType.setBorder(new EmptyBorder(0, 0, 20, 0));
        switchWeekType.addActionListener(event -> {
            typeOfWeek = (String) switchWeekType.getSelectedItem();
            if(!typeOfWeek.equals("off")) {
                loadToday(lastDate);
                fileOpened = true;
                openFileButton.setText("Close");
            }
            setOption("opt_typeofweek", typeOfWeek);
        });

        settingsGroup.add(new JLabel("Set opened schedule"));
        settingsGroup.add(switchSchedule);
        settingsGroup.add(new JLabel("Set type of week"));
        settingsGroup.add(switchWeekType);
        settingsGroup.setVisible(false);
        
        return settingsGroup;
    }

    // Initializes and adds the menu at the bottom of the screen
    private void makeButtonGroup() {
        
        // Button for toggling settings
        openSettingsButton = new JButton("Options");
        openSettingsButton.addActionListener(event -> {
            settingsGroup.setVisible(!settingsGroup.isVisible());
        });
        // Button for opening file
        openFileButton = new JButton("Open");
        openFileButton.addActionListener(event -> {
            if(fileOpened) {
                loadToday(lastDate);
                fileOpened = false;
                openFileButton.setText("Open");
                viewing = "";
            } else {
                loadChosen();
                fileOpened = true;
                openFileButton.setText("Close");
            }
        });

        experimentLayout = new GridLayout(0, 2);
        buttonGroup = new JPanel(experimentLayout);
        buttonGroup.add(openSettingsButton);
        buttonGroup.add(openFileButton);
    }

    // Constructor
    public LeviSchedule(BorderLayout lay) {
        // set visibility stuffs
        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        // BufferedImage image = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
        // Cursor smoothCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(15, 15), "invisibleCursor");

        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        
        this.setLayout(lay);

        makeSettingsPanel();
        makeButtonGroup();

        // Button for toggling settings
        this.add(settingsGroup);
        this.add(buttonGroup, BorderLayout.SOUTH);
        
        // Start the repaint / update timer
        timer.start();
        
        // Get the current date
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        
        // load settings
        loadScopt("schedules/config.scopt");
        typeOfWeek = variables.get("opt_typeofweek");

        // Load files / assets
        loadScopt("schedules/classes.scopt");
        loadToday(currentDate);
        loadAssets("src/fonts/CascadiaCode.ttf");
    }

    // Load assets, like fonts and images
    private void loadAssets(String fontFile) {
        // load fonts
        try {
            // get the font from the file
            File fontSource = new File(fontFile);
            Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontSource).deriveFont(12f);

            // derive the fonts for use
            
            // register the fonts
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(loadedFont);

            font = new Font("Cascadia Code", Font.PLAIN, 16);
            fontBold = new Font("Cascadia Code", Font.PLAIN, 16);
            fontSmall = new Font("Cascadia Code", Font.PLAIN, 10);
            fontTiny = new Font("Cascadia Code", Font.PLAIN, 8);
            fontMedium = new Font("Cascadia Code", Font.PLAIN, 12);
            // If the file wasn't found, or was incorrectly formatted:
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    // Wrapper function for loadSchedule, loads based off of day number
    private void loadToday(int currentDate) {
        // get the three letter representation of the date
        if (currentDate == 7)
            currentDate = 0;
        String dayToGet = daysOfWeek.get(currentDate).substring(0, 3).toLowerCase();
        // load the schedule
        loadSched("schedules/" + typeOfWeek + "/" + dayToGet + ".sched");
    }

    // recursively sets the font value of a Component
    private void setFontRecursively(java.awt.Component[] comp, Font f) {
        for (int x = 0; x < comp.length; x++) {
            if (comp[x] instanceof java.awt.Container)
                setFontRecursively(((java.awt.Container) comp[x]).getComponents(), f);
            try {
                comp[x].setFont(f);
            } catch (Exception e) {
            } // do nothing
        }
    }

    // prompts user to pick a .sched file, which then loads the file into the app
    private void loadChosen() {
        String docsFolder = System.getProperty("user.dir") + "/schedules";
        File documents = new File(docsFolder);
        JFileChooser jfc = new JFileChooser(documents);
        setFontRecursively(jfc.getComponents(), fontMedium);
        jfc.showOpenDialog(this);
        File selected = jfc.getSelectedFile();
        
        String path = selected.getPath();
        loadSched(path);
        String cutoff = "LeviSchedule/schedules/";
        viewing = path.substring(path.indexOf(cutoff) + cutoff.length());
    }

    // Load the class map, which represents your classes and their colors
    private void loadScopt(String path) {
        try {
            // Set up scanner of the cmap
            Scanner mapReader = new Scanner(new File(path));
            String line = mapReader.nextLine();

            // Keep reading until we reach the end
            while (!line.equals("END")) {
                // split the key and value
                String[] tokens = line.split(" = ");
                // put the variable in the variable map
                variables.put(tokens[0], tokens[1]);
                // Read next line
                line = mapReader.nextLine();
            }
            // If anything went wrong
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load a schedule file into the event storage
    private void loadSched(String path) {
        // clear the events in case this is during runtime
        events.clear();
        try {
            // Set up scanner of the sched
            Scanner schedReader = new Scanner(new File(path));
            String line = schedReader.nextLine();

            // Keep reading until we reach the end
            while (!line.equals("END")) {
                // Send the line to be processed by the ScheduledEvent constructor
                ScheduledEvent ev = new ScheduledEvent(line, scheduleDisplayH, variables);
                ev.setGraphicsConstants(scheduleDisplayY, PREF_W);
                events.add(ev);
                // Read next line
                line = schedReader.nextLine();
            }
            // In case it was an invalid file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private double getPercentElapsed() {
        // Get the current date (precise)
        java.util.Date now = new java.util.Date();
        // Get the seconds, minutes, and hours
        // TODO: find de-deprecated solution
        double second = now.getSeconds();
        double minute = now.getMinutes();
        double hour = now.getHours();
        // Get the percent of the day that has passed
        double percent = ((second + (minute * 60) + (hour * 3600))) / 86400;
        return percent;
    }

    private void setOption(String key, String value) {
        variables.put(key, value);
        // get config file
        File config = new File("schedules/config.scopt");
        String rewrite = "";
        Scanner s;
        try {
            s = new Scanner(config);
            String lastLine = s.nextLine();
            while(!lastLine.equals("END")) {
                rewrite += (lastLine.startsWith(key) ? (key + " = " + value) : (lastLine)) + "\n";
                lastLine = s.nextLine();
            }
            Files.write(config.toPath(), (rewrite + "END").getBytes());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // The timer to update every second
    private Timer timer = new Timer(1000/60, event -> {
        // Repaint the component
        repaint();
        
        // Multiply the length of the schedule display by the passed percent
        percentElapsed = getPercentElapsed();
        ratioElapsed = percentElapsed * scheduleDisplayH;

        // get the current day of the week
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        // if the day of the week has changed (happens once a day!!)
        if (lastDate != currentDate) {
            // if this makes the new week
            if (currentDate == 7) {
                setOption("opt_typeofweek", "default");
                typeOfWeek = "default";
            }
            // change the day
            loadToday(currentDate);
            lastDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        }
    });

    // Draw a string in the center of the viewport
    private void centerText(String text, Graphics2D g2, int height) {
        // get the width of the text as it would be displayed
        int width = g2.getFontMetrics().stringWidth(text);
        // draw the string
        g2.drawString(text, PREF_W / 2 - width / 2, height);
    }

    int redrawCursorPos = 0;
    // Draw to the window
    protected final void paintComponent(Graphics g) {
        redrawCursorPos++;
        // Call the supermethod
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Set the default font
        g2.setFont(font);

        // Draw the gradient background
        Paint beforeGrad = g2.getPaint();
        g2.setPaint(backgroundPaint);
        g2.fill(new java.awt.Rectangle(0, 0, PREF_W, PREF_H));
        g2.setPaint(beforeGrad);
        // Overlay for schedule area
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fill(new java.awt.Rectangle(0, scheduleDisplayY, PREF_W, scheduleDisplayH));
        g2.setColor(Color.BLACK);

        // Display time info at top
        Date now = new Date();
        int infoOffset = 25;
        centerText(new SimpleDateFormat("HH:mm.ss").format(now), g2, infoOffset);
        centerText(new SimpleDateFormat("MM/dd/yyyy").format(now), g2, infoOffset + 20);
        g2.setFont(fontBold);
        String weekStamp = daysOfWeek.get(lastDate < 7 ? lastDate : 0);
        centerText(weekStamp, g2, infoOffset + 40);

        // Display day info
        String weekType = " week";
        g2.setFont(fontSmall);
        if(!typeOfWeek.equals("default")) {
            centerText(
                typeOfWeek.substring(0, 1).toUpperCase() + typeOfWeek.substring(1) + weekType, 
                g2, 
                infoOffset + 55
            );
        }

        // Display the events
        events.forEach(ev -> {
            ev.paint(g2);
        });

        // Set up time pointer stroke
        int pointerLength = 30;
        dashPhase += 0.2f;
        BasicStroke dashedStroke = new BasicStroke(
            1.5f, 
            BasicStroke.CAP_ROUND, 
            BasicStroke.JOIN_MITER, 
            1.5f, 
            dash,
            dashPhase
            );
            
        // Draw the percent passed
        g2.setFont(fontTiny);
        String percent = (percentElapsed * 100 + "");
        percent = percent.substring(0, Math.min(4, percent.length() - 1));
        g2.drawString(percent + "%", PREF_W - (pointerLength - 2), (int) ratioElapsed + 3 + scheduleDisplayY);
        
        // Draw the schedule bounds
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, scheduleDisplayY, PREF_W, scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayH + scheduleDisplayY, PREF_W, scheduleDisplayH + scheduleDisplayY);

        // If we are not on today's schedule
        g2.setFont(fontTiny);
        g2.setColor(Color.RED);
        if (!viewing.equals("")) {
            // Display viewing notice
            centerText("Viewing: " + viewing, g2, infoOffset + 70);
        }

        // Draw the time pointer
        g2.setStroke(dashedStroke);
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawLine(0, (int) ratioElapsed + scheduleDisplayY, PREF_W - pointerLength, (int) ratioElapsed + scheduleDisplayY);

    }

    /* METHODS FOR CREATING JFRAME AND JPANEL */
    // set window size
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
    // create the window
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("LeviSchedule");
        BorderLayout lay = new BorderLayout();
        JPanel mainPanel = new LeviSchedule(lay);

        // settings
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(screenW - frame.getWidth(), 0);
    }

    // breh idk
    public static strictfp final void main(final String... arguments) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    @Override public void mouseMoved(MouseEvent e) {
        for(ScheduledEvent event: events)
        if (event.getVisualBounds().intersects(new Rectangle(e.getX(), e.getY(), 1, 1)))
        event.onHover(e.getX(), e.getY());
        else if(event.hovering) 
        event.onUnHover(e.getX(), e.getY());  
    }

    @Override public void mouseDragged(MouseEvent e) {}

    @Override public void mouseClicked(MouseEvent e) {}

    @Override public void mousePressed(MouseEvent e) {}

    @Override public void mouseReleased(MouseEvent e) {}

    @Override public void mouseExited(MouseEvent e) {}

    @Override public void mouseEntered(MouseEvent e) {}
}
