//必要になるファイルmap1.txt,MapTile.png,キャラクター.png,Hani.png
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.Color;
class GameState {//ゲームの全体の状態を統括。大体目に見えない部分を処理
    private boolean moveFlag;//カーソルの判定が移動になってるか
    private boolean summonFlag;//カーソルの判定が召喚になってるか
    private boolean battleFlag;//カーソルの判定がこうげきになってるか。//つまるとこ画面左のどのボタンが押されたか
    private int player1Mana;//プレイヤー1の残りマナ
    private int player2Mana;//プレイヤー2の残りマナ
    private int nowPlayer;//今操作を行えるプレイヤーを表す
    private BaseCharacter nowSummon;//今召喚カーソルをクリックして召喚できるキャラクターを表す

    public GameState() {//コンストラクタ初期マナと初期プレイヤーとかを設定
        moveFlag = false;
        summonFlag = false;
        battleFlag = false;
        player1Mana = 10;
        player2Mana = 10;
        nowPlayer = 1;
    }
    public void setMove() {
        moveFlag = true;
        summonFlag = false;
        battleFlag = false;
    }
    public void setSummon() {
        moveFlag = false;
        summonFlag = true;
        battleFlag = false;
    }
    public void setBattle() {
        moveFlag = false;
        summonFlag = false;
        battleFlag = true;
    }
    public boolean getMoveFlag() {
        return moveFlag;
    }
    public boolean getSummonFlag() {
        return summonFlag;
    }
    public boolean getBattleFlag() {
        return battleFlag;
    }
    public void setNowPlayer(int player) {
        nowPlayer = player;
    }
    public int getNowPlayer() {
        return nowPlayer;
    }
    public void setPlayer1Mana(int cost) {//マナの加減算処理(プレイヤー１)
        player1Mana -= cost;
        if(player1Mana < 0) {
            player1Mana = 0;
        }
    }
    public void setPlayer2Mana(int cost) {//マナの加減算処理(プレイヤー2)
        player2Mana -= cost;
        if(player2Mana < 0) {
            player2Mana = 0;
        }
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
    public BaseCharacter getNowSummon() {//今召喚するキャラを取得
        return nowSummon;
    }
}
class GameScreen extends JPanel implements MouseListener{
    static final int startX = 0;
    static final int startY = 0;
    int x;
    int y;
    int tmp_x;
    int tmp_y;
    int width = 0;
    int height = 0;
    int TileSize = 32;
    int rect_x = 0;
    int rect_y = 0;
    char tmp;
    boolean flag = false;
    BaseCharacter tmpl;
    Color rectColor = Color.red;
    GameState state = new GameState();
    boolean rect_flag = false;
    BufferedImage mapImage;
    BufferedImage charaImage;
    BufferedImage panelImage;
    Map map = new Map("map1.txt");
    public GameScreen() throws IOException {
        this.mapImage = createImage("MapTile.png", 1);
        this.charaImage = createImage("キャラクター.png", 2);
        this.panelImage = createImage("Hani.png", 3);
        this.width = mapImage.getWidth();//サイズ設定
        this.height = mapImage.getHeight();
        addMouseListener(this);
        initialize();
    }
    void initialize() {//ゲーム画面の初期位置の設定
        this.x = TileSize * (startX);
        this.y = TileSize * (startY);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        try {
            charaImage = createImage("キャラクター.png", 2);
            panelImage = createImage("Hani.png", 3);
        } catch (Exception e) {
            //TODO: handle exception
        }

        g.drawImage(mapImage, 0, 0, this);
        g.drawImage(charaImage, 0, 0, this);
        g.drawImage(panelImage, 0, 0, this);
        if(state.getMoveFlag() == true || state.getBattleFlag() == true || state.getSummonFlag() == true) {
            g.setColor(rectColor);
            g.drawRect(rect_x, rect_y, TileSize, TileSize);
        }
    }
    public BufferedImage createImage(String fileName, int type) throws IOException{//マップを
        BufferedImage bi = new BufferedImage(TileSize * 124, TileSize * 120, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        ImportTile tileset = new ImportTile(fileName);//タイルセットを呼び出す
        int verticalLength = map.getVerticalLength();//縦の長さ
        int horizontalLength = map.getHorizontalLength();//横の長さ
        int gX = 0;//タイルをどこから書き始めるか。この設定だと大体左上
        for (int x = 0; x < horizontalLength; x++) {
            int gY = 0;
            for (int y = 0; y < verticalLength; y++) {
                if(type == 1) {
                    g.drawImage(tileset.getTile(map.getStageMapCode(x, y)), gX, gY, null);//描画
                } else if(type == 2){
                    g.drawImage(tileset.getTile(map.getCharaMapCode(x, y)), gX, gY, null);//描画
                } else {
                    g.drawImage(tileset.getTile(map.getHaniMapCode(x, y)), gX, gY, null);//描画                   
                }
                gY += TileSize;//タイルの大きさ分横にずらす
            }
            gX += TileSize;//タイルの大きさ分縦にずらす
        }
        return bi;
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();
        Point point = e.getPoint();
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);
            rect_y = point.y- (point.y%TileSize);
            int array_x = rect_x/32;
            int array_y = rect_y/32;
            if(state.getMoveFlag() == true && state.getBattleFlag() == false && state.getSummonFlag() == false) {
                if(map.getCharaMapCode(array_x, array_y) != '.' && map.getCharaPosition(array_x, array_y).getMoveSelected() == false && state.getNowPlayer() == map.getCharaPosition(array_x, array_y).getPlayer()) {
                    tmp_x = rect_x; tmp_y = rect_y;
                    map.paintselectRange(array_x, array_y, map.getCharaPosition(array_x, array_y).getSpeed());
                    map.setCharaFlag(true);
                }else if(map.getCharaMapCode(array_x, array_y) == '.' &&  map.getHaniMapCode(array_x, array_y) == '1') {
                    BaseCharacter c = map.getCharaPosition(tmp_x/32, tmp_y/32);
                    c.move(rect_x, rect_y);
                    c.setMoveSelected(true);
                    map.haniMapInit();
                }
            } else if(state.getMoveFlag() == false && state.getBattleFlag() == false && state.getSummonFlag() == true) {
                BaseCharacter c =  state.getNowSummon();
                if(state.getNowPlayer() == 1) {
                    if(state.getPlayer1Mana() - c.getCost() >= 0) {
                        state.setPlayer1Mana(c.getCost());
                        c.move(rect_x, rect_y);
                        map.addCharacter(c, c.getPlayer());
                        c = null;
                    }
                } else {
                    if(state.getPlayer2Mana() - c.getCost() >= 0) {
                        state.setPlayer2Mana(c.getCost());
                        c.move(rect_x, rect_y);
                        map.addCharacter(c, c.getPlayer());
                        c = null;
                    }
                }    
            } else if (state.getMoveFlag() == false && state.getBattleFlag() == true && state.getSummonFlag() == false && state.getNowPlayer() == map.getCharaPosition(array_x, array_y).getPlayer() && map.getCharaPosition(array_x, array_y).getMoveSelected()){
                if(map.getCharaPosition(array_x, array_y) != null && flag == false) {
                    tmpl = map.getCharaPosition(array_x, array_y);
                    map.paintselectRange(array_x, array_y, tmpl.getSpeed());
                    flag = true;
                } else if(flag == true && map.getHaniMapCode(array_x, array_y) == '1') {
                    BaseCharacter chara = map.getCharaPosition(array_x, array_y);
                    if(chara.getClassType() != 'E') {
                        chara.giveDamage(tmpl.getAttackPoint());
                    } else {
                        chara.giveDamage(tmpl.getAttackToBuilding());
                    }
                    if(chara.isDead() == true) {
                        if(chara.getClassType() != 'E')
                        map.deleteCharacter(chara, chara.getPlayer());
                    } else {
                        map.deleteCharacter(chara, chara.getPlayer());
                        //ゲームセット
                    }
                    flag = false;
                    map.haniMapInit();
                }
            }
        }else if (btn == MouseEvent.BUTTON3){
            rect_flag = false;
        }else if (btn == MouseEvent.BUTTON2){
        }
        map.charaMapInit();
        map.charaPositionInit();

        repaint();
    }
    public void mouseReleased(MouseEvent e){ }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e)  { }
    public void mousePressed(MouseEvent e) { }
}
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
class ImportTile extends Component {//タイルチップを読み込むクラス
    public final int TileSize = 32;//タイルのサイズ
    static String codemap = ".0123456789ABCDE";//タイルと文字の対応のための文字列
    private BufferedImage tileset = null;//タイルセットを読み根で格納する
    public ImportTile(String filename)throws IOException {
        String imageFilename = filename;
        this.tileset = ImageIO.read(new File(imageFilename));
    }
    public Dimension getPreferredSize() {
        int width = 100;//高さと幅の初期値
        int height = 100;
        if (tileset != null) {//タイルセットがちゃんと読み込んでるなら
            width = tileset.getWidth(null);//サイズ決定
            height = tileset.getHeight(null);//
        }
        return new Dimension(width, height);//高さ幅を設定
    }
    BufferedImage getTile(char code) {
        int index = codemap.indexOf(code);//map.txtから読み取った記号がcodemapの何番目かを返す
        int x = (index % 4) * TileSize;//読み取った記号とタイルを対応させる
        int y = (index / 4) * TileSize;
        return tileset.getSubimage(x, y, TileSize, TileSize);
    }
}
class Map {//マップを生成するクラス
    private char[][] stageMapData;//ステージ背景のデータを格納する配列
    private char [][] charaMapData;//キャラのグラフィックを表現するためにキャラのタイプ記号を格納する配列。ここの実装変えたい
    private BaseCharacter [][] charaPosition;//キャラの位置関係そのものを格納するもの。ぶっちゃけ上と被ってるので上をなくしたい
    private char [][] haniMapData;//移動の選択したときに移動できる範囲を塗りつぶすために半透明のパネルを表示するためのの配列
    private int horizontalLength;//マップの横の長さ
    private int verticalLength;//マップの縦の長さ
    private boolean charaFlag = false;
    private ArrayList<BaseCharacter> character1 = new ArrayList<BaseCharacter>();//プレイヤー1のキャラを格納するリスト
    private ArrayList<BaseCharacter> character2 = new ArrayList<BaseCharacter>();//プレイヤー2のキャラを格納するリスト
    public Map(String stageMapFile) {
        stageMapData = readstageMapData(stageMapFile);//ファイルを読み込んでマップを生成
        horizontalLength = stageMapData[0].length;//
        verticalLength = stageMapData.length;//縦横の長さを取得
        charaMapData = new char [verticalLength][horizontalLength];//キャラのグラを入れる配列を初期化
        haniMapData = new char [verticalLength][horizontalLength];//範囲を表す配列を初期化
        charaPosition = new BaseCharacter [verticalLength][horizontalLength];
        character1.add(new Kyoten(480, 32, 1));//拠点の初期化なのでマップによって修正
        character2.add(new Kyoten(480, 704, 2));
        charaMapInit();
        haniMapInit();
        charaPositionInit();
    }
    ////////////////////////////////////////////////////////パクったぶぶんなので治さないとまずい
    private char[][] readstageMapData(String stageMapFile) {//map.txtから文字列を読み込む
        ArrayList<String> arrayHorizontalLine = new ArrayList<>();//読み込んだ文字列を格納する
        Scanner scanner = new Scanner(getClass().getResourceAsStream(stageMapFile));//map.txtを読み込む
            while (scanner.hasNextLine()) {//空行になるまで行を読み込む
                String mapstring = scanner.nextLine();//mapstringに読み込んだ行を格納
                arrayHorizontalLine.add(mapstring.replace("\t", ""));//改行文字を消す
            }
        int lineCount = arrayHorizontalLine.size();//読み込んだ行数を出す
        char[][] stageMapArray = new char[lineCount][];//読み込んだ行数のサイズの行を持つ二次元配列を作る
        for (int i = 0; i < lineCount; i++) {//char型配列に読み込んだ文字列を変換
            stageMapArray[i] = arrayHorizontalLine.get(i).toCharArray();
        }
        return stageMapArray;//配列として返す
    }
    /////////////////////////////////////////////////////////
    public void charaMapInit() {
        for(int i = 0; i < verticalLength; i++) {
            for(int j = 0; j < horizontalLength; j++) {
                charaMapData[i][j] = '.';
            }
        }
        for(int i = 0; i < character1.size(); i++){
            BaseCharacter c = character1.get(i);
            charaMapData[c.getPosition().y/32][c.getPosition().x/32] = c.getClassType();
        }
        for(int i = 0; i < character2.size(); i++){
            BaseCharacter c = character2.get(i);
            charaMapData[c.getPosition().y/32][c.getPosition().x/32] = c.getClassType();
        }
    }
    public void charaPositionInit() {
        for(int i = 0; i < horizontalLength; i++) {
            for(int j = 0; j < verticalLength; j++) {
                charaPosition[j][i] = null;
            }
        }
        for(int i = 0; i < character1.size(); i++){
            BaseCharacter c = character1.get(i);
            charaPosition[c.getPosition().y/32][c.getPosition().x/32] = c;
        }
        for(int i = 0; i < character2.size(); i++){
            BaseCharacter c = character2.get(i);
            charaPosition[c.getPosition().y/32][c.getPosition().x/32] = c;
        }
    }
    public void addCharacter(BaseCharacter chara, int player) {//キャラクターを追加する
        if(player == 1) {
            character1.add(chara);
        } else {
            character2.add(chara);
        }
        charaMapInit();
        charaPositionInit();
    }
    public void deleteCharacter(BaseCharacter chara, int player) {//死んだときとかにキャラクターを消す
        if(player == 1) {
            character1.remove(chara);
        } else {
            character2.remove(chara);
        }
    }
    public void reChracterMoveandBattle(int player) {
        if(player == 1) {
            for(int i = 0; i < character1.size(); i++) {
                character1.get(i).setBattleSelected(false);
                character1.get(i).setMoveSelected(false);
            }
        } else {
            for(int i = 0; i < character2.size(); i++) {
                character2.get(i).setBattleSelected(false);
                character2.get(i).setMoveSelected(false);
            }
        }
    }
    public void haniMapInit() {//移動範囲を塗りつぶすパネルの初期化。ようは完全透明なパネルを敷き詰める
        for(int i = 0; i < horizontalLength; i++) {
            for(int j = 0; j < verticalLength; j++) {
                haniMapData[j][i] = '.';
            }
        }
    }

    public char getStageMapCode(int x, int y) {//ある位置のマップの記号返す
        // 引数チェックは省略
        return stageMapData[y][x];
    }
    public char getCharaMapCode(int x, int y) {//ある位置のマップの記号返す
        // 引数チェックは省略
        return charaMapData[y][x];
    }
    public char getHaniMapCode(int x, int y) {//塗りしているタイルそのものを返す
        return  haniMapData[y][x];
    }
    public void setStageMapCode(int x, int y, char tile) {//多分マップエディタで使う
        stageMapData[y][x] = tile;
    }
    public void setCharaMapCode(int x, int y, char tile) {//これ使わないような気がする
        charaMapData[y][x] = tile;
    }
    public void setHaniMapCode(int x, int y, char tile) {//これもいらないような...
        haniMapData[y][x] = tile;
    }
    public int getHorizontalLength() {//横の長さを返す
        return horizontalLength;
    }
    public int getVerticalLength() {//縦の長さを返す
        return verticalLength;
    }
    public void setCharaFlag(boolean flag) {//これなんだっけ
        charaFlag = flag;
    }
    public boolean getCharaFlag() {
        return charaFlag;
    }
    public BaseCharacter getCharaPosition(int x, int y) {//キャラそのものが入ってる配列を返す
        return charaPosition[y][x];
    }
    public void paintselectRange(int x, int y, int speed) {//移動範囲を塗りつぶす。修正必須
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if(0 <= x+i && x+i < horizontalLength && 0 <= y+j && y+j < verticalLength) {
                    haniMapData[y+j][x+i] = '1';
                }
                if(0 <= x-i && x-i < horizontalLength && 0 <= y-j && y-j < verticalLength) {
                    haniMapData[y-j][x-i] = '1';
                }
                if(0 <= x-i && x-i < horizontalLength && 0 <= y+j && y+j < verticalLength) {
                    haniMapData[y+j][x-i] = '1';
                }
                if(0 <= x+i && x+i < horizontalLength && 0 <= y-j && y-j < verticalLength) {
                    haniMapData[y-j][x+i] = '1';
                }
            }
        }
    }
}

class Frame extends JFrame implements ActionListener{
    GameScreen screen;
    JButton b1=new JButton("召喚"),b2=new JButton("移動");
    JButton b3=new JButton("攻撃"),b4=new JButton("ターンエンド");
    JButton b5=new JButton(new ImageIcon("キャラ1.png"));
    JButton b6=new JButton(new ImageIcon("キャラ1.png"));
    JButton b7=new JButton(new ImageIcon("キャラ1.png"));
    JButton b8=new JButton(new ImageIcon("キャラ1.png"));
    JButton b9=new JButton(new ImageIcon("キャラ1.png"));
    JPanel p3;
    JPanel p4;
    JLabel cost;
    public Frame() throws IOException {
        JPanel panel = new JPanel();
        screen = new GameScreen();
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
        p2.setLayout(new GridLayout(2, 1));
        p2.add(p3);
        p3.setVisible(false);
        cost = new JLabel("残りマナ:Player1"+screen.state.getPlayer1Mana()+"Player2"+screen.state.getPlayer2Mana());
        p4.add(cost);
        this.add(p4, BorderLayout.SOUTH);
        this.add(p1,BorderLayout.WEST);
        this.add(p2,BorderLayout.EAST);
        this.add(panel, BorderLayout.CENTER);
        this.pack(); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        screen.map.haniMapInit();
        if (e.getSource()==b1) {
            screen.rectColor = Color.blue;
            screen.state.setSummon();
            p3.setVisible(true);
        } else if(e.getSource()==b2) {
            screen.rectColor = Color.red;
            screen.state.setMove();
            p3.setVisible(false);
        } else if(e.getSource() == b3) {
            screen.rectColor = Color.yellow;
            screen.state.setBattle();
            p3.setVisible(false);
        } else if(e.getSource() == b4) {
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
    public static void main(String[] args) throws IOException {
        new Frame();
    }
}
