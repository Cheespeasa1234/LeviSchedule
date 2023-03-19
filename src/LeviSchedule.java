// Written by Nathaniel Levison, March 2023
// License for this file at /LICENSE.txt

// Import Dependencies
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LeviSchedule extends JPanel {

    // Define the screen size for math reasons
    public static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int screenW = (int) screenDimension.getWidth();
    public static final int screenH = (int) screenDimension.getHeight();

    // Define the window size
    public static final int PREF_W = 150;
    public static final int PREF_H = screenH + 100;

    // Objects for the fonts
    private Font font;
    private Font fontBold;
    private Font fontMedium;
    private Font fontSmall;

    // Objects for the background colors etc
    private Color color1 = new Color(0, 201, 195, 130);
    private Color color2 = new Color(33, 118, 255, 130);
    private GradientPaint backgroundPaint = new GradientPaint(0, 0, color1, PREF_W, PREF_H, color2);

    // List of the days of the week for displaying and for file management
    private final String[] daysOfWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    // List the events and variables used in fs
    private ArrayList<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
    private HashMap<String, String> variables = new HashMap<String, String>();

    // Define what type of week it is (which folder)
    private String typeOfWeek = "default";

    // Will display text if we are looking at a different day than is today
    private String viewing = "";

    // Variables for the dotted line representing now
    private double ratioElapsed = 0;
    private double percentElapsed = 0;
    private int scheduleDisplayY = 100;
    private int scheduleDisplayH = 500;
    private float dashPhase = 0.0f;
    private float[] dash = { 2.0f, 5.0f };

    // Get the day of today
    private int lastDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

    // Constructor for the main panel
    public LeviSchedule(BorderLayout lay) {

        // set visibility stuffs
        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.setLayout(lay);

        // Define the settings panel
        JPanel settingsGroup = new JPanel();
        settingsGroup.setLayout(new BorderLayout());

        // Dropdown for the choosing of schedule
        JComboBox<String> switchSchedule = new JComboBox<String>(daysOfWeek);
        switchSchedule.setSelectedItem(daysOfWeek[lastDate - 1]);
        switchSchedule.addActionListener(event -> {
            String chosenSchedule = (String) switchSchedule.getSelectedItem();
            List<String> daysOfWeekAL = Arrays.asList(daysOfWeek);
            int listIndexChosen = daysOfWeekAL.indexOf(chosenSchedule);

            loadToday(listIndexChosen + 1);
            if (listIndexChosen + 1 != lastDate) {
                viewing = daysOfWeek[listIndexChosen];
            } else {
                viewing = "";
            }
        });
        settingsGroup.add(switchSchedule, BorderLayout.SOUTH);
        settingsGroup.setVisible(false);

        // Button for toggling settings
        JButton openSettingsButton = new JButton("Options");
        openSettingsButton.addActionListener(event -> {
            settingsGroup.setVisible(!settingsGroup.isVisible());
        });
        openSettingsButton.setVisible(true);

        // Add the settings to the panel for later use
        this.add(openSettingsButton, BorderLayout.SOUTH);
        this.add(settingsGroup);

        // Start the repaint / update timer
        timer.start();

        // Get the current date
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

        // Load files / assets
        loadcmap("schedules/classes.scopt");
        loadToday(currentDate);
        loadAssets();
    }

    // Load assets, like fonts and images
    private void loadAssets() {
        // load fonts
        try {
            // get the font from the file
            File fontSource = new File("src/fonts/CascadiaCode.ttf");
            Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontSource).deriveFont(12f);

            // derive the fonts for use
            font = loadedFont.deriveFont(16);
            fontBold = font.deriveFont(Font.BOLD);
            fontSmall = loadedFont.deriveFont(10);
            fontMedium = loadedFont.deriveFont(12);

            // register the fonts
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            ge.registerFont(fontBold);
            ge.registerFont(fontSmall);
            ge.registerFont(fontMedium);

            // If the file wasn't found, or was incorrectly formatted:
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    // Wrapper function for loadSchedule, loads based off of day number
    private void loadToday(int currentDate) {
        // get the three letter representation of the date
        String dayToGet = daysOfWeek[currentDate - 1].substring(0, 3).toLowerCase();
        // load the schedule
        loadSchedule("schedules/" + typeOfWeek + "/" + dayToGet + ".sched");
    }

    // Load the class map, which represents your classes and their colors
    private void loadcmap(String path) {
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
    private void loadSchedule(String path) {
        // clear the events in case this is during runtime
        events.clear();
        try {
            // Set up scanner of the sched
            Scanner schedReader = new Scanner(new File(path));
            String line = schedReader.nextLine();

            // Keep reading until we reach the end
            while (!line.equals("END")) {
                // Send the line to be processed by the ScheduledEvent constructor
                events.add(new ScheduledEvent(line, scheduleDisplayH, variables));
                // Read next line
                line = schedReader.nextLine();
            }
            // In case it was an invalid file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // The timer to update every 1/2 second
    private Timer timer = new Timer(500, event -> {
        // Repaint the component
        repaint();
        // Get the current date (precise)
        java.util.Date now = new java.util.Date();
        // Get the seconds, minutes, and hours
        // TODO: find de-deprecated solution
        double second = now.getSeconds();
        double minute = now.getMinutes();
        double hour = now.getHours();
        // Get the percent of the day that has passed
        percentElapsed = ((second + (minute * 60) + (hour * 3600))) / 86400;
        // Multiply the length of the schedule display by the passed percent
        ratioElapsed = percentElapsed * scheduleDisplayH;

        // get the current day of the week
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        // if the day of the week has changed (happens once a day!!)
        if (lastDate != currentDate) {
            // change the day
            loadToday(currentDate);
        }
    });

    // Draw a string in the center of the viewport
    private void centerText(String text, Graphics2D g2, int height) {
        // get the width of the text as it would be displayed
        int width = g2.getFontMetrics().stringWidth(text);
        // draw the string
        g2.drawString(text, PREF_W / 2 - width / 2, height);
    }

    // Draw to the window
    protected void paintComponent(Graphics g) {

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
        // Display current time
        String timeStamp = new SimpleDateFormat("HH:mm.ss").format(now);
        centerText(timeStamp, g2, infoOffset);
        // Display current date
        String dateStamp = new SimpleDateFormat("MM/dd/yyyy").format(now);
        centerText(dateStamp, g2, infoOffset + 20);
        // If we are not on today's schedule
        if (!viewing.equals("")) {
            // Display viewing notice
            g2.setFont(fontMedium);
            g2.setColor(Color.RED);
            centerText("Viewing: " + viewing, g2, infoOffset + 60);
            g2.setColor(Color.BLACK);
        }
        // Display day of the week
        g2.setFont(fontBold);
        String weekStamp = daysOfWeek[lastDate - 1];
        centerText(weekStamp, g2, infoOffset + 40);

        // Display the events
        g2.setFont(fontSmall);
        for (ScheduledEvent ev : events) {
            g2.setColor(ev.c);
            g2.fillRect(0, ev.start + scheduleDisplayY, PREF_W, ev.end - (ev.start));
            g2.setColor(Color.black);
            if (ev.isSpan)
                g2.drawString(ev.event.substring(1), 10, 5 + scheduleDisplayY + ev.start + (ev.end - ev.start) / 2);
        }

        // Set up time pointer stroke
        dashPhase += 9.0f;
        BasicStroke dashedStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.5f, dash,
                dashPhase);
        // Draw the time pointer
        g2.setStroke(dashedStroke);
        g2.drawLine(0, (int) ratioElapsed + scheduleDisplayY, PREF_W - 65, (int) ratioElapsed + scheduleDisplayY);
        // Draw the schedule bounds
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, scheduleDisplayY, PREF_W, scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayH + scheduleDisplayY, PREF_W, scheduleDisplayH + scheduleDisplayY);
        // Draw the percent passed
        String percent = (percentElapsed * 100 + "");
        percent = percent.substring(0, Math.min(5, percent.length() - 1));
        g2.drawString(percent + "%", PREF_W - 60, (int) ratioElapsed + 3 + scheduleDisplayY);
    }

    /* METHODS FOR CREATING JFRAME AND JPANEL */
    // set window size
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }
    // create the window
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("LeviSchedule");
        JPanel mainPanel = new LeviSchedule(new BorderLayout());

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
    public static void main(java.lang.String... arguments) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
