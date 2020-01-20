package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import base.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.Color;
import java.util.Random;
import java.io.FileWriter;

class GameState {//ゲームの全体の状態を統括。大体目に見えない部分を処理
    private boolean moveFlag;//カーソルの判定が移動になってるか
    private boolean summonFlag;//カーソルの判定が召喚になってるか
    private boolean battleFlag;//カーソルの判定がこうげきになってるか。//つまるとこ画面左のどのボタンが押されたか
    private int player1Mana;//プレイヤー1の残りマナ
    private int player2Mana;//プレイヤー2の残りマナ
    private int nowPlayer;//今操作を行えるプレイヤーを表す
    private BaseCharacter nowSummon;//今召喚カーソルをクリックして召喚できるキャラクターを表す
    private char nowTile = '.';
    private boolean makegraphic = true;
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
    public void setNowTile(char c) {
        nowTile = c;
    }
    public char getNowTile() {
        return nowTile;
    }
    public BaseCharacter getNowSummon() {//今召喚するキャラを取得
        return nowSummon;
    }
    public void setMakeGraphic(boolean flag) {
        makegraphic = flag;
    }
    public boolean getMakeGraphic() {
        return makegraphic;
    }
}

class GameScreen extends JPanel implements MouseListener,ActionListener{
    static final int startX = 0;//画面の位置を調整するもの
    static final int startY = 0;//画面の一を調整するもの
    SoundPlayer sp;
    int x;
    int y;
    int tmp_x;//移動の際に移動元のマスを記憶するもの
    int tmp_y;//同上
    int width = 0;//画面のサイズ
    int height = 0;//同上
    int TileSize = 32;//タイルの大きさ
    int rect_x = 0;//カーゾルの位置を記憶するもの
    int rect_y = 0;
    char tmp;
    private Timer timer;
    boolean ButtleSelectflag = false;
    BaseCharacter tmpl;
    Color rectColor = Color.red;
    GameState state = new GameState();
    boolean rect_flag = false;//カーソルを画面に表示するかしないか
    BufferedImage mapImage;//マップの画像を記憶する物
    BufferedImage charaImage;//キャラの画像を記憶する物
    BufferedImage panelImage;//範囲選択の際に染めたものを記録するもの
    Map map;
    BaseCharacter characterTmp = new Kyoten(32, 32, 1);
    int step = 1;
    public GameScreen(String file) throws IOException {
        sp = SoundPlayer.getInstance();
        map = new Map(file);
        mapImage = createImage("game/MapTile.png", 1);
        charaImage = createImage("game/Character.png", 2);
        panelImage = createImage("game/Hani.png", 3);
        width = mapImage.getWidth();//サイズ設定
        height = mapImage.getHeight();
        addMouseListener(this);
        initialize();
    }
    void initialize() {//ゲーム画面の初期位置の設定
        this.x = TileSize * (startX);
        this.y = TileSize * (startY);
    }
    public void paintComponent(Graphics g){
        g.drawImage(mapImage, 0, 0, this);
        for(int i = 0; i < map.getListSize(1); i++) {
            try {
                g.drawImage(map.getList(1).get(i).getGraphic(),map.getList(1).get(i).getPosition().x, map.getList(1).get(i).getPosition().y, this);
            } catch (Exception e) {
                System.err.println("ErrorInDrawImage");
            }
        }
        for(int i = 0; i < map.getListSize(2); i++) {
            try {
                g.drawImage(map.getList(2).get(i).getGraphic(), map.getList(2).get(i).getPosition().x, map.getList(2).get(i).getPosition().y, this);
            } catch (Exception e) {
                System.err.println("ErrorInDrawImage");
            }
        }
        if(state.getMakeGraphic() == true) {
            try {
                panelImage = createImage("game/Hani.png", 3);
            } catch (Exception e) {
                System.err.println("ErrorInCreateImage");
            }
        }
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
    public void actionPerformed(ActionEvent e){
        boolean upOrdown = true;
        boolean leftOrright = true;
        if(rect_y - characterTmp.getPosition().y < 0) {
            upOrdown = false;
        }
        if(rect_x - characterTmp.getPosition().x < 0) {
            leftOrright = false;
        }
        if(upOrdown == true && step == 1) {
            characterTmp.setDirection(0);
            characterTmp.move(characterTmp.getPosition().x, characterTmp.getPosition().y+4);
        } else if(upOrdown == false && step == 1) {
            characterTmp.setDirection(3);
            characterTmp.move(characterTmp.getPosition().x, characterTmp.getPosition().y-4);
        } else if(leftOrright == true && step == 2){
            characterTmp.move(characterTmp.getPosition().x+4, characterTmp.getPosition().y);
        } else if(leftOrright == false && step == 2) {
            characterTmp.move(characterTmp.getPosition().x-4, characterTmp.getPosition().y);
        }
        if(characterTmp.getPosition().y == rect_y) {
            if(rect_x != characterTmp.getPosition().x) {
                if(leftOrright == true) {
                    characterTmp.setDirection(2);
                } else {
                    characterTmp.setDirection(1);
                }
            }
            step = 2;
        }
        if(characterTmp.getPosition().x == rect_x && characterTmp.getPosition().y == rect_y) {
            step = 1;
            state.setMakeGraphic(true);
            timer.stop();
        }
        repaint();
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();
        Point point = e.getPoint();
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);
            rect_y = point.y- (point.y%TileSize);
            int array_x = rect_x/32;
            int array_y = rect_y/32;
            if(state.getMoveFlag() == true && state.getBattleFlag() == false && state.getSummonFlag() == false) {//移動
                if(map.getCharaPosition(array_x, array_y) != null && map.getCharaPosition(array_x, array_y).getMoveSelected() == false && state.getNowPlayer() == map.getCharaPosition(array_x, array_y).getPlayer()) {
                    tmp_x = rect_x; tmp_y = rect_y;
                    map.paintMoveRange(array_x, array_y, map.getCharaPosition(array_x, array_y).getSpeed());
                }else if(map.getCharaMapCode(array_x, array_y) == '.' &&  map.getHaniMapCode(array_x, array_y) == '1') {
                    characterTmp = map.getCharaPosition(tmp_x/32, tmp_y/32);
                    state.setMakeGraphic(false);
                    timer = new Timer(10, this);
                    timer.start();
                    map.haniMapInit();
                    characterTmp.setMoveSelected(true);
                }
            } else if(state.getMoveFlag() == false && state.getBattleFlag() == false && state.getSummonFlag() == true && map.getHaniMapCode(array_x, array_y) == '1') {
                BaseCharacter c =  state.getNowSummon();//召喚
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
                sp.playBGM("game/sample.wav");
            } else if (state.getMoveFlag() == false && state.getBattleFlag() == true && state.getSummonFlag() == false){
                if(map.getCharaPosition(array_x, array_y) != null && map.getCharaPosition(array_x, array_y).getBattleSelected() == false && ButtleSelectflag == false) {
                    tmpl = map.getCharaPosition(array_x, array_y);
                    if(tmpl.getPlayer() != state.getNowPlayer()) {
                        return;
                    }
                    map.paintButtleRange(array_x, array_y);
                    ButtleSelectflag = true;
                } else if(ButtleSelectflag == true && map.getHaniMapCode(array_x, array_y) == '1' && map.getCharaPosition(array_x, array_y) != null) {
                    BaseCharacter chara = map.getCharaPosition(array_x, array_y);
                    if(chara.getClassType() != 'E' || chara.getClassType() != 'D') {
                        chara.giveDamage(tmpl.getAttackPoint());
                    } else {
                        chara.giveDamage(tmpl.getAttackToBuilding());
                    }
                    if(chara.isDead() == true) {
                        if(chara.getClassType() != 'E' || chara.getClassType() != 'D') {
                        map.deleteCharacter(chara, chara.getPlayer());
                        } else {
                        map.deleteCharacter(chara, chara.getPlayer());
                        //ゲームセット
                        System.exit(0);
                        }
                    }
                    ButtleSelectflag = false;
                    tmpl.setBattleSelected(true);
                    map.haniMapInit();
                }
            }
        }else if (btn == MouseEvent.BUTTON3){
            rect_flag = false;
        }else if (btn == MouseEvent.BUTTON2){
        }
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
            setGraphic(classType);
        } catch (Exception e) {
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
    public void setGraphic(char character) throws IOException{//
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
        }
        else if(character == 'E') {
            tile = new ImportTile("game/Character.png");
            if(player == 1) {
                icon[0] = tile.getTile('D');
                icon[1] = tile.getTile('D');
                icon[2] = tile.getTile('D');
                icon[3] = tile.getTile('D');
            } else {
                icon[0] = tile.getTile('E');
                icon[1] = tile.getTile('E');
                icon[2] = tile.getTile('E');
                icon[3] = tile.getTile('E');
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
    public BufferedImage getGraphic() throws IOException{
        return graphic;
    }
    public void setDirection(int n) {
        graphic = icon[n];
    }
}
class Ippan extends BaseCharacter {//一般大学生
    private char c;
    public Ippan(int x, int y, int player) {
        super(5, "一般大学生", x, y, player, '0', 3, 2, 2, 3);//(int hp, String name, int x, int y,int player, char classType, int attackPoint, int attackToBuilding, int speed, int cost) {
    }
}
class Otaku extends BaseCharacter {//オタク。移動が強い
    public Otaku(int x, int y, int player) {
        super(3, "オタク", x, y, player, '1', 2, 1, 3, 5);//オタクくんさあ
    }
}
class Yokya extends BaseCharacter {//陽キャ。拠点に対する攻撃力が高い
    public Yokya(int x, int y, int player) {
        super(7, "陽キャ", x, y, player, '2', 4, 5, 2, 7);//WANIMA的な
    }
}
class Idol extends BaseCharacter {//アイドル。敵味方問わずHPを回復させる。拠点には1ダメージ与えれる
    public Idol(int x, int y, int player) {
        super(3, "アイドル", x, y, player, '3', -2, 1, 2, 5);
    }
}
class Kyoju extends BaseCharacter {//教授。人に対して高い攻撃力だけど拠点には何もできない
    public Kyoju(int x, int y, int player) {
        super(10, "教授", x, y, player, '4', 8, 0, 2, 10);
    }
}
class Kyoten extends BaseCharacter {//拠点
    public Kyoten(int x, int y, int player) {
        super(25, "拠点", x, y, player, 'E', 0, 0, 4, 0);//ひとまず
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
    protected char[][] stageMapData;//ステージ背景のデータを格納する配列
    private char [][] charaMapData;//キャラのグラフィックを表現するためにキャラのタイプ記号を格納する配列。ここの実装変えたい
    private BaseCharacter [][] charaPosition;//キャラの位置関係そのものを格納するもの。ぶっちゃけ上と被ってるので上をなくしたい
    private char [][] haniMapData;//移動の選択したときに移動できる範囲を塗りつぶすために半透明のパネルを表示するためのの配列
    private int horizontalLength;//マップの横の長さ
    private int verticalLength;//マップの縦の長さ
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
    public void setBuilding() {
        for(int i = 0; i < verticalLength; i++) {
            for(int j = 0; j < horizontalLength; j++) {
                if(stageMapData[j][i] == 'D') {
                    addCharacter(new Kyoten(32*i, 32*j, 1), 1);
                }
                if(stageMapData[j][i] == 'E') {
                    addCharacter(new Kyoten(32*i, 32*j, 1), 2);
                }
            }
        }
    }
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
    public int getListSize(int player) {
        if(player == 1) {
            return character1.size();
        } else {
            return character2.size();
        }
    }
    public ArrayList<BaseCharacter> getList(int player) {
        if(player == 1) {
            return character1;
        } else {
            return character2;
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
    public BaseCharacter getCharaPosition(int x, int y) {//キャラそのものが入ってる配列を返す
        return charaPosition[y][x];
    }
    public void paintSummonRange(int x, int y, int speed) {
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if(0 <= x+j && x+j < horizontalLength && 0 <= y+i && y+i < verticalLength) {
                    if((stageMapData[y+i][x+j] == '.' || stageMapData[y+i][x+j] == '5' || stageMapData[y+i][x+j] == '6' || stageMapData[y+i][x+j] == '7') && charaPosition[y+i][x+j] == null) {
                        haniMapData[y+i][x+j] = '1';
                    }
                }
                if(0 <= x-j && x-j < horizontalLength && 0 <= y+i && y+i < verticalLength) {
                    if((stageMapData[y+i][x-j] == '.' || stageMapData[y+i][x-j] == '5' || stageMapData[y+i][x-j] == '6' || stageMapData[y+i][x-j] == '7') && charaPosition[y+i][x-j] == null) {
                        haniMapData[y+i][x-j] = '1';
                    }
                }
                if(0 <= x+j && x+j < horizontalLength && 0 <= y-i && y-i < verticalLength) {
                    if((stageMapData[y-i][x+j] == '.' || stageMapData[y-i][x+j] == '5' || stageMapData[y-i][x+j] == '6' || stageMapData[y-i][x+j] == '7') && charaPosition[y-i][x+j] == null) {
                        haniMapData[y-i][x+j] = '1';
                    }
                }
                if(0 <= x-j && x-j < horizontalLength && 0 <= y-i && y-i < verticalLength) {
                    if((stageMapData[y-i][x-j] == '.' || stageMapData[y-i][x-j] == '5' || stageMapData[y-i][x-j] == '6' || stageMapData[y-i][x-j] == '7') && charaPosition[y-i][x-j] == null) {
                        haniMapData[y-i][x-j] = '1';
                    }
                }
            }
        }
    }
    public void paintMoveRange(int x, int y, int speed) {//移動範囲を塗りつぶす。修正必須
        boolean stopFlag = false;//
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if((i == 0 && j == 0) || !(0 <= x+j && x+j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    continue;
                }
                boolean banTile = ((stageMapData[y+i][x+j] == '.' || stageMapData[y+i][x+j] == '5' || stageMapData[y+i][x+j] == '6' || stageMapData[y+i][x+j] == '7') && charaPosition[y+i][x+j] == null);
                if(banTile == true){
                    haniMapData[y+i][x+j] = '1';
                } else if(banTile == false && j == 0){
                    stopFlag = true;
                    break;
                } else {
                    break;
                }
            }
            if(stopFlag == true) {
                stopFlag = false;
                break;
            }
        }
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if((i == 0 && j == 0) || !(0 <= x-j && x-j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    continue;
                }
                boolean banTile = ((stageMapData[y+i][x-j] == '.' || stageMapData[y+i][x-j] == '5' || stageMapData[y+i][x-j] == '6' || stageMapData[y+i][x-j] == '7') && charaPosition[y+i][x-j] == null);
                if(banTile == true){
                    haniMapData[y+i][x-j] = '1';
                } else if(banTile == false && j == 0){
                    stopFlag = true;
                    break;
                } else {
                    break;
                }
            }
            if(stopFlag == true) {
                stopFlag = false;
                break;
            }
        }
        for(int i = 1; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if(!(0 <= x-j && x-j < horizontalLength && 0 <= y-i && y-i < verticalLength)) {
                    continue;
                }
                boolean banTile = ((stageMapData[y-i][x-j] == '.' || stageMapData[y-i][x-j] == '5' || stageMapData[y-i][x-j] == '6' || stageMapData[y-i][x-j] == '7') && charaPosition[y-i][x-j] == null);
                if(banTile == true){
                    haniMapData[y-i][x-j] = '1';
                } else if(banTile == false && j == 0){
                    stopFlag = true;
                    break;
                } else {
                    break;
                }
            }
            if(stopFlag == true) {
                stopFlag = false;
                break;
            }
        }
        for(int i = 1; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if(!(0 <= x+j && x+j < horizontalLength && 0 <= y-i && y-i < verticalLength)) {
                    continue;
                }
                boolean banTile = ((stageMapData[y-i][x+j] == '.' || stageMapData[y-i][x+j] == '5' || stageMapData[y-i][x+j] == '6' || stageMapData[y-i][x+j] == '7') && charaPosition[y-i][x+j] == null);
                if(banTile == true){
                    haniMapData[y-i][x+j] = '1';
                } else if(banTile == false && j == 0){
                    stopFlag = true;
                    break;
                } else {
                    break;
                }
            }
            if(stopFlag == true) {
                stopFlag = false;
                break;
            }
        }

    }
    public void paintButtleRange(int x, int y) {
        for(int i = 0; i <= 1; i++) {
            for(int j = 0; j <= 1; j++) {
                if(i == 0 && j == 0) {
                    continue;
                }
                if((0 <= x+j && x+j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    haniMapData[y+i][x+j] = '1';
                }
                if((0 <= x-j && x-j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    haniMapData[y+i][x-j] = '1';
                }
                if((0 <= x+j && x+j < horizontalLength && 0 <= y-i && y-i < verticalLength)) {
                    haniMapData[y-i][x+j] = '1';
                }
                if((0 <= x-j && x-j < horizontalLength && 0 <= y-i && y-i < verticalLength)) {
                    haniMapData[y-i][x-j] = '1';
                }
            }
        }
    }
    public void saveMap() throws IOException{
        File file = new File("map5.txt");
        FileWriter filewriter = new FileWriter(file);
        for(int i = 0; i < verticalLength; ++i) {
            String s = new String(stageMapData[i]);
                filewriter.write(s);
                filewriter.write("\n");
        }
        filewriter.close();
    }
    public BaseCharacter getKyoten(int player) {
        BaseCharacter c;
        if(player == 1) {
            c = character1.get(0);
        } else {
            c = character2.get(0);
        }
        return c;
    }
    public void autoCreateMap() {
        for(int i = 0; i < horizontalLength; i++) {
            for(int j = 0; j < verticalLength; j++) {
                stageMapData[j][i] = '.';
            }
        }
        Random rand = new Random();
        for(int i = 0; i < 15; i++) {
            int roomHeight = rand.nextInt(3) + 3;
            int roomWidth = rand.nextInt(3) + 3;
            int roomX = rand.nextInt((horizontalLength - 6 - 2)) + 2;
            int roomY = rand.nextInt((verticalLength - 6 - 2)) + 2;
            int n = rand.nextInt(3);
            char c = '.';
            if(n == 0) {
                c = '3';
            } else if(n == 1) {
                c = '4';
            } else if(n == 2) {
                c = 'A';
            }

            for(int j = 0; j < roomHeight; j++) {
                roomWidth = rand.nextInt(3) + 3;
                for(int k = 0; k < roomWidth; k++) {
                    if(roomX + k <= horizontalLength && roomY + j <= verticalLength) {
                        stageMapData[roomY+j][roomX+k] = c;
                    }
                }
            }
            if(c == 'A') {
                int m = rand.nextInt(4);
                if(m == 0) {
                    for(int j = 0 ; j < roomHeight; j++) {
                        stageMapData[roomY+j][roomX+roomWidth/2] = '5';
                    }
                }
            }
        }
    }
}

class StageEditScreen extends GameScreen implements MouseListener{//エディタのViewにあたる部分
    public StageEditScreen(String file) throws IOException {
        super(file);
    }
    @Override
    public void paintComponent(Graphics g){
        try {
            mapImage = createImage("MapTile.png", 1);
        } catch (Exception e) {
            System.err.println("ErrorInCreateImage(Map)");
        }
        g.drawImage(mapImage, 0, 0, this);
        g.setColor(rectColor);
        g.drawRect(rect_x, rect_y, TileSize, TileSize);
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();
        Point point = e.getPoint();
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);
            rect_y = point.y- (point.y%TileSize);
            int array_x = rect_x/32;
            int array_y = rect_y/32;
            this.map.setStageMapCode(array_x, array_y, state.getNowTile());
        }else if (btn == MouseEvent.BUTTON3){
            rect_flag = false;
        }else if (btn == MouseEvent.BUTTON2){
        }
        repaint();
    }
    public void mouseReleased(MouseEvent e){ }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e)  { }
    public void mousePressed(MouseEvent e) { }
}
class StageEdit extends Map{//エディタのMにあたる部分
    public StageEdit(String s) {
        super(s);
    }
}
class StageEditFrame extends JFrame implements ActionListener {//いつものUIにあたるVの部分.起動するのにはnew StageEditFrame()をどっかでやればいいはず.
    StageEditScreen screen;
    JButton save = new JButton("save");
    JButton b [] = new JButton[16];
    JPanel p3;
    JPanel p4;
    JButton autoMapCreate;
    ImportTile tile = new ImportTile("game/MapTile.png");
    public StageEditFrame() throws IOException {
        JPanel panel = new JPanel();
        screen = new StageEditScreen("game/map5.txt");
        panel.setLayout(new GridLayout(1, 1));
        panel.add(screen);
        JPanel  p1=new JPanel(),p2=new JPanel(), p3 = new JPanel();
        b[0] = new JButton(new ImageIcon(tile.getTile('.')));
        b[1] = new JButton(new ImageIcon(tile.getTile('0')));
        b[2] = new JButton(new ImageIcon(tile.getTile('1')));
        b[3] = new JButton(new ImageIcon(tile.getTile('2')));
        b[4] = new JButton(new ImageIcon(tile.getTile('3')));
        b[5] = new JButton(new ImageIcon(tile.getTile('4')));
        b[6] = new JButton(new ImageIcon(tile.getTile('5')));
        b[7] = new JButton(new ImageIcon(tile.getTile('6')));
        b[8] = new JButton(new ImageIcon(tile.getTile('7')));
        b[9] = new JButton(new ImageIcon(tile.getTile('8')));
        b[10] = new JButton(new ImageIcon(tile.getTile('9')));
        b[11] = new JButton(new ImageIcon(tile.getTile('A')));
        b[12] = new JButton(new ImageIcon(tile.getTile('B')));
        b[13] = new JButton(new ImageIcon(tile.getTile('C')));
        b[14] = new JButton(new ImageIcon(tile.getTile('D')));
        b[15] = new JButton(new ImageIcon(tile.getTile('E')));
        autoMapCreate = new JButton("自動生成");
        p1.setLayout(new GridLayout(4,1));
        p2.setLayout(new GridLayout(4,4));
        p3.setLayout(new GridLayout(1, 1));
        p3.add(autoMapCreate);
        autoMapCreate.addActionListener(this);
        p3.add(save);
        for(int i = 0; i < 16; i++) {
            b[i].addActionListener(this);;
        }
        for(int i = 0; i < 16; i++) {
            p2.add(b[i]);
        }
        save.addActionListener(this);
        this.add(p2,BorderLayout.EAST);
        this.add(panel, BorderLayout.CENTER);
        this.add(p3, BorderLayout.SOUTH);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == b[0]) {
            screen.state.setNowTile('.');
        } else if(e.getSource() == b[1]) {
            screen.state.setNowTile('0');
        } else if(e.getSource() == b[2]) {
            screen.state.setNowTile('1');
        } else if(e.getSource() == b[3]) {
            screen.state.setNowTile('2');
        } else if(e.getSource() == b[4]) {
            screen.state.setNowTile('3');
        } else if(e.getSource() == b[5]) {
            screen.state.setNowTile('4');
        } else if(e.getSource() == b[6]) {
            screen.state.setNowTile('5');
        } else if(e.getSource() == b[7]) {
            screen.state.setNowTile('6');
        } else if(e.getSource() == b[8]) {
            screen.state.setNowTile('7');
        } else if(e.getSource() == b[9]) {
            screen.state.setNowTile('8');
        } else if(e.getSource() == b[10]) {
            screen.state.setNowTile('9');
        } else if(e.getSource() == b[11]) {
            screen.state.setNowTile('A');
        } else if(e.getSource() == b[12]) {
            screen.state.setNowTile('B');
        }  else if(e.getSource() == b[13]) {
            screen.state.setNowTile('C');
        } else if(e.getSource() == b[14]) {
            screen.state.setNowTile('D');
        } else if(e.getSource() == b[15]) {
            screen.state.setNowTile('E');
        } else if(e.getSource() == save) {
            try {
                screen.map.saveMap();
            } catch (Exception IE) {
                System.err.println("ErrorInSaveMap");
            }
        } else if(e.getSource() == autoMapCreate) {
            screen.map.autoCreateMap();
            screen.repaint();
        }
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
    JLabel cost;
    public GamePanel(String file) throws IOException {
        JPanel panel = new JPanel();
        screen = new GameScreen(file);
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
        this.setLayout(new BorderLayout());
        this.add(p4, BorderLayout.SOUTH);
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
