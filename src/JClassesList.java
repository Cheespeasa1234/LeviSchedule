import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JClassesList extends JPanel {

    public ArrayList<JClassEntry> classes = new ArrayList<JClassEntry>();
    private ActionListener onListChange;
    private boolean deleting = false;

    /**
     * Makes a panel for configuring the classes.
     * 
     * @param variables
     *            The variables that can be modified. Will only modify variables
     *            with FIVE parameters.
     */
    public JClassesList(HashMap<String, String> variables, ActionListener onListChange) {
        this.onListChange = onListChange;
        setLayout(new GridLayout(0, 1));
        // create a plus and minus pill

        JPillButtonMember plus = new JPillButtonMember("+", 30, 30);
        JPillButtonMember minus = new JPillButtonMember("-", 30, 30);
        minus.addActionListener(e -> {
            deleting = !deleting;
        });

        JTextField newClassName = new JTextField("New Class");
        JButton newClassColor = new JButton("");
        JButton newClassAdd = new JButton("+");
        newClassAdd.addActionListener(e -> {
            String[] params = { 
                "" + newClassColor.getBackground().getRed(),
                "" + newClassColor.getBackground().getGreen(),
                "" + newClassColor.getBackground().getBlue(), 
                "" + newClassColor.getBackground().getAlpha(), "\"" + newClassName.getText() + "\"" 
            };

            addClass(params);
        });
        newClassAdd.addActionListener(onListChange);

        JClassEntry newClass = new JClassEntry(newClassName, newClassColor, newClassAdd);
        add(newClass);

        add(new JLabel("Configure Classes"));

        for (String key : variables.keySet()) {
            String[] params = LeviScheduleLoader.splitArgs(variables.get(key));
            System.out.println("Params: " + variables.get(key));
            if (params.length == 5) {
                addClass(params);
            }
        }
    }

    public void addClass(String[] params) {
        Color c = new Color(Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                Integer.parseInt(params[2]));

        JTextField className = new JTextField(params[4]);

        JButton color = new JButton();
        
        JButton del = new JButton("-");

        JClassEntry entry = new JClassEntry(className, color, del);
        add(entry);
        classes.add(entry);

        del.addActionListener(e -> {
            
                remove(entry);
                classes.remove(entry);
                revalidate();
                repaint();
            
        });
    }
}
