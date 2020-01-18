package base;

import javax.swing.*;
import java.awt.*;

/* カーソルオブジェクト */

public class CursorLabel extends JLabel {
    private ImageIcon arrow;
    public CursorLabel(int fontSize) {
        this.setFont(new Font("ＭＳ ゴシック", Font.BOLD, fontSize));
        this.setText("→");
    }
}