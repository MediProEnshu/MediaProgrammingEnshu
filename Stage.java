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
class GameScreen extends JPanel implements MouseListener{
    static final int startX = 0;
    static final int startY = 0;
    volatile int x;
    volatile int y;
    int width = 0;
    int height = 0;
    int TileSize = 32;
    int rect_x = 0;
    int rect_y = 0;
    boolean rect_flag = false;
    String msg = "0";
    BufferedImage playerImage;
    BufferedImage mapImage;
    boolean doneFirstDrawing;
    Graphics2D rect;
    GameScreen(CreateMap map) throws IOException{
        //ImportTile tileset = new ImportTile();//キャラの設定
        //this.playerImage = tileset.getTile('E');
        this.mapImage = map.createImage();
        this.width = mapImage.getWidth();//サイズ設定
        this.height = mapImage.getHeight();
        addMouseListener(this);
        initialize();
    }
    void initialize() {//ゲーム画面の初期位置の設定
        this.x = TileSize * (startX);
        this.y = TileSize * (startY);
    }

    @Override
    protected void paintComponent(Graphics g) {//マップの表示とキャラの表示をする.キャラの表示はまた後で...
        super.paintComponent(g);
        if (!doneFirstDrawing) {
            g.drawString("", 0, 0);
            doneFirstDrawing = true;
        }
        // マップ表示
        g.drawImage(mapImage.getSubimage(x, y, getWidth(), getHeight()), 0, 0, this);
        rect = (Graphics2D)g;
        rect.setColor(Color.RED);
        BasicStroke stroke = new BasicStroke(5.0f);
        rect.setStroke(stroke);
        if(rect_flag) {
        rect.drawRect(rect_x, rect_y, TileSize, TileSize);
        }

    }
    public void mouseClicked(MouseEvent e) {
        int btn = e.getButton();
        Point point = e.getPoint();
        if (btn == MouseEvent.BUTTON1){
            rect_x = point.x - (point.x%TileSize);
            rect_y = point.y- (point.y%TileSize);
            rect_flag = true;
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

class CreateMap {//マップを生成するクラス

    private char[][] data;
    private int TileSize = 32;
    public CreateMap() {
        this.data = readMapData();
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
    BufferedImage createImage() throws IOException{//マップを
        BufferedImage bi = new BufferedImage(TileSize * 124, TileSize * 120, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        ImportTile tileset = new ImportTile();//タイルセットを呼び出す
        int verticalLength = data.length;//縦の長さ
        int horizontalLength = data[0].length;//横の長さ
        int gY = 0;//タイルをどこから書き始めるか。この設定だと大体左上
        for (int y = 0; y < verticalLength; y++) {
            int gX = 0;
            for (int x = 0; x < horizontalLength; x++) {
                g.drawImage(tileset.getTile(getCode(x, y)), gX, gY, null);//描画
                gX += TileSize;//タイルの大きさ分横にずらす
            }
            gY += TileSize;//タイルの大きさ分縦にずらす
        }
        return bi;
    }
    char getCode(int x, int y) {//ある位置のマップの記号返す
        // 引数チェックは省略
        return data[y][x];
    }
}
class ImportTile extends Component {//タイルチップを読み込むクラス
    public final int TileSize = 32;//タイルのサイズ
    static String codemap = ".0123456789ABCDE";//タイルと文字の対応のための文字列
    private BufferedImage tileset = null;//タイルセットを読み根で格納する
    public ImportTile()throws IOException {
        String imageFilename = "TileSet.PNG";
        this.tileset = ImageIO.read(new File(imageFilename));
    }
    public void paint(Graphics graphics) {//描画する
        graphics.drawImage(tileset, 0, 0, null);
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
class Frame extends JFrame {
    public Frame() throws IOException {
        JPanel panel = new JPanel();
        CreateMap map = new CreateMap();
        GameScreen screen = new GameScreen(map);
        panel.setLayout(new GridLayout(2, 3));
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
