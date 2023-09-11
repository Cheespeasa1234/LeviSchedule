import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigPanel extends JPanel {
    HashMap<String, String> classes = new HashMap<String, String>();

    // EACH DAY OF WEEK HAS AN ENTRY
    // WILL STORE:
    // - .sched formatted data

    // EACH PRESET STORES THE DAYS

    public ConfigPanel(String scheduleFolder) {
        File classesOpt = new File(scheduleFolder + "/classes.scopt");
        // get all folders in scheduleFolder
        File[] files = new File(scheduleFolder).listFiles();
        

    }

    public String join(String arr, String str) {
        String ret = "";
        for (int i = 0; i < arr.length(); i++) {
            ret += arr.charAt(i);
            if (i != arr.length() - 1) ret += str;
        }
        return ret;
    }

    public Dimension getPreferredSize() {
        return new Dimension(500, 200);
    }
}
