import javax.swing.*;
import java.awt.*;

class CursorLabel extends JLabel {
    private ImageIcon arrow;
    public CursorLabel(int fontSize) {
        this.setFont(new Font("ＭＳ ゴシック", Font.BOLD, fontSize));
        this.setText("→");
    }
}

class CursorLabelTest extends JFrame {
    public CursorLabelTest() {
        CursorLabel cursor = new CursorLabel(16);
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(cursor, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public static void main(String argv[]) {
        new CursorLabelTest();
    }
}