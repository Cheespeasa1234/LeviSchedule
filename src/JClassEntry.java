import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JClassEntry extends JPanel {

    public boolean scared = false;

    public JClassEntry(JTextField name, JButton color, JButton action) {
        setLayout(new GridLayout(0, 2));
        add(name);
        JPanel btns = new JPanel();
        btns.add(color);
        btns.add(action);
        add(btns);
    }
}
