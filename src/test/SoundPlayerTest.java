package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;


/* Test: BGMテスト用クラス */
/////////////////////////////////////////////////////////
class SoundPlayerTest extends JFrame implements KeyListener {
    JLabel label;
    SoundPlayer sp;

    public SoundPlayerTest() {
        label = new JLabel("テストですテストですテストです");
        this.addKeyListener(this); // キー入力受付

        /* 表示のための各種セッティング */
        this.setTitle("BGM テスト");
        this.add(label, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        sp = SoundPlayer.getInstance();
        sp.playBGM("test/sample.wav");
    }

    /* キーを押された時の処理 */
    public void keyPressed(KeyEvent e) {
        /* 何かのキー押したら変更 */
        label.setText("これはテストかもしれない");
        sp.killBGM();
        this.revalidate();
        this.repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    /* main関数 */
    public static void main(String argv[]) {
        new SoundPlayerTest();
    }
}