package game;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import base.*;

public class GameScreen extends JPanel implements MouseListener,ActionListener{
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
    GameEscape escape;
    int step = 1;//移動の時に使う。ステップ一が縦移動でステップ2が横移動

    public GameScreen(String file, GameEscape ge) throws IOException {
        escape = ge;
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
                    timer.start();//開始
                    map.haniMapInit();//範囲を適切に設定しなおし後で描画
                    characterTmp.setMoveSelected(true);//移動し終わったという設定をキャラにつける
                    modelTextLog.changeText("<html>player<body>"+characterTmp.getPlayer()+"<br/>の"+characterTmp.getName()+"<br/>が移動");//テキストログの設定
                  }
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
                    if(battleCharacter.getClassType() == '4') {
                        modelTextLog.changeText("<html>player<body>"+battleCharacter.getPlayer()+"<br/>の"+battleCharacter.getName()+"<br/>が"
                        + battledCharacter.getPlayer() + "の"+ battledCharacter.getName() +"<br/>を"+ -damage + "回復させた</html>");//テキストログ
                    } else {
                        modelTextLog.changeText("<html>player<body>"+battleCharacter.getPlayer()+"<br/>の"+battleCharacter.getName()+"<br/>が"
                        + battledCharacter.getPlayer() + "の"+ battledCharacter.getName() +"<br/>に"+ damage + "ダメージを与えた</html>");//テキストログ
                    }
                    if(battledCharacter.isDead() == true) {//戦闘でHPが0になったとき
                        map.deleteCharacter(battledCharacter, battledCharacter.getPlayer());//マップからキャラを削除する
                        if(battledCharacter.getClassType() == 'E' || battledCharacter.getClassType() == 'D'){//拠点だったらゲーム終了
                            escape.toResult();
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