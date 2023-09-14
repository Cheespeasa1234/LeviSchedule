import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JCreatePresetArea extends JPanel {

    JLabel presetName, presetLbl;
    JButton createDelete;
    JComboBox<String> presetBox;
    JPillButtonMember createPreset, deletePreset;
    String[] presetList;
    public JCreatePresetArea(String[] presets, ActionListener listener) {
        this.presetList = presets;
        setLayout(new GridLayout(3, 2));
        presetLbl = new JLabel("Selected Preset:");
        presetName = new JLabel("default");
        presetBox = new JComboBox<String>(presets);
        presetBox.addActionListener(e -> {
            if (presetBox.getSelectedItem() == null || presetBox.getSelectedIndex() == 0) {
                return;
            }
            System.out.println("Selected: " + presetBox.getSelectedItem().toString());
            presetName.setText(presetBox.getSelectedItem().toString());
            presetBox.setSelectedItem("Select...");
        });
        presetBox.addActionListener(listener);
        
        createPreset = new JPillButtonMember("+", 10, 30);
        createPreset.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter the name of the new preset.");
            if (name == null || name.equals("")) {
                return;
            }
            ArrayList<String> presetArr = new ArrayList<String>(Arrays.asList(presetList));
            if (presetArr.contains(name)) {
                JOptionPane.showMessageDialog(null, "Preset already exists.");
                return;
            }
            
            // add to preset list
            presetArr.add(name);
            this.presetList = presetArr.toArray(new String[presetArr.size()]);
            presetBox.addItem(name);
            presetBox.setSelectedItem("Select...");
            presetName.setText(name);
        });
        
        deletePreset = new JPillButtonMember("-", 10, 30);
        deletePreset.addActionListener(e -> {
            JOptionPane selector = new JOptionPane();
            ArrayList<String> presetArr = new ArrayList<String>(Arrays.asList(presetList));
            JComboBox<String> presetBox2 = new JComboBox<String>(presetList);
            presetBox2.removeItemAt(0);
            selector.setMessage(new Object[] { "Select a preset to delete:", presetBox2 });
            selector.setMessageType(JOptionPane.QUESTION_MESSAGE);
            selector.setOptionType(JOptionPane.OK_CANCEL_OPTION);
            selector.createDialog(null, "Delete Preset").setVisible(true);
            if (selector.getValue() == null || selector.getValue().equals(JOptionPane.UNINITIALIZED_VALUE) || presetBox2.getSelectedItem() == null || selector.getValue().equals(JOptionPane.CANCEL_OPTION) ) {
                return;
            }
            
            int index = presetBox2.getSelectedIndex();
            if (index == -1) {
                return;
            }
            System.out.println("INDEX: " + index);
            if (index < 2) {
                JOptionPane.showMessageDialog(null, "Cannot delete default presets.");
                return;
            }
            
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete preset " + presetBox2.getSelectedItem() + "?", "Delete Preset", JOptionPane.YES_NO_OPTION);
            if (confirmed != JOptionPane.YES_OPTION) {
                return;
            }
            
            presetArr.remove(index + 1);
            
            this.presetList = presetArr.toArray(new String[presetArr.size()]);
            System.out.println("Preset List: " + Arrays.toString(presetList));
            presetBox.removeItemAt(index);
            presetName.setText(presetBox.getSelectedItem().toString());
        });
        
        createDelete = new JButton();
        createDelete.setLayout(new GridLayout(1,2));
        createDelete.setPreferredSize(new Dimension(30, 30));
        createDelete.setBackground(Color.WHITE);
        createDelete.setFocusPainted(false);
        createDelete.setFocusable(false);

        createDelete.add(createPreset, BorderLayout.WEST);
        createDelete.add(deletePreset, BorderLayout.EAST);

        this.add(presetLbl);
        this.add(presetName);
        this.add(presetBox);
        this.add(createDelete);
    }
}
