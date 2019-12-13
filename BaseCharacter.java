import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* キャレクターの始祖オブジェクト */
// 兵士や塔オブジェクトの親
// ヒットポイントと自身の画像のみ保持

/* Model: ヒットポイントと画像の場所を保持. */
/////////////////////////////////////////////////////////
class BaseCharacterModel {
    private int maxHitPoint;
    private int hitPoint;
    private String imagePath;

    public BaseCharacterModel(int hp, String pathString) {
        if(hp < 0){ hp = 0; } // 不適切なヒットポイントの修正
        maxHitPoint = hp;
        hitPoint = hp;
        imagePath = pathString;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String pathString) {
        imagePath = pathString;
    }

    /* ヒットポイントを返す. */
    public int getHitPoint() {
        return hitPoint;
    }

    /* このキャラクターは絶命した? */
    public boolean isDead() {
        return hitPoint <= 0;
    }

    /* damageが0以上なら損傷、0未満なら治療. */
    public void giveDamage(int damage) {
        hitPoint -= damage;
        if(hitPoint < 0)           { hitPoint = 0; }
        if(hitPoint > maxHitPoint) { hitPoint = maxHitPoint; }
    }
}


/* Object: キャラクターの表示 */
/////////////////////////////////////////////////////////
class BaseCharacterLabel extends JLabel {
    private BaseCharacterModel model;

    public BaseCharacterLabel(BaseCharacterModel bcm) {
        model = bcm;
        this.setCharacterImage();
    }

    /* キャラクターの画像をセット */
    public void setCharacterImage() {
        ImageIcon icon = new ImageIcon(model.getImagePath());
        this.setIcon(icon);
    }
}


/* Test: 描画テスト用クラス */
/////////////////////////////////////////////////////////
class BaseCharacterTest extends JFrame implements KeyListener {
    private BaseCharacterModel model;
    private BaseCharacterLabel chara;
    
    public BaseCharacterTest() {
        model = new BaseCharacterModel(5, "./sampleChara.png");
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
            model.setImagePath("./sampleChara2.png");
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
