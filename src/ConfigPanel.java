import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigPanel extends JPanel {
    String[] presets = {"delayed", "default"};

    // EACH DAY OF WEEK HAS AN ENTRY
    // WILL STORE:
    // - .sched formatted data

    // EACH PRESET STORES THE DAYS

    public ConfigPanel() {

        this.setLayout(new GridLayout(3, 2));

        JLabel presetLbl = new JLabel("Selected Preset:");
        JComboBox<String> presetBox = new JComboBox<String>(presets);

        JLabel createPresetNameLbl = new JLabel("Create Preset Name:");
        JTextField createPresetName = new JTextField("");
        
        JButton createPreset = new JButton("+");
        createPreset.addActionListener(e -> {
            String name = createPresetName.getText();
            createPresetName.setText("");
            if (name.length() == 0 || name.contains(" ")) return;
            for (String preset : presets) {
                if (preset.equals(name)) return;
            }

            String[] newPresets = new String[presets.length + 1];
            for (int i = 0; i < presets.length; i++) {
                newPresets[i] = presets[i];
            }
            newPresets[presets.length] = name;
            presets = newPresets;
            presetBox.addItem(name);
            presetBox.setSelectedItem(name);
        });

        this.add(presetLbl);
        this.add(presetBox);
        this.add(createPresetNameLbl);
        this.add(createPresetName);
        this.add(createPreset);
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
