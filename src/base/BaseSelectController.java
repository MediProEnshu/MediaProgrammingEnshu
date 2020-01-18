package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.System;

/* セレクトオブジェクト */

/* Controller: 決定されたときのコマンドを返す */
/////////////////////////////////////////////////////////
public class BaseSelectController implements KeyListener {
    BaseSelectModel model;
    /* キー割当は継承する際に好きなキーに変更して問題ないです */
    /* 複数のキーを1つの操作に割り当てる場合は、KeyPressedをOverRideして書換てください. */
    final int DETERMI_KEY = KeyEvent.VK_ENTER;
    final int UP_KEY = KeyEvent.VK_UP;
    final int DOWN_KEY = KeyEvent.VK_DOWN;

    public BaseSelectController(BaseSelectModel bsm) {
        model = bsm;
    }

    /* キーを押された時の処理 */
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        switch(k){
            /* 決定キーを押された場合 */
            case DETERMI_KEY:
                model.determined();
                break;
            /* 上方向キーを押された場合 */
            case UP_KEY:
                model.upped();
                break;
            /* 下方向キーを押された場合 */
            case DOWN_KEY:
                model.downed();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}