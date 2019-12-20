package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* コマンドオブジェクト */

/* Object: コマンドラベルオブジェクト(Modelから取り出した情報を使う) */
/* Modelを与えられると対応する JLabel+パラメタ を作るオブジェクト. */
/////////////////////////////////////////////////////////
public class BaseCommandLabel extends JLabel {
    public BaseCommandLabel(BaseCommandModel model) {
        this.setText(model.getCommand());
    }
}