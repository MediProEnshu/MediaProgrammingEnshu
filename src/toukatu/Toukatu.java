package toukatu;

import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;
import start.*;
import game.*;
// import result.*;

/* 定数のimport */
import static start.StartManager.START_GAME;
// import static start.START_EDIT;
import static start.StartManager.START_QUIT;
import static start.StartManager.START_UPDATE;


/* View */
/////////////////////////////////////////////////////////
public class Toukatu extends JFrame implements Observer{
    StartManager start;
    MapPanel mp;
    // ResultManager result;

    private int toukatuState;

    private static final int START = 0;
    private static final int GAME = 1;
    private static final int EDIT = 2;
    private static final int RESULT = 3;


    public Toukatu() {
        toukatuState = START;

        // スタート画面のセットアップ.
        start = new StartManager();
        start.model.addObserver(this);
        this.add(start, BorderLayout.CENTER);
        this.addKeyListener(start.getController());

        // 画面の表示処理
        this.setSize(1000, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void update(Observable o, Object arg){
        switch(toukatuState){
            case START:
                switch(start.updateStart()){ // 選択したコマンドに応じた実行.
                    case START_GAME:
                        this.getContentPane().removeAll();
                        toukatuState = GAME;
                        try {
                            mp = new MapPanel();
                        } catch(IOException e){
                            System.out.println("ゲーム部分に関するエラー");
                        }
                        this.add(mp, BorderLayout.CENTER);
                        this.revalidate();
                        this.repaint();
                        break;
                    case START_QUIT:
                        start.toQuit();
                        new QuitGame();
                        break;
                    case START_UPDATE:
                        // do nothing;
                        break;
                    default:
                        System.out.println("Toukatuの実行中に原因不明のエラーが発生しました.");
                        new QuitGame();
                }
                break;
        }
    }


    /* main関数 */
    public static void main(String argv[]) {
        new Toukatu();
    }
}
