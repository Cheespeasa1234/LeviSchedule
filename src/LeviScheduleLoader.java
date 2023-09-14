import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import lib.Array;

public class LeviScheduleLoader {
    // Load the class map, which represents your classes and their colors
    public static void loadScopt(File f, HashMap<String, String> variables) {
        try {
            // Set up scanner of the cmap
            Scanner mapReader = new Scanner(f);

            // Keep reading until we reach the end
            while (mapReader.hasNextLine()) {
                String line = mapReader.nextLine();
                String[] tokens = line.split(" = ");
                variables.put(tokens[0], tokens[1]);
            }

            mapReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSch(File f, ArrayList<ArrayList<ScheduledEvent>> events, HashMap<String, String> variables) {
        events.clear();
        try {
            Scanner schedReader = new Scanner(f);
            int day = 0;
            while (schedReader.hasNextLine()) {
                String line = schedReader.nextLine();
                if (line.startsWith("END")) {
                    day++;
                    continue;
                } else if (line.startsWith("START")) {
                    events.add(new ArrayList<ScheduledEvent>());
                    continue;
                }

                // if there is a var
                if (line.contains("$")) {
                    String[] tokens = line.split(" ");
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i].startsWith("$")) {
                            tokens[i] = variables.get(tokens[i].substring(1));
                        }
                    }
                    line = String.join(" ", tokens);
                }

                ScheduledEvent event = new ScheduledEvent(line, 50, variables);
                event.setGraphicsConstants(0, 100);
                events.get(day).add(event);
            }
            schedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load a schedule file into the event storage
    public static void loadSch(File f, String dayCode, ArrayList<ScheduledEvent> events, int scheduleDisplayH,
            int scheduleDisplayY, int PREF_W, HashMap<String, String> variables) {
        // clear the events in case this is during runtime
        events.clear();
        try {
            boolean foundDayStart = false;
            Scanner schedReader = new Scanner(f);
            while (schedReader.hasNextLine()) {

                String line = schedReader.nextLine();
                if (!foundDayStart) {
                    foundDayStart = line.equals("START" + dayCode);
                    continue;
                } else if (foundDayStart && line.equals("END" + dayCode)) {
                    break;
                }

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

    public static void setOption(String key, String value, HashMap<String, String> variables) {
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
            Files.write(config.toPath(), (rewrite + "\nEND").getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] splitArgs(String val) {
        // split by space, but not if in between quotes
        ArrayList<String> args = new ArrayList<String>();
        String current = "";
        boolean inQuotes = false;
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                args.add(current);
                current = "";
            } else {
                current += c;
            }
        }
        args.add(current);
        return args.toArray(new String[0]);
    }
}
