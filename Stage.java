import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.imageio.plugins.tiff.TIFFDirectory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.Color;
class GameState {
    private boolean moveFlag;
    private boolean summonFlag;
    private boolean battleFlag;

    public GameState() {
        moveFlag = false;
        summonFlag = false;
        battleFlag = false;
    }
    public void setMoveFlag(boolean state) {
        moveFlag = state;
    }
    public void setSummonFlag(boolean state) {
        summonFlag = state;
    }
    public void setBattleFlag(boolean state) {
        battleFlag = state;
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
    DrawCharacter chara;
    boolean rect_flag = false;
    BufferedImage mapImage;
    BufferedImage charaImage;
    BufferedImage panelImage;
    Map map = new Map("map1.txt");
    public GameScreen() throws IOException {
        this.mapImage = createImage("Tile.png", 1);
        this.charaImage = createImage("CharaMap.png", 2);
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
            mapImage = createImage("Tile.png", 1);
            charaImage = createImage("CharaMap.png", 2);
            panelImage = createImage("Hani.png", 3);
        } catch (Exception e) {
            //TODO: handle exception
        }

        g.drawImage(mapImage, 0, 0, this);
        g.drawImage(charaImage, 0, 0, this);
        g.drawImage(panelImage, 0, 0, this);
        //if()
        g.setColor(rectColor);
        g.drawRect(rect_x, rect_y, TileSize, TileSize);
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
            /*if(flag == true ) {
                if(map.getCharaMapCode(rect_x/32, rect_y/32) != '.') {
                    map.deleteCharacter(map.getCharaPosition(rect_x/32, rect_y/32));
                }
            }*/
            if(state.getMoveFlag() == true && state.getBattleFlag() == false && state.getSummonFlag() == false) {
                if(map.getCharaMapCode(rect_x/32, rect_y/32) != '.' && map.getCharaFlag() == false) {
                    tmp_x = rect_x; tmp_y = rect_y;
                    map.paintselectRange(rect_x/32, rect_y/32, map.getCharaPosition(rect_x/32, rect_y/32).getSpeed());
                    map.setCharaFlag(true);
                }else if(map.getCharaFlag() == true && map.getCharaMapCode(rect_x/32, rect_y/32) == '.') {
                    BaseCharacter c = map.getCharaPosition(tmp_x/32, tmp_y/32);
                    c.move(rect_x, rect_y);
                    map.setCharaFlag(false);
                    map.haniMapInit();
                }
            } else if(state.getMoveFlag() == false && state.getBattleFlag() == false && state.getSummonFlag() == true) {
                map.addCharacter(new Otaku(rect_x, rect_y));
            } else if (state.getMoveFlag() == false && state.getBattleFlag() == true && state.getSummonFlag() == false) {
                if(map.getCharaPosition(rect_x/32, rect_y/32) != null && flag == false) {
                    tmpl = map.getCharaPosition(rect_x/32, rect_y/32);
                    map.paintselectRange(rect_x/32, rect_y/32, tmpl.getSpeed());
                    flag = true;
                } else if(flag == true) {
                    BaseCharacter chara = map.getCharaPosition(rect_x/32, rect_y/32);
                    chara.giveDamage(100);
                    if(chara.isDead() == true) {
                        map.deleteCharacter(chara);
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
    private int x = 0;
    private int y = 0;
    private int attackPoint;
    private int defencePoint;
    private int maxHitPoint;
    private int hitPoint;
    private String imagePath;
    private boolean selected = false;
    private String name; 
    private int player;
    BufferedImage graphic;
    private char classType;
    private int speed;
    public BaseCharacter(int hp, String name, int x, int y, int speed, int player, char classType) {
        if(hp < 0){ hp = 0; } // 不適切なヒットポイントの修正
        maxHitPoint = hp;
        hitPoint = hp;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.classType = classType;
        this.speed = speed;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String pathString) {
        imagePath = pathString;
    }
    public String getName() {
        return name;
    }
    public int getPlayer() {
        return player;
    }
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean getSelected() {
        return selected;
    }
    public Point getPosition() {
        Point p = new Point();
        p.x = x;
        p.y = y;
        return p;
    }
    public char getClassType() {
        return classType;
    }
    /* ヒットポイントを返す. */
    public int getHitPoint() {
        return hitPoint;
    }
    public int getAttackPoint() {
        return attackPoint;
    }
    public int getDefencePoint() {
        return defencePoint;
    }
    /* このキャラクターは絶命した? */
    public boolean isDead() {
        return hitPoint <= 0;
    }
    public int getSpeed() {
        return speed;
    }
    void attack(BaseCharacter c) {
//
    }
    /* damageが0以上なら損傷、0未満なら治療. */
    public void giveDamage(int attackPoint) {
        int damage = attackPoint; 
        hitPoint -= damage;
    }
    public void setGraphic(String filepath) throws IOException{   
        this.graphic =  ImageIO.read(new File(filepath));
    }
}
class Ippan extends BaseCharacter {
    public Ippan() {
        super(5, "一般大学生", 32, 32, 2, 1, '1');
    }
}
class Otaku extends BaseCharacter {
    public Otaku(int x, int y) {
        super(3, "オタク", x, y, 4, 5, '2');//オタクくんさあ
    }
}
class Yokya extends BaseCharacter {
    public Yokya() {
        super(5, "陽キャ", 32, 32, 2, 1, '3');//WANIMA的な
    }
}
class Kyoten extends BaseCharacter {
    public Kyoten() {
        super(5, "陽キャ", 32, 32, 2, 1, '3');//ひとまず
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
    private char[][] stageMapData;
    private char [][] charaMapData;
    private BaseCharacter [][] charaPosition;
    private char [][] haniMapData;
    private int horizontalLength;
    private int verticalLength;
    private boolean charaFlag = false;
    private ArrayList<BaseCharacter> character1 = new ArrayList<BaseCharacter>();
    //private ArrayList<BaseCharacter> character2 = new ArrayList();
    public Map(String stageMapFile) {
        stageMapData = readstageMapData(stageMapFile);
        horizontalLength = stageMapData[0].length;
        verticalLength = stageMapData.length;
        charaMapData = new char [verticalLength][horizontalLength];
        haniMapData = new char [verticalLength][horizontalLength];
        charaPosition = new BaseCharacter [verticalLength][horizontalLength];
        charaMapInit();
        haniMapInit();
        charaPositionInit();
    }
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
        return stageMapArray;
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
    }
    public void addCharacter(BaseCharacter chara) {
        character1.add(chara);
        charaMapInit();
        charaPositionInit();
    }
    public void deleteCharacter(BaseCharacter chara) {
        character1.remove(chara);
    }
    public void haniMapInit() {
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
    public char getHaniMapCode(int x, int y) {
        return  haniMapData[y][x];
    }
    public void setStageMapCode(int x, int y, char tile) {
        stageMapData[y][x] = tile;
    }
    public void setCharaMapCode(int x, int y, char tile) {
        charaMapData[y][x] = tile;
    }
    public void setHaniMapCode(int x, int y, char tile) {
        haniMapData[y][x] = tile;
    }
    public int getHorizontalLength() {
        return horizontalLength;
    }
    public int getVerticalLength() {
        return verticalLength;
    }
    public void setCharaFlag(boolean flag) {
        charaFlag = flag;
    }
    public boolean getCharaFlag() {
        return charaFlag;
    }
    public BaseCharacter getCharaPosition(int x, int y) {
        return charaPosition[y][x];
    }
    public void paintselectRange(int x, int y, int speed) {
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
    public Frame() throws IOException {
        JPanel panel = new JPanel();
        screen = new GameScreen();
        panel.setLayout(new GridLayout(1, 1));
        panel.add(screen);
        JPanel  p1=new JPanel(),p2=new JPanel();
        // JPanelに貼り付けるためのJButtonを5つ生成
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);

        JButton b5=new JButton("button 3"),b6=new JButton("button 4");
        // 2つのJPanelをそれぞれ，3x1, 2x1 のGridLayoutに設定．
        p1.setLayout(new GridLayout(4,1));
        p2.setLayout(new GridLayout(2,1));
        // ボタンをそれぞれのJPanelに貼付け
        p1.add(b1); p1.add(b2); p1.add(b3);p1.add(b4);
        p2.add(b5); p2.add(b6);
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
        } else if(e.getSource()==b2) {
            screen.rectColor = Color.red;
            screen.state.setMove();
        } else if(e.getSource() == b3) {
            screen.rectColor = Color.yellow;
            screen.state.setBattle();
        } else if(e.getSource() == b4) {
            screen.flag = true;
        }
        screen.repaint();
    }
    public static void main(String[] args) throws IOException {
        new Frame();
    }
}
