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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
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

    private Array<String> daysOfWeek = new Array<String>(
            new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" });
    private Array<String> typesOfWeek = new Array<String>(new String[] { "default", "delayed", "off" });

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
    int len = 5;

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
            if (!typeOfWeek.equals("off")) {
                loadToday(lastDate);
                fileOpened = true;
                openFileButton.setText("Close");
            }
            setOption("opt_typeofweek", typeOfWeek);
        });

        JSlider lenSlider = new JSlider(4, 7, len);
        lenSlider.setPreferredSize(new Dimension(100, 40));
        lenSlider.setMajorTickSpacing(1);
        lenSlider.setPaintTicks(true);
        lenSlider.setPaintLabels(true);
        lenSlider.setSnapToTicks(true);
        lenSlider.addChangeListener(e -> {
            len = lenSlider.getValue();
        });

        // JButton openConfig = new JButton("Config");
        // openConfig.addActionListener(e -> {
        // ConfigPanel c = new ConfigPanel();
        // // make a option dialog with Cancel and Save buttons
        // Object[] options = { "Cancel", "Save" };
        // int n = JOptionPane.showOptionDialog(this, c, "Config",
        // JOptionPane.YES_NO_OPTION,
        // JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // });

        settingsGroup.add(new JLabel("Set opened schedule"));
        settingsGroup.add(switchSchedule);
        settingsGroup.add(new JLabel("Set type of week"));
        settingsGroup.add(switchWeekType);
        settingsGroup.add(new JLabel("Set percent accuracy"));
        settingsGroup.add(lenSlider);
        // settingsGroup.add(openConfig);
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
            if (fileOpened) {
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

        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        this.setLayout(lay);

        makeSettingsPanel();
        makeButtonGroup();

        // Button for toggling settings
        this.add(settingsGroup);
        this.add(buttonGroup, BorderLayout.SOUTH);

        // Get the current date
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

        // load settings
        File configFile = new File("schedules/config.scopt");
        System.out.println(configFile.getAbsolutePath());
        loadScopt(configFile);
        typeOfWeek = variables.get("opt_typeofweek");
        System.out.println(typeOfWeek);
        System.exit(0);

        // Load files / assets
        loadScopt(new File("schedules/classes.scopt"));
        loadToday(currentDate);
        loadAssets("src/fonts/CascadiaCode.ttf");

        timer.start();
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
        File f = new File("/schedules/" + typeOfWeek + ".sch");
        loadSch(f, dayToGet);
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

        // tell them to select day of week
        String[] options = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        int n = JOptionPane.showOptionDialog(this, "Select the day of the week", "Day of Week",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // get the day code
        String dayCode = options[n].substring(0, 3).toLowerCase();

        loadSch(selected, dayCode);
        viewing = selected.getName();
    }

    // Load the class map, which represents your classes and their colors
    private void loadScopt(File f) {
        try {
            // Set up scanner of the cmap
            Scanner mapReader = new Scanner(f);

            // Keep reading until we reach the end
            while (mapReader.hasNextLine()) {
                String line = mapReader.nextLine();
                String[] tokens = line.split(" = ");
                variables.put(tokens[0], tokens[1]);
                line = mapReader.nextLine();
            }

            mapReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load a schedule file into the event storage
    private void loadSch(File f, String dayCode) {
        // clear the events in case this is during runtime
        events.clear();
        try {
            boolean foundDayStart = false;
            Scanner schedReader = new Scanner(f);
            while (schedReader.hasNextLine()) {

                String line = schedReader.nextLine();
                if (!foundDayStart) {
                    foundDayStart = line.equals(dayCode);
                    continue;
                } else if (foundDayStart && line.equals("END" + dayCode)) {
                    break;
                }

                // if given var:
                if (line.indexOf("$") > -1) {
                    String varName = line.substring(line.indexOf("$") + 1);
                    String val = variables.get(varName);
                    line = line.replace("$" + varName, val);
                }

                // make the scheduled event
                ScheduledEvent ev = new ScheduledEvent(line, scheduleDisplayH, variables);
                ev.setGraphicsConstants(scheduleDisplayY, PREF_W);
                events.add(ev);
            }

            schedReader.close();
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
            while (!lastLine.equals("END")) {
                rewrite += (lastLine.startsWith(key) ? (key + " = " + value) : (lastLine)) + "\n";
                lastLine = s.nextLine();
            }
            Files.write(config.toPath(), (rewrite + "END").getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // The timer to update every second
    private Timer timer = new Timer(1000 / 60, event -> {
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

    private void drawInfo(Graphics2D g2) {
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
        if (!typeOfWeek.equals("default")) {
            centerText(
                    typeOfWeek.substring(0, 1).toUpperCase() + typeOfWeek.substring(1) + weekType,
                    g2,
                    infoOffset + 55);
        }

        // If we are not on today's schedule
        if (!viewing.equals("")) {
            g2.setFont(fontTiny);
            g2.setColor(Color.RED);
            // Display viewing notice
            centerText("Viewing: " + viewing, g2, infoOffset + 70);
            g2.setColor(Color.BLACK);
        }

    }

    // Draw to the window
    public void paintComponent(Graphics g) {
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

        // Draw the tabs
        int w = PREF_W / 3;
        int h = 50;
        for (int i = 0; i < 3; i++) {
            int x = i * w;
            int y = scheduleDisplayY - h;

            // draw outline of left, right, and bottom sides
            g2.drawLine(x, y + h, x + w, y + h);
            g2.drawLine(x, y + h, x, y - h / 2);
            g2.drawLine(x, y + h, x, y - h / 2);
        }

        // Overlay for schedule area
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fill(new java.awt.Rectangle(0, scheduleDisplayY, PREF_W, scheduleDisplayH));
        g2.setColor(Color.BLACK);

        // Display time info at top
        drawInfo(g2);

        // Display the events
        events.forEach(ev -> {
            g2.setColor(ev.getEventColor());
            g2.fill(ev.getVisualBounds());
            g2.setColor(Color.BLACK);

            String textToDisplay = !ev.hovering ? ev.getEventName().substring(1) : (ev.startTime + " -> " + ev.endTime);

            if (ev.isSpan)
                g2.drawString(textToDisplay, 10, 5 + scheduleDisplayY + ev.start + (ev.end - ev.start) / 2);
        });

        // Set up time pointer stroke
        int pointerLength = 30 - len;
        dashPhase += 0.2f;
        BasicStroke dashedStroke = new BasicStroke(
                1.5f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER,
                1.5f,
                dash,
                dashPhase);

        // Draw the percent passed
        g2.setFont(fontTiny);
        String percent = (percentElapsed * 100 + "");
        percent = percent.substring(0, Math.min(len, percent.length() - 1)) + "%";
        int strLen = g2.getFontMetrics().stringWidth(percent);
        g2.drawString(percent, PREF_W - strLen - 3, (int) ratioElapsed + 3 + scheduleDisplayY);

        // Draw the schedule bounds
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, scheduleDisplayY, PREF_W, scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayH + scheduleDisplayY, PREF_W, scheduleDisplayH + scheduleDisplayY);

        // Draw the time pointer
        g2.setStroke(dashedStroke);
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawLine(0, (int) ratioElapsed + scheduleDisplayY, PREF_W - pointerLength,
                (int) ratioElapsed + scheduleDisplayY);
        g2.setStroke(new BasicStroke(2));

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

    @Override
    public void mouseMoved(MouseEvent e) {
        for (ScheduledEvent event : events)
            if (event.getVisualBounds().intersects(new Rectangle(e.getX(), e.getY(), 1, 1)))
                event.hovering = true;
            else if (event.hovering)
                event.hovering = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }
}
