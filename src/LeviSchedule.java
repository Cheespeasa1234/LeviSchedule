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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
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

public class LeviSchedule extends JPanel implements Serializable {

    public static final long serialVersionUID = "You're Mother".hashCode();
    
    public static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int screenW = (int) screenDimension.getWidth();
    public static final int screenH = (int) screenDimension.getHeight();

    public static final int PREF_W = 150;
    public static final int PREF_H = screenH + 100;

    private String fontName = "Cascadia Mono";
    private Font font;
    private Font fontBold;
    private Font fontMedium;
    private Font fontSmall;

    private Color color1 = new Color(0, 201, 195, 130);
    private Color color2 = new Color(33, 118, 255, 130);
    private GradientPaint backgroundPaint = new GradientPaint(0, 0, color1, PREF_W, PREF_H, color2);

    private final String[] daysOfWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
            "Saturday" };

    private ArrayList<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
    private HashMap<String, String> variables = new HashMap<String, String>();
    private String typeOfWeek = "default";

    private String viewing = "";

    private double ratioElapsed = 0;
    private double percentElapsed = 0;
    private int scheduleDisplayY = 100;
    private int scheduleDisplayH = 500;
    private float dashPhase = 0.0f;
    private float[] dash = { 2.0f, 5.0f };

    private int lastDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

    public LeviSchedule(BorderLayout lay) {
        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.setLayout(lay);

        JPanel settingsGroup = new JPanel();
        settingsGroup.setLayout(new BorderLayout());
        JComboBox<String> switchSchedule = new JComboBox<String>(daysOfWeek);
        switchSchedule.setSelectedItem(daysOfWeek[lastDate-1]);
        switchSchedule.addActionListener(event -> {
            String chosenSchedule = (String) switchSchedule.getSelectedItem();
            List<String> daysOfWeekAL = Arrays.asList(daysOfWeek);
            int listIndexChosen = daysOfWeekAL.indexOf(chosenSchedule);
            if(listIndexChosen + 1 != lastDate) {
                viewing = daysOfWeek[listIndexChosen];
                loadToday(listIndexChosen + 1);
            } else {
                viewing = "";
            }
        });

        settingsGroup.add(switchSchedule, BorderLayout.SOUTH);
        settingsGroup.setVisible(false);

        JButton openSettingsButton = new JButton("Options");
        openSettingsButton.addActionListener(event -> {
            settingsGroup.setVisible(!settingsGroup.isVisible());
        });
        openSettingsButton.setVisible(true);

        this.add(openSettingsButton, BorderLayout.SOUTH);
        this.add(settingsGroup);

        timer.start();
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

        // read by line
        loadcmap("schedules/classes.scopt");
        loadToday(currentDate);
        loadAssets();
    }

    private void loadAssets() {
        // load fonts
        font = new Font(fontName, Font.PLAIN, 16);
        fontBold = new Font(fontName, Font.BOLD, 16);
        fontSmall = new Font(fontName, Font.PLAIN, 10);
        fontMedium = new Font(fontName, Font.PLAIN, 12);
    }

    private void loadToday(int currentDate) {
        String dayToGet = daysOfWeek[currentDate - 1].substring(0, 3).toLowerCase();
        loadSchedule("schedules/" + typeOfWeek + "/" + dayToGet + ".sched");
    }

    private void loadcmap(String path) {
        try {
            Scanner mapReader = new Scanner(new File(path));
            String line = mapReader.nextLine();
            while (!line.equals("END")) {
                String[] tokens = line.split(" = ");
                variables.put(tokens[0], tokens[1]);
                line = mapReader.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSchedule(String path) {
        events.clear();
        try {
            Scanner schedReader = new Scanner(new File(path));
            String line = schedReader.nextLine();
            while (!line.equals("END")) {
                events.add(new ScheduledEvent(line, scheduleDisplayH, variables));
                line = schedReader.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Timer timer = new Timer(500, event -> {
        repaint();

        java.util.Date now = new java.util.Date();
        double second = now.getSeconds();
        double minute = now.getMinutes();
        double hour = now.getHours();

        percentElapsed = ((second + (minute * 60) + (hour * 3600))) / 86400;
        ratioElapsed = percentElapsed * scheduleDisplayH;

        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        if (lastDate != currentDate) {
            loadToday(currentDate);
        }
    });

    private void centerText(String text, Graphics2D g2, int height) {
        g2.drawString(text, PREF_W / 2 - g2.getFontMetrics().stringWidth(text) / 2, height);
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(font);

        Paint beforeGrad = g2.getPaint();
        g2.setPaint(backgroundPaint);
        g2.fill(new java.awt.Rectangle(0, 0, PREF_W, PREF_H));
        g2.setPaint(beforeGrad);

        g2.setColor(new Color(255, 255, 255, 100));
        g2.fill(new java.awt.Rectangle(0, scheduleDisplayY, PREF_W, scheduleDisplayH));
        g2.setColor(Color.BLACK);

        Date now = new Date();
        
        int infoOffset = 25;

        String timeStamp = new SimpleDateFormat("HH:mm.ss").format(now);
        centerText(timeStamp, g2, infoOffset);
        
        String dateStamp = new SimpleDateFormat("MM/dd/yyyy").format(now);
        centerText(dateStamp, g2, infoOffset + 20);
        
        if(!viewing.equals("")) {
            g2.setFont(fontMedium);
            g2.setColor(Color.RED);
            centerText("Viewing: " + viewing, g2, infoOffset + 60);
            g2.setColor(Color.BLACK);
        }
        
        g2.setFont(fontBold);
        String weekStamp = daysOfWeek[lastDate - 1];
        centerText(weekStamp, g2, infoOffset + 40);

        g2.setFont(fontSmall);
        for (ScheduledEvent ev : events) {
            g2.setColor(ev.c);
            g2.fillRect(0, ev.start + scheduleDisplayY, PREF_W, ev.end - (ev.start));
            g2.setColor(Color.black);
            if (ev.isSpan)
                g2.drawString(ev.event.substring(1), 10, scheduleDisplayY + ev.start + (ev.end - ev.start) / 2);
        }

        dashPhase += 9.0f;
        BasicStroke dashedStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.5f, dash,
                dashPhase);

        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, scheduleDisplayY, PREF_W, scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayH + scheduleDisplayY, PREF_W, scheduleDisplayH + scheduleDisplayY);

        g2.setStroke(dashedStroke);
        g2.drawLine(0, (int) ratioElapsed + scheduleDisplayY, PREF_W - 65, (int) ratioElapsed + scheduleDisplayY);

        String percent = (percentElapsed * 100 + "");
        percent = percent.substring(0, Math.min(5, percent.length() - 1));
        g2.drawString(percent + "%", PREF_W - 60, (int) ratioElapsed + 3 + scheduleDisplayY);
    }

    /* METHODS FOR CREATING JFRAME AND JPANEL */

    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("LeviSchedule");

        JPanel mainPanel = new LeviSchedule(new BorderLayout());

        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(screenW - frame.getWidth(), 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
