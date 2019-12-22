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
import java.util.Iterator;
class GameScreen extends JPanel implements MouseListener{
    static final int startX = 0;
    static final int startY = 0;
    int x;
    int y;
    int width = 0;
    int height = 0;
    int TileSize = 32;
    int rect_x = 0;
    int rect_y = 0;
    DrawCharacter chara;
    boolean rect_flag = false;
    BufferedImage mapImage;
    BufferedImage charaImage;
    Map map = new Map("map1.txt");
    public GameScreen() throws IOException {
        this.mapImage = createImage("Tile.png", true);
        //this.charaImage = createImage("Charaset.PNG");
        this.width = mapImage.getWidth();//サイズ設定
        this.height = mapImage.getHeight();
        addMouseListener(this);
        initialize();
    }
    void initialize() {//ゲーム画面の初期位置の設定
        this.x = TileSize * (startX);
        this.y = TileSize * (startY);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mapImage.getSubimage(x, y, getWidth(), getHeight()), 0, 0, this);
        //g.drawImage(charaImage.getSubimage(x, y, getWidth(), getHeight()), 0, 0, this);
        g.setColor(Color.RED);
        if(rect_flag == true) {
        g.drawRect(rect_x, rect_y, TileSize, TileSize);
        }
    }
    public BufferedImage createImage(String fileName, boolean type) throws IOException{//マップを
        BufferedImage bi = new BufferedImage(TileSize * 124, TileSize * 120, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        ImportTile tileset = new ImportTile(fileName);//タイルセットを呼び出す
        int verticalLength = map.getVerticalLength();//縦の長さ
        int horizontalLength = map.getHorizontalLength();//横の長さ
        int gY = 0;//タイルをどこから書き始めるか。この設定だと大体左上
        for (int y = 0; y < verticalLength; y++) {
            int gX = 0;
            for (int x = 0; x < horizontalLength; x++) {
                if(type) {
                g.drawImage(tileset.getTile(map.getStageMapCode(x, y)), gX, gY, null);//描画
                } else {
                    g.drawImage(tileset.getTile(map.getStageMapCode(x, y)), gX, gY, null);//描画
                }
                gX += TileSize;//タイルの大きさ分横にずらす
            }
            gY += TileSize;//タイルの大きさ分縦にずらす
        }
        return bi;
    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();
        Point point = e.getPoint();
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);
            rect_y = point.y- (point.y%TileSize);
            rect_flag = true;

            if(rect_x == chara.model.getPosition().x && rect_y == chara.model.getPosition().y && chara.model.selected == false)  {
                chara.model.selected = true;
            }
            else if(chara.model.selected == true) {
                chara.model.setPosition(rect_x, rect_y);
                rect_flag = false;
                chara.model.selected = false;
            }
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
    public BaseCharacter(int hp, String name, int x, int y, int player, char classType) {
        if(hp < 0){ hp = 0; } // 不適切なヒットポイントの修正
        maxHitPoint = hp;
        hitPoint = hp;
        this.x = x;
        this.y = y;
        this.name = name;
        this.player = player;
        this.classType = classType;
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
    void attack(BaseCharacter c) {
//
    }
    /* damageが0以上なら損傷、0未満なら治療. */
    public void giveDamage(int attackPoint) {
        int damage = attackPoint - getAttackPoint(); 
        hitPoint -= damage;
        if(hitPoint < 0)           { hitPoint = 0; }
        if(hitPoint > maxHitPoint) { hitPoint = maxHitPoint; }
    }
    public void setGraphic(String filepath) throws IOException{   
        this.graphic =  ImageIO.read(new File(filepath));
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
    //private BaseCharacter [][] charaMap;
    private int horizontalLength;
    private int verticalLength;
    private ArrayList<BaseCharacter> character1 = new ArrayList<BaseCharacter>();
    //private ArrayList<BaseCharacter> character2 = new ArrayList();
    public Map(String stageMapFile) {
        stageMapData = readstageMapData(stageMapFile);
        horizontalLength = stageMapData[0].length;
        verticalLength = stageMapData.length;
        charaMapData = new char [verticalLength][horizontalLength];
        BaseCharacter c = new BaseCharacter(5, "chara1", 32, 32, 1, '1');
        character1.add(c);
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
        for(int i = 0; i < horizontalLength; i++) {
            for(int j = 0; j < verticalLength; j++) {
                charaMapData[j][i] = '.';
            }
            for(Iterator it = character1.iterator(); it.hasNext(); ){
                BaseCharacter c = (BaseCharacter)(it.next());
                charaMapData[c.getPosition().y][c.getPosition().x] = c.getClassType();
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
    public void setCode(int x, int y, char tile) {
        charaMapData[y][x] = tile;
    }
    public int getHorizontalLength() {
        return horizontalLength;
    }
    public int getVerticalLength() {
        return verticalLength;
    }
}

class Frame extends JFrame {
    public Frame() throws IOException {
        JPanel panel = new JPanel();
        CreateMap map = new CreateMap();
        GameScreen screen = new GameScreen();
        panel.setLayout(new GridLayout(1, 1));
        panel.setPreferredSize(new Dimension(screen.width, screen.height));
        panel.add(screen);
        this.add(panel, BorderLayout.CENTER);
        this.pack(); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public static void main(String[] args) throws IOException {
        new Frame();
    }
}
