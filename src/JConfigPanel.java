import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class JConfigPanel extends JPanel {
    String[] presets = { "Select...", "default", "delayed" };
    JCreatePresetArea presetArea;
    JConfigMainArea mainArea;

    // makes a panel to configure the events list, settings, and presets
    public JConfigPanel(Font f) {
        
        HashMap<String, String> variables = new HashMap<String, String>();
        LeviScheduleLoader.loadScopt(new File("schedules/classes.scopt"), variables);

        ArrayList<ArrayList<ScheduledEvent>> events = new ArrayList<ArrayList<ScheduledEvent>>();
        LeviScheduleLoader.loadSch(new File("schedules/default.sch"), events, variables);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); 
        Border comp = new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(10,10,10,10));
        
        presetArea = new JCreatePresetArea(presets, e -> {
            mainArea.setPreset(presetArea.presetName.getText());
            System.out.println("IN THE LISTENER");
        });

        presetArea.setBorder(comp);

        mainArea = new JConfigMainArea(events);
        mainArea.setBorder(comp);
        mainArea.setPreferredSize(new Dimension(800, 600));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints leftGBC = new GridBagConstraints();
        leftGBC.fill = GridBagConstraints.BOTH;
        leftGBC.weightx = 1;
        leftGBC.weighty = 0.8;
        leftGBC.gridx = 0;
        leftGBC.gridy = 0;
        leftPanel.add(mainArea, leftGBC);

        JButton saveButton = new JButton("Save");
        leftGBC.gridy = 1;
        leftPanel.add(saveButton, leftGBC);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints rightGBC = new GridBagConstraints();
        rightGBC.fill = GridBagConstraints.BOTH;
        rightGBC.weightx = 1;
        rightGBC.weighty = 0.25;
        rightGBC.gridx = 0;
        rightGBC.gridy = 0;
        rightPanel.add(presetArea, rightGBC);

        JScrollPane classesPane = new JScrollPane();
        JClassesList classesList = new JClassesList(variables, e -> {
            classesPane.revalidate();
            classesPane.repaint();
        });
        classesPane.setViewportView(classesList);
        classesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        classesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightGBC.gridy = 1;
        rightGBC.weighty = 0.75;
        rightPanel.add(classesPane, rightGBC);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        add(leftPanel, c);
        c.gridx = 1;
        add(rightPanel, c);
    }
    
    public String join(String arr, String str) {
        String ret = "";
        for (int i = 0; i < arr.length(); i++) {
            ret += arr.charAt(i);
            if (i != arr.length() - 1)
                ret += str;
        }
        return ret;
    }

    public void saveChangesToFiles() {

    }
}
