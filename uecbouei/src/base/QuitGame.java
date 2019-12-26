package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class QuitGame implements ActionListener {
    public static final int DELAY = 2000;
    private javax.swing.Timer timer;

    public QuitGame() {
        //指定秒数遅らせるためのtimer
        timer = new javax.swing.Timer(DELAY, this); // 自分のListenerにadd.
        timer.start();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == timer){
            System.exit(0);
        }
    }
}