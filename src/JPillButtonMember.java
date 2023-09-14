import java.awt.Dimension;

import javax.swing.JButton;

public class JPillButtonMember extends JButton {
    public JPillButtonMember(String n, int w, int h) {
        super(n);
        setFocusPainted(false);
        setPreferredSize(new Dimension(w, h));
        setOpaque(false);
        setBorder(null);
        setContentAreaFilled(false);

        // add hover
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setOpaque(true);
                setContentAreaFilled(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setOpaque(false);
                setContentAreaFilled(false);
            }
        });
    }
}
