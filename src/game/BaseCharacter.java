package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

class BaseCharacter {
    private int x = 0;//x座標
    private int y = 0;//y座標
    private int attackPoint;//人に対する攻撃力
    private int attackToBuilding;//建物に対する攻撃力
    private int maxHitPoint;//最大HP
    private int hitPoint;//HP
    private String imagePath;//キャラのグラフィックの実装を変えたとき陽
    private String name; //キャラの名前
    private int player;//プレイヤー1か2か
    BufferedImage [] icon = new BufferedImage [4];
    BufferedImage graphic;
    private char classType;//キャラクターを表す記号。いまのところこれとタイルを対応させてる
    private int speed;//何マス動けるか
    private int cost;//召喚するのにマナいくつ必要か
    private boolean moveSelected;//移動したか。1ターンに移動は一回だけ.trueはし終わった状態を指す
    private boolean battleSelected;//攻撃したか//1ターンに攻撃は1回だけ
    public BaseCharacter(int hp, String name, int x, int y,int player, char classType, int attackPoint, int attackToBuilding, int speed, int cost) {
        if(hp < 0){ hp = 0; } // 不適切なヒットポイントの修正
        maxHitPoint = hp;
        hitPoint = hp;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.classType = classType;
        this.attackPoint = attackPoint;
        this.attackToBuilding = attackToBuilding;
        this.speed = speed;
        this.cost = cost;

        try {
            setGraphic(classType); //画像をここでセット
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println("ErrorInSetGraphic");
        }
        moveSelected = false;
        battleSelected = false;
    }
    public String getImagePath() {//ファイル名を返す
        return imagePath;
    }
    public void setImagePath(String pathString) {//セッター
        imagePath = pathString;
    }
    public String getName() {//キャラ名を取得
        return name;
    }
    public int getPlayer() {//キャラがどのプレイヤーのものか取得
        return player;
    }
    public void move(int x, int y) {//移動処理
        this.x = x;
        this.y = y;
    }
    public Point getPosition() {//現座標をPoint型で返す
        Point p = new Point();
        p.x = x;
        p.y = y;
        return p;
    }
    public char getClassType() {//クラス名を取得.配列に対応させる時に使う
        return classType;
    }
    /* ヒットポイントを返す. */
    public int getHitPoint() {//今のHPを取得
        return hitPoint;
    }
    public int getAttackPoint() {//人に対する攻撃力を取得
        return attackPoint;
    }
    public int getAttackToBuilding() {//拠点に対する攻撃力
        return attackToBuilding;
    }
    /* このキャラクターは絶命した? */
    public boolean isDead() {//志望判定
        return hitPoint <= 0;
    }
    public int getSpeed() {//動けるマス数を取得
        return speed;
    }
    public int getCost() {//召喚に必要なマナを取得
        return cost;
    }
    public boolean getMoveSelected() {//移動したか
        return moveSelected;
    }
    public boolean getBattleSelected() {//攻撃したか
        return battleSelected;
    }
    /* damageが0以上なら損傷、0未満なら治療. */
    public void giveDamage(int attackPoint) {//ダメージ処理
        hitPoint -= attackPoint;//ダメージ演算
        if(hitPoint < 0) {//0以下にになったら0にする
            hitPoint = 0;
        } else if(hitPoint > maxHitPoint) {//回復したりしたときに最大HPを越さないようにする
            hitPoint = maxHitPoint;
        }
    }
    public void setMoveSelected(boolean flag) {//ターンの初めに移動しなおせるようにする
        moveSelected = flag;
    }
    public void setBattleSelected(boolean flag) {//ターンの初めに攻撃しなおせるようにする
        battleSelected = flag;
    }
    public void setGraphic(char character) throws IOException{//グラフィックをセットする
        ImportTile tile = null;
        if(character == '0') {
            if(player == 1) {
                tile = new ImportTile("game/Chara1A.png");
            } else {
                tile = new ImportTile("game/Chara1B.png");
            }
        } else if(character == '1') {
            if(player == 1) {
                tile = new ImportTile("game/Chara2A.png");
            } else {
                tile = new ImportTile("game/Chara2B.png");
            }
        } else if(character == '2') {
            if(player == 1) {
                tile = new ImportTile("game/Chara3A.png");
            } else {
                tile = new ImportTile("game/Chara3B.png");
            }
        } else if(character == '3') {
            if(player == 1) {
                tile = new ImportTile("game/Chara4A.png");
            } else {
                tile = new ImportTile("game/Chara4B.png");
            }
        } else if(character == '4') {
            if(player == 1) {
                tile = new ImportTile("game/Chara5A.png");
            } else {
                tile = new ImportTile("game/Chara5B.png");
            }
        } else if(character == 'E') {
            tile = new ImportTile("game/Character.png");
            if(player == 1) {
                icon[1] = tile.getTile('E');
                icon[2] = tile.getTile('E');
                icon[3] = tile.getTile('E');
                icon[0] = tile.getTile('E');
            } else {
                icon[0] = tile.getTile('D');
                icon[1] = tile.getTile('D');
                icon[2] = tile.getTile('D');
                icon[3] = tile.getTile('D');
            }
            graphic = icon[0];
            return;
        }
        icon[0] = tile.getTile('0');
        icon[1] = tile.getTile('4');
        icon[2] = tile.getTile('8');
        icon[3] = tile.getTile('C');
        graphic = icon[0];
    }
    public BufferedImage getGraphic() throws IOException{//グラフィックのゲッター
        return graphic;
    }
    public void setDirection(int n) {//方向転換
        graphic = icon[n];
    }
}