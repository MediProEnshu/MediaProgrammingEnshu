package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import javax.imageio.plugins.tiff.TIFFDirectory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.Point;
class CreateMap {//マップを生成するクラス
    private char[][] data;
    private int hor;
    private int ver;

    public CreateMap() {
        data = readMapData();
        hor = data[0].length;
        ver = data.length;
    }
    private char[][] readMapData() {//map.txtから文字列を読み込む
        ArrayList<String> line = new ArrayList<>();//読み込んだ文字列を格納する
        Scanner scanner = new Scanner(getClass().getResourceAsStream("map1.txt"));//map.txtを読み込む
            while (scanner.hasNextLine()) {//空行になるまで行を読み込む
                String mapstring = scanner.nextLine();//mapstringに読み込んだ行を格納
                line.add(mapstring.replace("\t", ""));//改行文字を消す
            }
        int lineCount = line.size();//読み込んだ行数を出す
        char[][] maparray = new char[lineCount][];//読み込んだ行数のサイズの行を持つ二次元配列を作る
        for (int i = 0; i < lineCount; i++) {//char型配列に読み込んだ文字列を変換
            maparray[i] = line.get(i).toCharArray();
        }
        return maparray;
    }
    public char getCode(int x, int y) {//ある位置のマップの記号返す
        // 引数チェックは省略
        return data[y][x];
    }
    public void setCode(int x, int y, char tile) {
        data[y][x] = tile;
    }
    public int gethor() {
        return hor;
    }
    public int getver() {
        return ver;
    }
}
class ImportTile extends Component {//タイルチップを読み込むクラス
    public final int TileSize = 32;//タイルのサイズ
    static String codemap = ".0123456789ABCDE";//タイルと文字の対応のための文字列
    private BufferedImage tileset = null;//タイルセットを読み根で格納する
    public ImportTile()throws IOException {
        String imageFilename = "TileSet.pngex";
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
class BaseCharacterModel {
    private int x = 0;
    private int y = 0;

    private int maxHitPoint;
    private int hitPoint;
    private String imagePath;
    boolean selected = false;
    public BaseCharacterModel(int hp, String pathString) {
        if(hp < 0){ hp = 0; } // 不適切なヒットポイントの修正
        maxHitPoint = hp;
        hitPoint = hp;
        imagePath = pathString;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String pathString) {
        imagePath = pathString;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Point getPosition() {
        Point p = new Point();
        p.x = x;
        p.y = y;
        return p;
    }

    /* ヒットポイントを返す. */
    public int getHitPoint() {
        return hitPoint;
    }

    /* このキャラクターは絶命した? */
    public boolean isDead() {
        return hitPoint <= 0;
    }

    /* damageが0以上なら損傷、0未満なら治療. */
    public void giveDamage(int damage) {
        hitPoint -= damage;
        if(hitPoint < 0)           { hitPoint = 0; }
        if(hitPoint > maxHitPoint) { hitPoint = maxHitPoint; }
    }
}
/* Object: キャラクターの表示 */
/////////////////////////////////////////////////////////
class BaseCharacterLabel extends JLabel {
    private BaseCharacterModel model;
    BufferedImage icon;
    public BaseCharacterLabel(BaseCharacterModel bcm) throws IOException{
        model = bcm;
        this.setCharacterImage();
    }
    /* キャラクターの画像をセット */
    public void setCharacterImage() throws IOException{
        icon = ImageIO.read(new File(model.getImagePath()));
    }
}

class DrawCharacter extends JPanel {
    BaseCharacterModel model;
    BaseCharacterLabel label;
    public DrawCharacter() {
        model = new BaseCharacterModel(5, "./Chara.png");
        try {
            label = new BaseCharacterLabel(model);
        } catch (IOException e) {

        }
    }
}
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
    CreateMap map = new CreateMap();
    public GameScreen() throws IOException {
        this.mapImage = createImage();
        this.width = mapImage.getWidth();//サイズ設定
        this.height = mapImage.getHeight();
        addMouseListener(this);
        initialize();
        MakeChara();
    }
    void initialize() {//ゲーム画面の初期位置の設定
        this.x = TileSize * (startX);
        this.y = TileSize * (startY);
    }
    void MakeChara() throws IOException{
        chara = new DrawCharacter();
        chara.model.setPosition(64, 64);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            mapImage = createImage();
        } catch (IOException e) {

        }
        g.drawImage(mapImage.getSubimage(x, y, getWidth(), getHeight()), 0, 0, this);
        g.drawImage(chara.label.icon, chara.model.getPosition().x, chara.model.getPosition().y, this);
        g.setColor(Color.RED);
        if(rect_flag == true)
        g.drawRect(rect_x, rect_y, TileSize, TileSize);
    }
    public BufferedImage createImage() throws IOException{//マップを
        BufferedImage bi = new BufferedImage(TileSize * 124, TileSize * 120, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        ImportTile tileset = new ImportTile();//タイルセットを呼び出す
        int verticalLength = map.getver();//縦の長さ
        int horizontalLength = map.gethor();//横の長さ
        int gY = 0;//タイルをどこから書き始めるか。この設定だと大体左上
        for (int y = 0; y < verticalLength; y++) {
            int gX = 0;
            for (int x = 0; x < horizontalLength; x++) {
                g.drawImage(tileset.getTile(map.getCode(x, y)), gX, gY, null);//描画
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

public class MapFrame extends JFrame {
    public MapFrame() throws IOException {
        JPanel panel = new JPanel();
        this.setTitle("BaseCharacter テスト");
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
        new MapFrame();
    }
}
