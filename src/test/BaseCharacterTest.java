package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;

/* キャレクターの始祖オブジェクト */
// 兵士や塔オブジェクトの親
// ヒットポイントと自身の画像のみ保持


/* Test: 描画テスト用クラス */
/////////////////////////////////////////////////////////
class BaseCharacterTest extends JFrame implements KeyListener {
    private BaseCharacterModel model;
    private BaseCharacterLabel chara;

    public BaseCharacterTest() {
        model = new BaseCharacterModel(5, "test/sampleChara.png");
        chara = new BaseCharacterLabel(model);
        this.addKeyListener(this); // キー入力受付

        /* 表示のための各種セッティング */
        this.setTitle("BaseCharacter テスト");
        this.add(chara, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /* キーを押された時の処理 */
    public void keyPressed(KeyEvent e) {
        /* 何かのキー押したらダメージ */
        model.giveDamage(1);

        if(model.isDead()) {
            /* 死んだら、ガイコツの画像に差し替え */
            model.setImagePath("test/sampleChara2.png");
            chara.setCharacterImage();
            /* GUI書き換え */
            this.revalidate();
            this.repaint();
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    /* main関数 */
    public static void main(String argv[]) {
        new BaseCharacterTest();
    }
}
