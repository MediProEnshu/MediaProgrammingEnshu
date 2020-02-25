package game;

import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class Map {//マップを生成するクラス
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