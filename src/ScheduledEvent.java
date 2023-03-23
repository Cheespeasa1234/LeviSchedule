// Written by Nathaniel Levison, March 2023
// License for this file at /LICENSE.txt

// Import Dependencies
import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;

import lib.Hoverable;

public class ScheduledEvent implements Hoverable {
    // maps time "HH:mm" >> event "Event"
    public String startTime, endTime;
    public double startSeconds, endSeconds, startPercent, endPercent;
    public int start, end;

    public boolean isSpan; // does it have beginning and end
    public boolean hovering;
    public String event;
    public Color c;

    // turn the file input to seconds since midnight
    public static int inputTimeToSeconds(String in) {
        String[] tokens = in.split(":");
        int[] hs = new int[]{Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1])};
        int secs = (hs[0] * 60 * 60) + hs[1] * 60;
        return secs;
    }

    // constructor
    public ScheduledEvent(String rawData, int displayH, HashMap<String, String> vars) {
        // if given var:
        if (rawData.indexOf("$") > -1) {
            // get the variable called
            String var = rawData.substring(rawData.indexOf("$") + 1);
            System.out.print("VAR: " + var + " || ");
            // get the variable value
            String val = vars.get(var);
            System.out.print("VAL: " + val + " || ");
            // replace the variable call with the value (very dynamic)
            rawData = rawData.replace("$" + var, val);
            System.out.println("RAW: " + rawData + " || ");
        }

        // get the tokens of the data
        String[] tokens = rawData.substring(2).split(" ");

        // if this is a moment
        if(rawData.charAt(0) == '.') {
            this.isSpan = false;
            this.endSeconds = startSeconds + 1;
        // if this is a span
        } else if(rawData.charAt(0) == '<') {
            this.isSpan = true;
            this.endSeconds = inputTimeToSeconds(tokens[1]);
        }
        this.startSeconds = inputTimeToSeconds(tokens[0]);
        this.startTime = tokens[0];
        this.endTime = tokens[1];

        // set the name of the event
        String eventName = rawData.substring(rawData.indexOf("\""));
        this.event = eventName.substring(0, eventName.length() - 1);

        // set the color of the event
        this.c = new Color(Integer.valueOf(tokens[2]), Integer.valueOf(tokens[3]), Integer.valueOf(tokens[4]), Integer.valueOf(tokens[5]));
        
        // set the day location of the event
        this.startPercent = this.startSeconds / 86400.0;
        this.endPercent = this.endSeconds / 86400.0;
        // set the display location of the event
        this.start = (int) (startPercent * displayH);
        this.end = (int) (endPercent * displayH);
    }
    private int scheduleDisplayY;
    private int PREF_W;
    public void setGraphicsConstants(int scheduleDisplayY, int PREF_W) {
        this.scheduleDisplayY = scheduleDisplayY;
        this.PREF_W = PREF_W;
    }

    public Rectangle getVisualBounds() {
        return new Rectangle(0, this.start + this.scheduleDisplayY, this.PREF_W, this.end - (this.start));
    }

    public void paint(java.awt.Graphics2D g2) {
        g2.setColor(this.c);
        g2.fill(this.getVisualBounds());
        g2.setColor(Color.BLACK);

        String textToDisplay = this.event.substring(1);
        if(hovering)
            textToDisplay = startTime + " -> " + endTime;

        if (this.isSpan)
            g2.drawString(textToDisplay, 10, 5 + scheduleDisplayY + this.start + (this.end - this.start) / 2);
    }

    @Override
    public void onHover(int mouseX, int mouseY) {
        hovering = true;
    }

    @Override
    public void onUnHover(int mouseX, int mouseY) {
        hovering = false;
    }
}