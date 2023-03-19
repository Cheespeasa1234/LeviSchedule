import java.awt.Color;
import java.util.HashMap;

public class ScheduledEvent {
    // maps time "HH:mm" >> event "Event"
    public double startSeconds;
    public double endSeconds;
    public double startPercent;
    public double endPercent;
    public int start;
    public int end;

    public boolean isSpan; // does it have beginning and end
    public String event;
    public Color c;

    public static int inputTimeToSeconds(String in) {
        String[] tokens = in.split(":");
        int[] hs = new int[]{Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1])};
        int secs = (hs[0] * 60 * 60) + hs[1] * 60;
        return secs;
    }

    public ScheduledEvent(String rawData, int displayH, HashMap<String, String> vars) {

        // if given var:
        if (rawData.indexOf("$") > -1) {
            String var = rawData.substring(rawData.indexOf("$") + 1);
            System.out.print("VAR: " + var + " || ");
            String val = vars.get(var);
            System.out.print("VAL: " + val + " || ");
            rawData = rawData.replace("$" + var, val);
            System.out.println("RAW: " + rawData + " || ");
        }

        String[] tokens = rawData.substring(2).split(" ");
        if(rawData.charAt(0) == '.') {
            this.isSpan = false;
            this.endSeconds = startSeconds + 1;
        } else if(rawData.charAt(0) == '<') {
            this.isSpan = true;
            this.endSeconds = inputTimeToSeconds(tokens[1]);
        }
        this.startSeconds = inputTimeToSeconds(tokens[0]);

        String eventName = rawData.substring(rawData.indexOf("\""));
        this.event = eventName;

        this.c = new Color(Integer.valueOf(tokens[2]), Integer.valueOf(tokens[3]), Integer.valueOf(tokens[4]), Integer.valueOf(tokens[5]));
        
        this.startPercent = this.startSeconds / 86400.0;
        this.endPercent = this.endSeconds / 86400.0;

        this.start = (int) (startPercent * displayH);
        this.end = (int) (endPercent * displayH);
    }
}
