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
        maxHitPoint = hp;
        hitPoint = hp;
        imagePath pathString;
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
    private model;

    public void BaseCharacterLabel(BaseCharacterModel bcm) {
        model = bcm;
        this.// 画像をこいつに貼り付ける
    }
}