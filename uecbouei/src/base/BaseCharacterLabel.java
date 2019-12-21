package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* キャレクターの始祖オブジェクト */
// 兵士や塔オブジェクトの親
// ヒットポイントと自身の画像のみ保持


/* Object: キャラクターの表示 */
/////////////////////////////////////////////////////////
public class BaseCharacterLabel extends JLabel {
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