import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class JConfigMainArea extends JPanel {

    ArrayList<ArrayList<ScheduledEvent>> events;
    String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
    HashMap<String, String> variables = new HashMap<String, String>();

    public JConfigMainArea(ArrayList<ArrayList<ScheduledEvent>> events) {
        super();
        File f = new File("schedules/classes.scopt");
        System.out.println(f.getAbsolutePath());
        LeviScheduleLoader.loadScopt(f, variables);
        setPreset("default");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < events.size(); i++) {
            for (int j = 0; j < events.get(i).size(); j++) {
                ScheduledEvent event = events.get(i).get(j);
                // get the starting time
                double spct = event.startPercent;
                double epct = event.endPercent;

                int startX = (int) (spct * getWidth());
                int endX = (int) (epct * getWidth());

                int h = getHeight() / 7;
                int y = i * h; // by day of the week
                g2.setColor(event.trackedClass.color);
                g2.fillRect(startX, y, endX - startX, h);
            }
        }
    }

    public void setPreset(String preset) {

        events = new ArrayList<ArrayList<ScheduledEvent>>();

        // find the file in src/schedules/[preset].sch
        String path = "schedules/" + preset + ".sch";
        File f = new File(path);
        System.out.println(f.getAbsolutePath());
        if (!f.exists()) {
            // create file
            System.out.println("Creating file: " + path);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            // read file
            System.out.println("Reading file: " + path);
            try {
                Scanner s = new Scanner(f);
                int day = 0;
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    if(line.startsWith("END")) {
                        day++;
                        continue;
                    } else if(line.startsWith("START")) {
                        events.add(new ArrayList<ScheduledEvent>());
                        continue;
                    }

                    // if there is a var
                    if(line.contains("$")) {
                        String[] tokens = line.split(" ");
                        for(int i = 0; i < tokens.length; i++) {
                            if(tokens[i].startsWith("$")) {
                                tokens[i] = variables.get(tokens[i].substring(1));
                            }
                        }
                        line = String.join(" ", tokens);
                    }

                    ScheduledEvent event = new ScheduledEvent(line, 50, variables);
                    event.setGraphicsConstants(0, 100);
                    events.get(day).add(event);
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repaint();
    }
}
