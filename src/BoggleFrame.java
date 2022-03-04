import javax.swing.*;
import java.awt.*;

public class BoggleFrame extends JFrame
{
    private BogglePanel gamePanel;

    public BoggleFrame()
    {
        super("Boggle");
        gamePanel = new BogglePanel();
        this.getContentPane().add(gamePanel);
        this.getContentPane().setLayout(new GridLayout(1,1));
        this.setSize(600,400);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
