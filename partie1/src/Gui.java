import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends JFrame implements Runnable {

    public Gui(JPanel panel) {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setPanel(panel);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                repaint();
            }
        });
    }

    private void setPanel(JPanel panel) {
        this.add(panel);
    }

    @Override
    public void run() {
        this.setVisible(true);
    }
}
