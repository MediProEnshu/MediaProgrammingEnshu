package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* キャレクターの始祖オブジェクト */
// 兵士や塔オブジェクトの親
// ヒットポイントと自身の画像のみ保持

/* Model: ヒットポイントと画像の場所を保持. */
/////////////////////////////////////////////////////////
public class BaseCharacterModel {
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