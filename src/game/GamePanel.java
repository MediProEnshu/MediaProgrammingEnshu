package game;

import base.*;
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
import java.util.Random;
import java.io.FileWriter;
import java.util.Observable;
import java.util.Observer;

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

class GameScreen extends JPanel implements MouseListener,ActionListener{
    /*この辺の変数Gの一部ameStateに置いたほうがいいんじゃね？*/
    int tmp_x;//移動の際に移動元のマスを記憶するもの
    int tmp_y;//同上
    int width = 0;//画面のサイズ
    int height = 0;//同上
    int TileSize = 32;//タイルの大きさ
    int rect_x = 0;//カーゾルの位置を記憶するもの
    int rect_y = 0;
    private Timer timer;//アニメーション陽
    boolean battleSelectflag = false;
    BaseCharacter battleCharacter;
    Color rectColor = Color.red;
    GameState state = new GameState();
    boolean rect_flag = false;//カーソルを画面に表示するかしないか
    BufferedImage mapImage;//マップの画像を記憶する物
    BufferedImage charaImage;//キャラの画像を記憶する物
    BufferedImage haniImage;//範囲選択の際に染めたものを記録するもの
    Map map;//マップの初期化
    BaseCharacter characterTmp = new Kyoten(32, 32, 1);//これも
    DynamicTextModel modelTextLog = new DynamicTextModel("テキストログ");//テキストログのモデル
    DynamicTextModel modelMapInfo = new DynamicTextModel(" ");//マップ情報を出すラベルのモデル
    SoundPlayer sp;
    int step = 1;//移動の時に使う。ステップ一が縦移動でステップ2が横移動
    public GameScreen(String file) throws IOException {
        map = new Map(file);
        sp = SoundPlayer.getInstance();
        sp.playBGM("game/game_bgm.wav");
        mapImage = createImage("game/MapTile.png", 1);
        haniImage = createImage("game/Hani.png", 3);
        width = mapImage.getWidth();//サイズ設定
        height = mapImage.getHeight();
        addMouseListener(this);
    }
    public void paintComponent(Graphics g){
        g.drawImage(mapImage, 0, 0, this);//マップを描画
        for(int i = 0; i < map.getListSize(1); i++) {//プレイヤー1のキャラクターを描画
            try {
                g.drawImage(map.getList(1).get(i).getGraphic(),map.getList(1).get(i).getPosition().x, map.getList(1).get(i).getPosition().y, this);
            } catch (Exception e) {
                //TODO: handle exception
                System.err.println("ErrorInSetGraphic");
            }
        }
        for(int i = 0; i < map.getListSize(2); i++) {//プレイヤー2のキャラを描画
            try {
                g.drawImage(map.getList(2).get(i).getGraphic(), map.getList(2).get(i).getPosition().x, map.getList(2).get(i).getPosition().y, this);
            } catch (Exception e) {
                //TODO: handle exception
                System.err.println("ErrorInSetGraphic");
            }
        }
        if(state.getMakeGraphic() == true) {//移動範囲・召喚範囲を描画しないといけないときには新しい画像で描画する。移動のアニメーション中は処理が重いので新しい画像にしない
            try {
                haniImage = createImage("game/Hani.png", 3);
            } catch (Exception e) {
                //TODO: handle exception
                System.err.println("ErrorInSetGraphic");
            }
        }
        g.drawImage(haniImage, 0, 0, this);//範囲を描画する
        if(state.getMoveFlag() == true || state.getBattleFlag() == true || state.getSummonFlag() == true) {//カーソルの描画
            g.setColor(rectColor);
            g.drawRect(rect_x, rect_y, TileSize, TileSize);
        }
    }
    public BufferedImage createImage(String fileName, int type) throws IOException{//配列から画像を生成する
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
                    g.drawImage(tileset.getTile(map.getStageMapCode(x, y)), gX, gY, null);//マップの描画
                } else {
                    g.drawImage(tileset.getTile(map.getHaniMapCode(x, y)), gX, gY, null);//選択範囲の描画
                }
                gY += TileSize;//タイルの大きさ分横にずらす
            }
            gX += TileSize;//タイルの大きさ分縦にずらす
        }
        return bi;
    }
    public void actionPerformed(ActionEvent e){//移動の時のアニメーション
        boolean upOrDown = true;//上に行くか下に行くか.trueは下
        boolean leftOrRight = true;//右に行くか左に行くか.trueは右
        if(rect_y - characterTmp.getPosition().y < 0) {//カーソルの位置と現在のキャラの位置を比較して方向を決める
            upOrDown = false;
        }
        if(rect_x - characterTmp.getPosition().x < 0) {//カーソルの位置と現在のキャラの位置を比較して方向を決める
            leftOrRight = false;
        }
        if(upOrDown == true && step == 1) {//下に行くとき
            characterTmp.setDirection(0);//方向転換
            characterTmp.move(characterTmp.getPosition().x, characterTmp.getPosition().y+4);//移動
        } else if(upOrDown == false && step == 1) {//上に行くとき
            characterTmp.setDirection(3);//方向転換
            characterTmp.move(characterTmp.getPosition().x, characterTmp.getPosition().y-4);//移動
        } else if(leftOrRight == true && step == 2){//右に行くとき.方向転換はべつのとこで行う
            characterTmp.move(characterTmp.getPosition().x+4, characterTmp.getPosition().y);//移動
        } else if(leftOrRight == false && step == 2) {//左に行くとき
            characterTmp.move(characterTmp.getPosition().x-4, characterTmp.getPosition().y);
        }
        if(characterTmp.getPosition().y == rect_y) {//上下に移動し終わった後方向転換をする
            if(rect_x != characterTmp.getPosition().x) {//左右に移動する必要があるとき。つまり上下に行ったあと左右に移動するとき
                if(leftOrRight == true) {//右
                    characterTmp.setDirection(2);//右に方向転換
                } else {
                    characterTmp.setDirection(1);//左に方向転換
                }
            }
            step = 2;//移動ステップを2にする
        }
        if(characterTmp.getPosition().x == rect_x && characterTmp.getPosition().y == rect_y) {//目的地に行ったらアニメーション終了
            step = 1;
            state.setMakeGraphic(true);//範囲を描画するようにする.
            characterTmp.move(rect_x, rect_y);//上手く移動できてない場合があるのでもう一度目的地に合わせる
            timer.stop();//アニメーション終了
        }
        repaint();
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();//クリックがどのボタンか取得
        Point point = e.getPoint();//クリックされた地点を取得
        if (btn == MouseEvent.BUTTON1){//左クリックだったとき
            rect_x = point.x - (point.x%TileSize);//カーソルのいちを補正
            rect_y = point.y- (point.y%TileSize);//カーソルの位置を補正
            int array_x = rect_x/32;//配列に入れるときには配列のサイズに合わせるarray_は配列の添え字になるときの変数
            int array_y = rect_y/32;//
            if(array_x < 0 || map.getHorizontalLength() < array_x || array_y < 0 || map.getVerticalLength() < array_y) {//配列の領域外は弾く
                return;
            }
            map.charaPositionInit();//一度ここでキャラの位置情報を更新
            if(state.getMoveFlag() == true && state.getBattleFlag() == false && state.getSummonFlag() == false) {//移動コマンドの時
                if(map.getCharaPosition(array_x, array_y) != null && map.getCharaPosition(array_x, array_y).getMoveSelected() == false &&
                state.getNowPlayer() == map.getCharaPosition(array_x, array_y).getPlayer() &&
                !(map.getCharaPosition(array_x, array_y).getClassType() == 'D' || map.getCharaPosition(array_x, array_y).getClassType() == 'E')) {//ここの条件長いけど
                    //キャラがいないマス、既に移動したキャラ、操作プレイヤーのものではないキャラを弾いてる,拠点も弾いてたわ
                    tmp_x = rect_x; tmp_y = rect_y;//移動前のクリックした段階での座標を持つ
                    map.paintMoveRange(array_x, array_y, map.getCharaPosition(array_x, array_y).getSpeed());//移動範囲にキャラの座標と移動できる範囲を入れて描画.
                } else if(map.getHaniMapCode(array_x, array_y) == '1') {//移動できるマスの時
                    characterTmp = map.getCharaPosition(tmp_x/32, tmp_y/32);//一時変数に元の位置のキャラを保存する
                    state.setMakeGraphic(false);//範囲は処理の軽減のため新たに画像を生成しなくする
                    timer = new Timer(10, this);//アニメーション
                    // TODO 音楽流す, 分岐条件考える
                    if(characterTmp.getClassType() == '2') {
                        sp.playSE("game/otaku_walk.wav");
                    }else {
                        sp.playSE("game/normal_walk.wav");
                    }
                    timer.start();//開始
                    map.haniMapInit();//範囲を適切に設定しなおし後で描画
                    characterTmp.setMoveSelected(true);//移動し終わったという設定をキャラにつける
                    modelTextLog.changeText("<html>player<body>"+characterTmp.getPlayer()+"<br/>の"+characterTmp.getName()+"<br/>が移動");//テキストログの設定
                }
            } else if(state.getMoveFlag() == false && state.getBattleFlag() == false && state.getSummonFlag() == true && map.getHaniMapCode(array_x, array_y) == '1') {//召喚ボタンが押されて召喚できるマスだったとき
                if(state.getNowSummon() == null) {//今召喚するものが選択されてないときは弾く
                    return;
                }
                BaseCharacter c =  state.getNowSummon();//召喚するものを取得
                if(state.getNowPlayer() == 1) {
                    if(state.getPlayer1Mana() - c.getCost() >= 0 && c != null) {//プレイヤー1の時のマナが足りてるか判定
                        state.setPlayer1Mana(c.getCost());//マナを消費し
                        c.move(rect_x, rect_y);//目的の位置に移動
                        map.addCharacter(c, c.getPlayer());//リストに追加
                        map.setHaniMapCode(array_x, array_y, '.');
                        modelTextLog.changeText("<html>player<body>"+state.getNowPlayer()+" が<br/>"+c.getName()+"を召喚");//テキストログ
                    }
                } else {
                    if(state.getPlayer2Mana() - c.getCost() >= 0 && c != null) {//プレイヤー2も同様
                        state.setPlayer2Mana(c.getCost());
                        c.move(rect_x, rect_y);
                        map.addCharacter(c, c.getPlayer());
                        map.setHaniMapCode(array_x, array_y, '.');
                        modelTextLog.changeText("<html>player<body>"+state.getNowPlayer()+" が<br/>"+c.getName()+"を召喚");//テキストログ
                    }
                }
                // TODO 音が愚を流す, 分岐条件考える
                if(c.getClassType() == '2') {
                    sp.playSE("game/yokya_way.wav");
                }else {
                    sp.playSE("game/summon.wav");
                }
                state.setNowSummon(null);//またボタンを押して召喚するものを選んだほうが安全

            } else if (state.getMoveFlag() == false && state.getBattleFlag() == true && state.getSummonFlag() == false){//戦闘コマンド

                if(map.getCharaPosition(array_x, array_y) != null && map.getCharaPosition(array_x, array_y).getBattleSelected() == false &&
                    battleSelectflag == false && map.getCharaPosition(array_x, array_y).getPlayer() == state.getNowPlayer()) {
                    //長いけどキャラ選択した時点でそこがキャラがいないマスじゃなくて、選択したキャラが戦闘済みじゃなくて、
                    //攻撃させるキャラを選択する前で、選択したキャラが自分のキャラクターかを判定する
                    battleCharacter = map.getCharaPosition(array_x, array_y);//戦闘する自分のキャラは一時変数に保存
                    map.paintbattleRange(array_x, array_y);//戦闘範囲を描画
                    battleSelectflag = true;//次は誰に攻撃するかを選ぶ
                } else if(battleSelectflag == true && map.getHaniMapCode(array_x, array_y) == '1' && map.getCharaPosition(array_x, array_y) != null) {
                    //誰に攻撃するか選ぶ前で攻撃できるはんいに入っててそこにキャラがいるとき
                    int damage = 0;
                    BaseCharacter battledCharacter = map.getCharaPosition(array_x, array_y);//攻撃「される」キャラを保存.
                    if(battledCharacter.getClassType() != 'E' || battledCharacter.getClassType() != 'D') {//拠点以外のとき
                        battledCharacter.giveDamage(battleCharacter.getAttackPoint());//人間に対する攻撃力でダメージ演算
                        damage = battleCharacter.getAttackPoint();
                    } else {
                        battledCharacter.giveDamage(battleCharacter.getAttackToBuilding());//拠点のときは拠点に対する攻撃力で計算
                        damage = battleCharacter.getAttackToBuilding();
                    }
                    //TODO:ダメージ音流したい
                    if(battleCharacter.getClassType() == '3') {
                        modelTextLog.changeText("<html>player<body>"+battleCharacter.getPlayer()+"<br/>の"+battleCharacter.getName()+"<br/>がplayer"
                        + battledCharacter.getPlayer() + "の"+ battledCharacter.getName() +"<br/>を"+ -damage + "回復させた</html>");//テキストログ
                    } else {
                        modelTextLog.changeText("<html>player<body>"+battleCharacter.getPlayer()+"<br/>の"+battleCharacter.getName()+"<br/>がplayer"
                        + battledCharacter.getPlayer() + "の"+ battledCharacter.getName() +"<br/>に"+ damage + "ダメージを与えた</html>");//テキストログ
                    }
                    if(battledCharacter.isDead() == true) {//戦闘でHPが0になったとき
                        map.deleteCharacter(battledCharacter, battledCharacter.getPlayer());//マップからキャラを削除する
                        if(battledCharacter.getClassType() == 'E' || battledCharacter.getClassType() == 'D'){//拠点だったらゲーム終了
                        System.exit(0);//ひとまずは強制終了にしてる。ここからリザルト画面にうつるのかな
                        } else {
                            //TODO:叫びを流したい
                        }
                    }
                    battleSelectflag = false;//攻撃をする自キャラを再度選択できるようにする
                    battleCharacter.setBattleSelected(true);//戦闘済みの状態にする
                    map.haniMapInit();//範囲を書き直す
                }
            }
            if(map.getCharaPosition(array_x, array_y) == null) {//マップ情報のやつ
                modelMapInfo.changeText("<html>地面<body><br />何もない</body></html>");//マップのカーソルがキャラクター以外にある時
            } else {//キャラクターの情報を出力
                String move;
                String attack;
                if(map.getCharaPosition(array_x, array_y).getMoveSelected() == false) {//移動済みかどうか
                    move = "できる";
                } else {
                    move = "できない";
                }
                if(map.getCharaPosition(array_x, array_y).getBattleSelected() == false) {//戦闘済みがどうか
                    attack = "できる";
                } else {
                    attack = "できない";
                }
                modelMapInfo.changeText("<html>プレイヤー:"+ map.getCharaPosition(array_x, array_y).getPlayer() +"<br />"+
                map.getCharaPosition(array_x, array_y).getName() +"<br />残りHP:"+
                map.getCharaPosition(array_x, array_y).getHitPoint() + "<br />人に対する攻撃力:" +
                map.getCharaPosition(array_x, array_y).getAttackPoint() + "<br />拠点に対する攻撃力:" +
                map.getCharaPosition(array_x, array_y).getAttackToBuilding() + "<br />移動:" +
                move + "<br />攻撃:" + attack + "</html>");
            }
        }else if (btn == MouseEvent.BUTTON3){
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
        haniMapData = new char [verticalLength][horizontalLength];//範囲を表す配列を初期化
        charaPosition = new BaseCharacter [verticalLength][horizontalLength];
        setBuilding();
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
    public void setBuilding() {//マップの拠点のタイルから拠点のキャラクターを生成する
        for(int i = 0; i < verticalLength; i++) {
            for(int j = 0; j < horizontalLength; j++) {
                if(stageMapData[i][j] == 'E') {
                    addCharacter(new Kyoten(32*j, 32*i, 1), 1);
                }
                if(stageMapData[i][j] == 'D') {
                    addCharacter(new Kyoten(32*j, 32*i, 2), 2);
                }
            }
        }
    }
    public void charaPositionInit() {//キャラの位置関係をリセットする。
        for(int i = 0; i < verticalLength; i++) {
            for(int j = 0; j < horizontalLength; j++) {
                charaPosition[i][j] = null;
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
        charaPositionInit();
    }
    public void deleteCharacter(BaseCharacter chara, int player) {//死んだときとかにキャラクターを消す
        if(player == 1) {
            character1.remove(chara);
        } else {
            character2.remove(chara);
        }
    }
    public int getListSize(int player) {//リストの大きさを取得
        if(player == 1) {
            return character1.size();
        } else {
            return character2.size();
        }
    }
    public ArrayList<BaseCharacter> getList(int player) {//キャラクターが入ってるリストを取得
        if(player == 1) {
            return character1;
        } else {
            return character2;
        }
    }
    public void reChracterMoveandBattle(int player) {//ターンの終了時に移動と攻撃を再びできるようにする
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
    public char getHaniMapCode(int x, int y) {//塗りしているタイルそのものを返す
        return  haniMapData[y][x];
    }
    public void setStageMapCode(int x, int y, char tile) {//多分マップエディタで使う
        stageMapData[y][x] = tile;
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
    public void paintSummonRange(int x, int y, int speed) {//召喚判定
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {//移動不能マスを染めないだけ。移動の判定とは少しアルゴリズムが違う
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
    public void paintMoveRange(int x, int y, int speed) {//移動範囲を塗りつぶす
        boolean stopFlag = false;//j = 0, i != 0のときに移動不可マスにぶつかったかどうかを記録する
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {//まず横のマスを見ていき移動不能マスや人にぶつかったらそれ以上塗りつぶすのをやめて縦に1マス進める。また横に見てくのを繰り返し。縦に一マス進めたときにぶつかったら塗りつぶすのをやめる.
                if((i == 0 && j == 0) || !(0 <= x+j && x+j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    continue;//本人のいるとことかは飛ばす
                }
                boolean canMoveTile = ((stageMapData[y+i][x+j] == '.' || stageMapData[y+i][x+j] == '5' || stageMapData[y+i][x+j] == '6' || stageMapData[y+i][x+j] == '7') && charaPosition[y+i][x+j] == null);//移動できるマス
                if(canMoveTile == true){//移動できるマスなら
                    haniMapData[y+i][x+j] = '1';//染める
                } else if(canMoveTile == false && j == 0){//移動出来ない且つ縦に1マス進めたときなら
                    stopFlag = true;//塗りつぶすのをやめる
                    break;
                } else {//移動できないだけならば
                    break;//iを1つ進める
                }
            }
            if(stopFlag == true) {//ループを抜ける
                stopFlag = false;
                break;
            }
        }//以下も同様
        for(int i = 0; i <= speed; i++) {
            for(int j = 0; j <= speed; j++) {
                if((i == 0 && j == 0) || !(0 <= x-j && x-j < horizontalLength && 0 <= y+i && y+i < verticalLength)) {
                    continue;
                }
                boolean canMoveTile = ((stageMapData[y+i][x-j] == '.' || stageMapData[y+i][x-j] == '5' || stageMapData[y+i][x-j] == '6' || stageMapData[y+i][x-j] == '7') && charaPosition[y+i][x-j] == null);
                if(canMoveTile == true){
                    haniMapData[y+i][x-j] = '1';
                } else if(canMoveTile == false && j == 0){
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
                boolean canMoveTile = ((stageMapData[y-i][x-j] == '.' || stageMapData[y-i][x-j] == '5' || stageMapData[y-i][x-j] == '6' || stageMapData[y-i][x-j] == '7') && charaPosition[y-i][x-j] == null);
                if(canMoveTile == true){
                    haniMapData[y-i][x-j] = '1';
                } else if(canMoveTile == false && j == 0){
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
                boolean canMoveTile = ((stageMapData[y-i][x+j] == '.' || stageMapData[y-i][x+j] == '5' || stageMapData[y-i][x+j] == '6' || stageMapData[y-i][x+j] == '7') && charaPosition[y-i][x+j] == null);
                if(canMoveTile == true){
                    haniMapData[y-i][x+j] = '1';
                } else if(canMoveTile == false && j == 0){
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
    public void paintbattleRange(int x, int y) {//攻撃できる範囲を塗りつぶす
        for(int i = 0; i <= 1; i++) {
            for(int j = 0; j <= 1; j++) {//八方位の8マスを染める
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
    public void saveMap() throws IOException{//マップエディタ用。マップをテキストファイルに保存する
        File file = new File("game/map6.txt");//保存するファイル
        FileWriter filewriter = new FileWriter(file);//ファイルに書き込む物
        for(int i = 0; i < verticalLength; ++i) {//横一行ずつ保存していく
            String s = new String(stageMapData[i]);
                filewriter.write(s);
                filewriter.write("\n");
        }
        filewriter.close();
    }
    public BaseCharacter getKyoten(int player) {//拠点を返す
        BaseCharacter c;
        if(player == 1) {
            c = character1.get(0);
        } else {
            c = character2.get(0);
        }
        return c;
    }
    public void autoCreateMap() {//自動生成
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
            mapImage = createImage("game/MapTile.png", 1);//ステージのマップだけ表示する.範囲とかは生成しないで良い
        } catch (Exception e) {
            //TODO: handle exception
        }
        g.drawImage(mapImage, 0, 0, this);
        g.setColor(rectColor);
        g.drawRect(rect_x, rect_y, TileSize, TileSize);
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();//どのボタンかを取得
        Point point = e.getPoint();//クリックされた座標を取得
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);//カーソルの座標
            rect_y = point.y- (point.y%TileSize);
            int array_x = rect_x/32;//配列に入れる座標
            int array_y = rect_y/32;
            this.map.setStageMapCode(array_x, array_y, state.getNowTile());//ここで配列を書き換えて見た目を変えている
        }else if (btn == MouseEvent.BUTTON3){
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
    StageEditScreen screen;//ゲーム画面用の変数
    JButton save = new JButton("save");//押すと現在のエディタのマップがテキストファイルに保存される
    JButton b [] = new JButton[16];//タイル
    JPanel p1;
    JPanel p2;
    JPanel p3;
    JButton autoMapCreate;
    ImportTile tile = new ImportTile("game/MapTile.png");
    public StageEditFrame() throws IOException {
        JPanel panel = new JPanel();
        screen = new StageEditScreen("game/map6.txt");
        p1=new JPanel();p2=new JPanel(); p3 = new JPanel();
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
        panel.setLayout(new GridLayout(1,1));
        panel.add(screen);
        p2.setLayout(new GridLayout(4,4));
        p1.setLayout(new GridLayout(1,2));
        p1.add(autoMapCreate);
        autoMapCreate.addActionListener(this);
        p1.add(save);
        for(int i = 0; i < 16; i++) {//actionListernerいれる奴
            b[i].addActionListener(this);;
        }
        for(int i = 0; i < 16; i++) {//パネルにはっつける
            p2.add(b[i]);
        }
        save.addActionListener(this);
        this.add(p1, BorderLayout.SOUTH);
        this.add(p2,BorderLayout.EAST);
        this.add(panel, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){//押すボタンによってセットするタイルを変えてる
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
                screen.map.saveMap();//セーブ
            } catch (Exception IE) {
                //TODO: handle exception
            }
        } else if(e.getSource() == autoMapCreate) {
            screen.map.autoCreateMap();
            screen.repaint();
        }
    }
}
class DynamicTextLabel extends JLabel implements Observer {
    DynamicTextModel model;
    public DynamicTextLabel(DynamicTextModel dtm) {
        model = dtm;
        this.setText(model.getText());
        model.addObserver(this);
    }
    public void update(Observable o, Object arg){
        this.setText(model.getText());
    }
}
class DynamicTextModel extends Observable{
    /* 最初と最後の境目を表現するため、1つダミーを用意 */
    protected final int MEMORY_SIZE = 6;
    protected final String EMPTY_STR = "here_is_empty.";
    protected final String DUMMY_STR = "here_is_dummy."; // 最初だけ使う
    protected String texts[] = new String[MEMORY_SIZE]; // リンクバッファとして使う
    /* 表示している場所のindex, ダミーのindex, 最後に記録された場所のindex */
    protected int index, dum_index, cur_index;

    public DynamicTextModel(String str) {
        //Arrays.fill(texts, EMPTY_STR);
        index = 0; dum_index = 0;
        texts[index] = DUMMY_STR;
        texts[++index] = str;
        cur_index = index;
    }

    protected int previousIndex(int i) {
        return --i >= 0 ? i : MEMORY_SIZE-1;
    }

    protected int nextIndex(int i) {
        return ++i < MEMORY_SIZE ? i : 0;
    }

    public String getText() {
        return texts[index];
    }

    /* テキストを加える */
    public void changeText(String str) {
        cur_index = this.nextIndex(cur_index);
        index = cur_index; // indexを最新にする

        if(index == dum_index) { // ダミーなら現在地の次を空にする
            dum_index = this.nextIndex(index);
        }
        texts[index] = str; // 文字列代入

        setChanged();
        notifyObservers();
    }

    /* 1つ前のテキストを表示 */
    public void toPrevious() {
        if(this.isFirstNow()) { return; } // これ以上戻れない

        index = this.previousIndex(index);

        setChanged();
        notifyObservers();
    }

    /* 1つ後のテキストを表示 */
    public void toNext() {
        if(this.isLastNow()) { return; } // これ以上進めない

        index = this.nextIndex(index);

        setChanged();
        notifyObservers();
    }

    /* cur_indexに移動 */
    public void toCurrent() {
        index = cur_index;

        setChanged();
        notifyObservers();
    }

    /* 現在指しているindexは最初のindex? */
    public boolean isFirstNow() {
        return this.previousIndex(index) == dum_index;
    }

    /* 現在指しているindexは最後のindex? */
    public boolean isLastNow() {
        return this.nextIndex(index) == dum_index ||
               texts[ this.nextIndex(index) ] == EMPTY_STR;
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
        repaint();
    }
}
