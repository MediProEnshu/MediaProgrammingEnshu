import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.System;

/* セレクトオブジェクト */

/* Model: BaseCommandの集合とカーソルの位置を保持, コマンドで動かしたいオブジェクトに監視させる. */
/////////////////////////////////////////////////////////
class BaseSelectModel extends Observable {
    private ArrayList<BaseCommandModel> commands;
    private int selectPosition, maxPosition;
    private boolean isDetermined;

    public BaseSelectModel(String[] cmdTexts) {
        isDetermined = false;
        BaseCommandModel cmd;
        maxPosition = 0;
        commands = new ArrayList<BaseCommandModel>();
        for(String s : cmdTexts) {
            cmd = new BaseCommandModel(maxPosition, s);
            commands.add(cmd);
            maxPosition++;
        }
    }

    public ArrayList<BaseCommandModel> getCommands() {
        return commands;
    }

    public int getCommandSize() {
        return maxPosition;
    }

    public int getCurrentPosition() {
        while(selectPosition < 0) { selectPosition += maxPosition; }
        return selectPosition % maxPosition;
    }

    public boolean getIsDetermined() {
        return isDetermined;
    }

    /* 監視から逃れる */
    public void fleezeSelect() {
        this.deleteObservers();
    }

    public void determined() {
        isDetermined = true;
        setChanged();
        notifyObservers();
    }

    public void upped() {
        selectPosition--; //上の選択肢ほど小さい値であることに注意.
        setChanged();
        notifyObservers();
    }

    public void downed() {
        selectPosition++; //上の選択肢ほど小さい値であることに注意.
        setChanged();
        notifyObservers();
    }
}


/* View: BaseCommandの集合とカーソルの位置を表示 */
/////////////////////////////////////////////////////////
class BaseSelectView extends JPanel {
    JPanel cursorPanel, cmdsPanel;
    ArrayList<BaseCommandModel> commands;
    BaseSelectModel model;
    CursorLabel cursor;
    int cmdSize;

    public BaseSelectView(BaseSelectModel bsm) {
        model = bsm;
        commands = model.getCommands();
        cmdSize = model.getCommandSize();
        cursorPanel = new JPanel();
        cmdsPanel = new JPanel();
        cursor = new CursorLabel(/* font size = */ 16);

        cursorPanel.setLayout(new GridLayout(cmdSize, 1));
        cmdsPanel.setLayout(new GridLayout(cmdSize, 1));
        for(BaseCommandModel m : commands) {
            BaseCommandLabel l = new BaseCommandLabel(m);
            cmdsPanel.add(l);
        }
        cursorPanel.add(cursor);
        this.add(cursorPanel, BorderLayout.WEST);
        this.add(cmdsPanel, BorderLayout.EAST);
    }

    public void changeCursorPosition(int pos) {
        this.removeAll();
        cursorPanel = new JPanel();
        cursorPanel.setLayout(new GridLayout(cmdSize, 1));
        for(int i = 0; i < pos; i++) {
            cursorPanel.add(new JLabel());
        }
        cursorPanel.add(cursor);
        this.add(cursorPanel, BorderLayout.WEST);
        this.add(cmdsPanel, BorderLayout.EAST);
    }

    public void updateLabel() {
        int pos = model.getCurrentPosition();
        this.changeCursorPosition(pos);
    }
}


/* Controller: 決定されたときのコマンドを返す */
/////////////////////////////////////////////////////////
class BaseSelectController implements KeyListener {
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