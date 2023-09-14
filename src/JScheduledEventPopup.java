import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JScheduledEventPopup extends JPanel {
    public JTextField name;
    public JButton color;
    public JTextField startTimeHr, startTimeMin, endTimeHr, endTimeMin;

    public JScheduledEventPopup(ScheduledEvent ev) {
        name = new JTextField(ev.trackedClass.name);
        color = new JButton("*");
        color.setForeground(ev.trackedClass.color);
        String[] startTime = ev.startTime.split(":");
        String[] endTime = ev.endTime.split(":");
    
        startTimeHr = new JTextField(startTime[0]);
        startTimeMin = new JTextField(startTime[1]);
        endTimeHr = new JTextField(endTime[0]);
        endTimeMin = new JTextField(endTime[1]);

        add(name);
        add(color);
        add(startTimeHr);
        add(startTimeMin);
        add(new JLabel(":"));
        add(endTimeHr);
        add(endTimeMin);
    }
}
