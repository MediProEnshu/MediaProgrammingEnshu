package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

class StageEditScreen extends GameScreen implements MouseListener{//エディタのViewにあたる部分
    public StageEditScreen(String file, GameEscape ge) throws IOException {
        super(file, ge);
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

public class StageEditPanel extends JPanel implements ActionListener {//いつものUIにあたるVの部分.起動するのにはnew StageEditPanel()をどっかでやればいいはず.
    StageEditScreen screen;//ゲーム画面用の変数
    GameEscape escape;
    JButton save = new JButton("save");//押すと現在のエディタのマップがテキストファイルに保存される
    JButton b [] = new JButton[16];//タイル
    JPanel p1;
    JPanel p2;
    JPanel p3;
    JButton autoMapCreate;
    ImportTile tile = new ImportTile("game/MapTile.png");

    public StageEditPanel(GameEscape ge) throws IOException {
        escape = ge;
        JPanel panel = new JPanel();
        screen = new StageEditScreen("map6.txt", ge);
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
        this.setLayout(new BorderLayout());
        this.add(p1, BorderLayout.SOUTH);
        this.add(p2,BorderLayout.EAST);
        this.add(panel, BorderLayout.CENTER);
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
                escape.toStart();
            } catch (Exception IE) {
                //TODO: handle exception
            }
        } else if(e.getSource() == autoMapCreate) {
            screen.map.autoCreateMap();
            screen.repaint();
        }
    }
}