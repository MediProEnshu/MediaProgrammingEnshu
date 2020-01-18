package game;

public class BaseCharacter {
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
            //TODO: handle exception
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
                tile = new ImportTile("キャラ1A.png");
            } else {
                tile = new ImportTile("キャラ1B.png");
            }
        } else if(character == '1') {
            if(player == 1) {
                tile = new ImportTile("キャラ2A.png");
            } else {
                tile = new ImportTile("キャラ2B.png");
            }
        } else if(character == '2') {
            if(player == 1) {
                tile = new ImportTile("キャラ3A.png");
            } else {
                tile = new ImportTile("キャラ3B.png");
            }
        } else if(character == '3') {
            if(player == 1) {
                tile = new ImportTile("キャラ4A.png");
            } else {
                tile = new ImportTile("キャラ4B.png");
            }
        } else if(character == '4') {
            if(player == 1) {
                tile = new ImportTile("キャラ5A.png");
            } else {
                tile = new ImportTile("キャラ5B.png");
            }
        }
        else if(character == 'E') {
            tile = new ImportTile("キャラクター.png");
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