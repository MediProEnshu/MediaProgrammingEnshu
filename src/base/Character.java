class BaseCharacter {
    private int x = 0;//x座標
    private int y = 0;//y座標
    private int attackPoint;//人に対する攻撃力
    private int attackToBuilding;//建物に対する攻撃力
    private int maxHitPoint;//最大HP
    private int hitPoint;//HP
    //private String imagePath;//キャラのグラフィックの実装を変えたとき陽
    private String name; //キャラの名前
    private int player;//プレイヤー1か2か
    //BufferedImage graphic;
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
        moveSelected = false;
        battleSelected = false;
    }
    public String getImagePath() {//ファイル名を返す
        return imagePath;
    }
    public void setImagePath(String pathString) {//セッター
        imagePath = pathString;
    }
    public String getName() {
        return name;
    }
    public int getPlayer() {
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
    public void setBattleSelected(boolean flag) {
        battleSelected = flag;
    }
    public void setGraphic(String filepath) throws IOException{//ここ使うか未定
        this.graphic =  ImageIO.read(new File(filepath));
    }
}
class Ippan extends BaseCharacter {//一般大学生
    public Ippan(int x, int y, int player) {
        super(5, "一般大学生", x, y, player, '1', 3, 2, 2, 3);//(int hp, String name, int x, int y,int player, char classType, int attackPoint, int attackToBuilding, int speed, int cost) {
    }
}
class Otaku extends BaseCharacter {//オタク。移動が強い
    public Otaku(int x, int y, int player) {
        super(3, "オタク", x, y, player, '2', 2, 1, 3, 5);//オタクくんさあ
    }
}
class Yokya extends BaseCharacter {//陽キャ。拠点に対する攻撃力が高い
    public Yokya(int x, int y, int player) {
        super(7, "陽キャ", x, y, player, '3', 4, 5, 2, 7);//WANIMA的な
    }
}
class Idol extends BaseCharacter {//アイドル。敵味方問わずHPを回復させる。拠点には1ダメージ与えれる
    public Idol(int x, int y, int player) {
        super(3, "アイドル", x, y, player, '4', -2, 1, 2, 5);
    }
}
class Kyoju extends BaseCharacter {//教授。人に対して高い攻撃力だけど拠点には何もできない
    public Kyoju(int x, int y, int player) {
        super(10, "教授", x, y, player, '5', 8, 0, 2, 10);
    }
}
class Kyoten extends BaseCharacter {//拠点
    public Kyoten(int x, int y, int player) {
        super(25, "拠点", x, y, player, 'E', 0, 0, 0, 0);//ひとまず
    }
}
