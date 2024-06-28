import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
public class RoundedBorder extends AbstractBorder {
    private int radius;
    public RoundedBorder(int radius){
        this.radius=radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
//        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d=(Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(x,y,width-1,height-1,radius,radius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(10,10,10,10);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 10;
        return insets;
    }
}
