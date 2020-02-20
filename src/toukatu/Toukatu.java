package toukatu;

import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;
import start.*;
import game.*;
import result.*;

/* 定数のimport */
import static start.StartManager.START_GAME;
import static start.StartManager.START_EDIT;
import static start.StartManager.START_QUIT;
import static start.StartManager.START_UPDATE;
import static game.GameEscape.GAME_RESULT;
import static game.GameEscape.GAME_START;

/* View */
/////////////////////////////////////////////////////////
public class Toukatu extends JFrame implements Observer{
    StartManager start;
    GamePanel gp;
    GameEscape ge;
    ResultPanel rp;

    private int toukatuState;

    private static final int TOUKATU_START = 0;
    private static final int TOUKATU_GAME = 1;
    private static final int TOUKATU_EDIT = 2;
    private static final int TOUKATU_RESULT = 3;


    public Toukatu() {
        toukatuState = TOUKATU_START;

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


    public void update(Observable o, Object arg) {
        switch(toukatuState) {
            case TOUKATU_START:
                switch(start.updateStart()) { // 選択したコマンドに応じた実行.
                    case START_GAME:
                        this.getContentPane().removeAll();
                        toukatuState = TOUKATU_GAME;
                        try {
                            ge = new GameEscape();
                            ge.addObserver(this);
                            gp = new GamePanel("map5.txt", ge);
                        } catch(IOException e) {
                            System.out.println("ゲーム部分に関するエラー");
                        }
                        this.add(gp, BorderLayout.CENTER);
                        this.revalidate();
                        this.repaint();
                        break;
                    case START_EDIT:
                        this.getContentPane().removeAll();
                        toukatuState = TOUKATU_EDIT;
                        try {
                            ge = new GameEscape();
                            ge.addObserver(this);
                            gp = new GamePanel("map6.txt", ge);
                        } catch(IOException e) {
                            System.out.println("ステージエディト部分に関するエラー");
                        }
                        this.add(gp, BorderLayout.CENTER);
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
            case TOUKATU_GAME:
                switch(ge.getGameState()) {
                    case GAME_RESULT:
                        this.getContentPane().removeAll();
                        rp = new ResultPanel();
                        this.add(rp, BorderLayout.CENTER);
                        this.revalidate();
                        this.repaint();
                        break;
                    case GAME_START:
                        new QuitGame();
                        break;
                }
                break;
            case TOUKATU_RESULT:
                new QuitGame();
        }
    }


    /* main関数 */
    public static void main(String argv[]) {
        new Toukatu();
    }
}
