import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BasicStroke;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Toolkit;

public class LeviSchedule extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    
    public static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int screenW = (int) screenDimension.getWidth();
    public static final int screenH = (int) screenDimension.getHeight();
    
    public static final int PREF_W = 150;
    public static final int PREF_H = screenH + 100;
    
    private String fontName = "Cascadia Mono";
    private Font font;
    private Font fontBold;
    private Font fontSmall;
    
    private final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private ArrayList<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
    private HashMap<String, String> variables = new HashMap<String, String>();
    private String typeOfWeek = "default";

    private double ratioElapsed = 0;
    private double percentElapsed = 0;
    private int scheduleDisplayY = 100;
    private int scheduleDisplayH = 500;

    private int lastDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);

    public LeviSchedule() {
        this.setFocusable(true);
        this.setBackground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        
        timer.start();
        int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
        
        font = new Font(fontName, Font.PLAIN, 16);
        fontBold = new Font(fontName, Font.BOLD, 16);
        fontSmall = new Font(fontName, Font.PLAIN, 10);

        // read by line
        loadcmap("schedules/classes.cmap");
        loadToday(currentDate);
        loadAssets();
    }

    private void loadAssets() {
        // load fonts

    }

    private void loadToday(int currentDate) {
        String dayToGet = daysOfWeek[currentDate-1].substring(0, 3).toLowerCase();
        loadSchedule("schedules/" + typeOfWeek + "/" + dayToGet + ".sched");
    }

    private void loadcmap(String path) {
        try {
            Scanner mapReader = new Scanner(new File(path));
            String line = mapReader.nextLine();
            while(!line.equals("END")) {
                String[] tokens = line.split(" = ");
                variables.put(tokens[0], tokens[1]);
                line = mapReader.nextLine();
            }
        } catch(Exception e) { e.printStackTrace(); }
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

    private Timer timer = new Timer(1000, new ActionListener() {
        @Override public void actionPerformed(ActionEvent e) {
            repaint();
            java.util.Date now = new java.util.Date();
            double second = now.getSeconds();
            double minute = now.getMinutes();
            double hour = now.getHours();
            percentElapsed = ((second + (minute * 60) + (hour * 3600))) / 86400;
            ratioElapsed = percentElapsed * scheduleDisplayH;

            int currentDate = LocalDate.now().getDayOfWeek().get(ChronoField.DAY_OF_WEEK);
            if(lastDate != currentDate) {
                loadToday(currentDate);
            }
        }
    });

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(font);
        
        Date now = new Date();
        String timeStamp = new SimpleDateFormat("HH:mm.ss").format(now);
        int timeStampWidth = g2.getFontMetrics().stringWidth(timeStamp);
        g2.drawString(timeStamp, PREF_W / 2 - timeStampWidth / 2, 20);
        
        String dateStamp = new SimpleDateFormat("MM/dd/yyyy").format(now);
        int dateStampWidth = g2.getFontMetrics().stringWidth(dateStamp);
        g2.drawString(dateStamp, PREF_W / 2 - dateStampWidth / 2, 40);
        
        g2.setFont(fontBold);
        String weekStamp = daysOfWeek[lastDate-1];
        int weekStampWidth = g2.getFontMetrics().stringWidth(weekStamp);
        g2.drawString(weekStamp, PREF_W / 2 - weekStampWidth / 2, 80);

        
        g2.setFont(fontSmall);
        for(ScheduledEvent ev: events) {
            g2.setColor(ev.c);
            g2.fillRect(0, ev.start + scheduleDisplayY, PREF_W, ev.end - (ev.start));
            g2.setColor(Color.black);
            if(ev.isSpan)
                g2.drawString(ev.event.substring(1), 10, scheduleDisplayY + ev.start + (ev.end-ev.start) / 2);
        }
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, (int) ratioElapsed + scheduleDisplayY, PREF_W - 65, (int) ratioElapsed + scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayY, PREF_W, scheduleDisplayY);
        g2.drawLine(0, scheduleDisplayH + scheduleDisplayY, PREF_W, scheduleDisplayH + scheduleDisplayY);
        String percent = (percentElapsed * 100 + "");
        percent = percent.substring(0, Math.min(7, percent.length()-1));
        g2.drawString(percent + "%", PREF_W - 60, (int) ratioElapsed + 5 + scheduleDisplayY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    /* METHODS FOR CREATING JFRAME AND JPANEL */

    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("You're Mother");
        JPanel gamePanel = new LeviSchedule();

        frame.getContentPane().add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(screenW-frame.getWidth(), screenH-frame.getHeight());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
