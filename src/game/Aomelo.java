package game;

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

final class GameScreen extends JPanel {
    static final int startX = 16;
    static final int startY = 16;
    volatile int x;
    volatile int y;
    int TileSize = 32;
    BufferedImage playerImage;
    BufferedImage mapImage;
    boolean doneFirstDrawing;

    GameScreen(CreateMap map) {
        setBackground(Color.BLACK);
        ImportTile tileset = new ImportTile();
        this.playerImage = tileset.getTile('E');
        this.mapImage = map.createImage();
        initialize();
    }
    void initialize() {
        this.x = TileSize * (startX + 4);
        this.y = TileSize * (startY + 4);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!doneFirstDrawing) {
            g.drawString("", 0, 0);
            doneFirstDrawing = true;
        }
        // マップ表示
        g.drawImage(mapImage.getSubimage(x, y, getWidth(), getHeight()), 0, 0, this);
        // プレイヤーキャラクター表示
        g.drawImage(playerImage, TileSize * 4, TileSize * 4, this);
    }

}

class CreateMap {

    private char[][] data;
    private int TileSize = 32;
    public CreateMap() {
        this.data = readMapData();
    }

    private char[][] readMapData() {
        ArrayList<String> a = new ArrayList<>();
        Scanner scanner = new Scanner(getClass().getResourceAsStream("map.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                a.add(line.replace("\t", ""));
            }
        int lineCount = a.size();
        char[][] chars = new char[lineCount][];
        for (int i = 0; i < lineCount; i++) {
            chars[i] = a.get(i).toCharArray();
        }
        // すべての行が同じ桁数かどうかのチェックは省略
        return chars;
    }
    BufferedImage createImage() {
        BufferedImage bi = new BufferedImage(TileSize * 124, TileSize * 120, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        ImportTile tileset = new ImportTile();
        int verticalLength = data.length;
        int horizontalLength = data[0].length;
        int gY = TileSize * 8;
        for (int y = 0; y < verticalLength; y++) {
            int gX = TileSize * 8;
            for (int x = 0; x < horizontalLength; x++) {
                g.drawImage(tileset.getTile(getCode(x, y)), gX, gY, null);
                gX += TileSize;
            }
            gY += TileSize;
        }
        return bi;
    }
    char getCode(int x, int y) {
        // 引数チェックは省略
        return data[y][x];
    }

    boolean canPass(int x, int y) {
        // 第6ビットが0なら通過できる
        return (getCode(x, y) & 0b01000000) == 0;
    }
}
class ImportTile extends Component {
    public final int TileSize = 32;
    static String codemap = ".0123456789ABCDE";
    private BufferedImage tileset = null;
    public ImportTile(){
        String imageFilename = "TileSet.PNG";
        try {//[104]
            this.tileset = ImageIO.read(new File(imageFilename));//[105]
        } catch (IOException e) {//[106]
            System.out.println("image file not found. [" + imageFilename + "]");//[107]
        }
    }
    public void paint(Graphics graphics) {//[110]
        graphics.drawImage(tileset, 0, 0, null);//[111]
    }
    BufferedImage getTile(char code) {
        int index = codemap.indexOf(code);
        int x = (index % 4) * TileSize;
        int y = (index / 4) * TileSize;
        return tileset.getSubimage(x, y, TileSize, TileSize);
    }
}

public class Aomero extends JFrame {
    CreateMap map;
    GameScreen screen;
    public Aomero() throws IOException {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,1));
        this.setBackground(Color.BLUE);
        this.add(panel, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public static void main(String[] args) throws IOException {
        new Aomero();
    }
}
