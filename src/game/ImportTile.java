package game;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImportTile extends Component {//タイルチップを読み込むクラス
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