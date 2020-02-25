package game;

import base.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;

class GameState {//ゲームの全体の状態を統括。大体目に見えない部分を処理
    private boolean moveFlag;//カーソルの判定が移動になってるか
    private boolean summonFlag;//カーソルの判定が召喚になってるか
    private boolean battleFlag;//カーソルの判定がこうげきになってるか。//つまるとこ画面左のどのボタンが押されたか
    private int player1Mana;//プレイヤー1の残りマナ
    private int player2Mana;//プレイヤー2の残りマナ
    private int nowPlayer;//今操作を行えるプレイヤーを表す
    private BaseCharacter nowSummon;//今召喚カーソルをクリックして召喚できるキャラクターを表す
    private char nowTile = '.';//マップエディタで何のタイルを上書きするか決めるもの
    private boolean makegraphic = true;
    DynamicTextModel manaTextModel;//マナの表示に関わるモデル
    private int deathPlayer1Character;//リザルト画面で使う死亡数
    private int deathPlayer2Character;
    public GameState() {//コンストラクタ初期マナと初期プレイヤーとかを設定
        moveFlag = false;
        summonFlag = false;
        battleFlag = false;
        player1Mana = 10;
        player2Mana = 10;
        nowPlayer = 1;
        deathPlayer1Character = 0;
        deathPlayer2Character = 0;
        manaTextModel = new DynamicTextModel("player1のマナ" + player1Mana + "player2のマナ" + player2Mana);
    }
    public void setMove() {//スイッチを押したときに移動できる状態にする
        moveFlag = true;
        summonFlag = false;
        battleFlag = false;
    }
    public void setSummon() {//スイッチを押したときに召喚できる状態にする
        moveFlag = false;
        summonFlag = true;
        battleFlag = false;
    }
    public void setBattle() {//スイッチを押したときに戦闘できる状態にする
        moveFlag = false;
        summonFlag = false;
        battleFlag = true;
    }
    public boolean getMoveFlag() {//各getter
        return moveFlag;
    }
    public boolean getSummonFlag() {
        return summonFlag;
    }
    public boolean getBattleFlag() {
        return battleFlag;
    }
    public void setNowPlayer(int player) {//現在操作してるプレイヤーは誰かセットする
        nowPlayer = player;
    }
    public int getNowPlayer() {//現在操作しているプレイヤーは誰か返す
        return nowPlayer;
    }
    public void setPlayer1Mana(int cost) {//マナの加減算処理(プレイヤー１)
        player1Mana -= cost;
        if(player1Mana < 0) {
            player1Mana = 0;
        }
        manaTextModel.changeText("player1のマナ" + player1Mana + "player2のマナ" + player2Mana);
    }
    public void setPlayer2Mana(int cost) {//マナの加減算処理(プレイヤー2)
        player2Mana -= cost;
        if(player2Mana < 0) {
            player2Mana = 0;
        }
        manaTextModel.changeText("player1のマナ" + player1Mana + "player2のマナ" + player2Mana);
    }
    public int getPlayer1Mana() {//今のマナを取得
        return player1Mana;
    }
    public int getPlayer2Mana() {//同様
        return player2Mana;
    }
    public void setNowSummon(BaseCharacter character) {//召喚ボタンを押してキャラクターをクリックした後に処理
        nowSummon = character;
    }
    public void setNowTile(char tile) {//マップエディタでつかういまクリックしたときになんのタイルを置くかセットする
        nowTile = tile;
    }
    public char getNowTile() {//クリックしたときに何のタイルがおかれるか取得する
        return nowTile;
    }
    public BaseCharacter getNowSummon() {//今召喚するキャラを取得
        return nowSummon;
    }
    public void setMakeGraphic(boolean flag) {//半透明な選択範囲の画像を描画するかしないか。処理軽減のためにある
        makegraphic = flag;
    }
    public boolean getMakeGraphic() {//半透明な選択範囲の画像を描画するかしないか取得する
        return makegraphic;
    }
    public void setDeathCharacter(int player) {//キャラクターが死んだときに数を増やす
        if(player == 1) {
            deathPlayer1Character++;
        } else {
            deathPlayer2Character++;
        }
    }
}

class Ippan extends BaseCharacter {//一般大学生
    private char c;
    public Ippan(int x, int y, int player) {
        super(5, "一般大学生", x, y, player, '0', 3, 2, 4, 3);//(int hp, String name, int x, int y,int player, char classType, int attackPoint, int attackToBuilding, int speed, int cost) {
    }
}

class Otaku extends BaseCharacter {//オタク。移動が強い
    public Otaku(int x, int y, int player) {
        super(3, "オタク", x, y, player, '1', 2, 1, 6, 5);//オタクくんさあ
    }
}

class Yokya extends BaseCharacter {//陽キャ。拠点に対する攻撃力が高い
    public Yokya(int x, int y, int player) {
        super(7, "陽キャ", x, y, player, '2', 4, 5, 4, 7);//WANIMA的な
    }
}

class Idol extends BaseCharacter {//アイドル。敵味方問わずHPを回復させる。拠点には1ダメージ与えれる
    public Idol(int x, int y, int player) {
        super(3, "アイドル", x, y, player, '3', -2, 1, 4, 5);
    }
}

class Kyoju extends BaseCharacter {//教授。人に対して高い攻撃力だけど拠点には何もできない
    public Kyoju(int x, int y, int player) {
        super(10, "教授", x, y, player, '4', 8, 0, 4, 10);
    }
}

class Kyoten extends BaseCharacter {//拠点
    public Kyoten(int x, int y, int player) {
        super(25, "拠点", x, y, player, 'E', 0, 0, 4, 0);//ひとまず
    }
}

public class GamePanel extends JPanel implements ActionListener{
    GameScreen screen;
    JButton b1=new JButton("召喚"),b2=new JButton("移動");
    JButton b3=new JButton("攻撃"),b4=new JButton("ターンエンド");
    ImportTile tile = new ImportTile("game/Character.png");
    JButton b5=new JButton(new ImageIcon(tile.getTile('0')));
    JButton b6=new JButton(new ImageIcon(tile.getTile('1')));
    JButton b7=new JButton(new ImageIcon(tile.getTile('2')));
    JButton b8=new JButton(new ImageIcon(tile.getTile('3')));
    JButton b9=new JButton(new ImageIcon(tile.getTile('4')));
    JPanel p3;
    JPanel p4;
    DynamicTextLabel mana;
    DynamicTextLabel textLog;
    DynamicTextLabel mapInfo;
    GameEscape escape;
    public GamePanel(String file, GameEscape ge) throws IOException {
        escape = ge;
        JPanel panel = new JPanel();
        screen = new GameScreen(file, escape);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(screen);
        JPanel  p1=new JPanel(),p2=new JPanel();
        p3 = new JPanel(); p4 = new JPanel();
        // JPanelに貼り付けるためのJButtonを5つ生成
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        // 2つのJPanelをそれぞれ，3x1, 2x1 のGridLayoutに設定．
        p1.setLayout(new GridLayout(4,1));
        p2.setLayout(new GridLayout(2,1));
        b5.addActionListener(this);
        b6.addActionListener(this);
        b7.addActionListener(this);
        b8.addActionListener(this);
        b9.addActionListener(this);
        // ボタンをそれぞれのJPanelに貼付け
        p1.add(b1); p1.add(b2); p1.add(b3);p1.add(b4);
        p3.setLayout(new GridLayout(3, 2));
        p3.add(b5); p3.add(b6); p3.add(b7); p3.add(b8); p3.add(b9);
        p2.setLayout(new GridLayout(3, 1));
        textLog = new DynamicTextLabel(screen.modelTextLog);
        mapInfo = new DynamicTextLabel(screen.modelMapInfo);
        mana = new DynamicTextLabel(screen.state.manaTextModel);
        textLog.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
        mapInfo.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
        mana.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 16));
        mana.setBackground(Color.GREEN);
        p2.add(p3);
        p2.add(textLog);
        p2.add(mapInfo);
        p4.add(mana);
        this.setLayout(new BorderLayout());
        this.add(p4, BorderLayout.NORTH);
        this.add(p1,BorderLayout.WEST);
        this.add(p2,BorderLayout.EAST);
        this.add(panel, BorderLayout.CENTER);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==b1) {
            screen.map.haniMapInit();
            screen.rectColor = Color.blue;
            screen.state.setSummon();
            BaseCharacter c = screen.map.getKyoten(screen.state.getNowPlayer());
            screen.map.paintSummonRange(c.getPosition().x/32, c.getPosition().y/32, c.getSpeed());
            p3.setVisible(true);
        } else if(e.getSource()==b2) {
            screen.map.haniMapInit();
            screen.rectColor = Color.red;
            screen.state.setMove();
            p3.setVisible(false);
        } else if(e.getSource() == b3) {
            screen.map.haniMapInit();
            screen.rectColor = Color.yellow;
            screen.state.setBattle();
            p3.setVisible(false);
        } else if(e.getSource() == b4) {
            screen.map.haniMapInit();
            if(screen.state.getNowPlayer() == 1) {
                screen.state.setNowPlayer(2);
                screen.state.setPlayer1Mana(-5);
                screen.map.reChracterMoveandBattle(1);
            } else {
                screen.state.setNowPlayer(1);
                screen.state.setPlayer2Mana(-5);
                screen.map.reChracterMoveandBattle(2);
            }
            p3.setVisible(false);
        } else if(e.getSource() == b5) {
            screen.state.setNowSummon(new Ippan(0, 0, screen.state.getNowPlayer()));
        } else if(e.getSource() == b6) {
            screen.state.setNowSummon(new Otaku(0, 0, screen.state.getNowPlayer()));
        } else if(e.getSource() == b7) {
            screen.state.setNowSummon(new Yokya(0, 0, screen.state.getNowPlayer()));
        } else if(e.getSource() == b8) {
            screen.state.setNowSummon(new Idol(0, 0, screen.state.getNowPlayer()));
        } else if(e.getSource() == b9) {
            screen.state.setNowSummon(new Kyoju(0, 0, screen.state.getNowPlayer()));
        }
        screen.repaint();
    }
}
