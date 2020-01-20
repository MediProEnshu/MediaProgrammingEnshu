package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.System;

/* セレクトオブジェクト */

/* View: BaseCommandの集合とカーソルの位置を表示 */
/////////////////////////////////////////////////////////
public class BaseSelectView extends JPanel {
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
            l.setFont(new Font("MS ゴシック", Font.PLAIN, 30));
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
