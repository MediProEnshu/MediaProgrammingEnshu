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
import static start.StartPanel.START_GAME;
import static start.StartPanel.START_EDIT;
import static start.StartPanel.START_QUIT;
import static start.StartPanel.START_UPDATE;
import static game.GameEscape.GAME_RESULT;
import static game.GameEscape.GAME_START;
import static result.ResultPanel.RESULT_START;
import static result.ResultPanel.RESULT_QUIT;
import static result.ResultPanel.RESULT_UPDATE;

/* View */
/////////////////////////////////////////////////////////
public class Toukatu extends JFrame implements Observer{
    StartPanel start;
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
        start = new StartPanel();
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
                        System.out.println("ゲーム画面でコマンド選択エラーが発生しました.");
                        new QuitGame();
                }
                break;
            case TOUKATU_GAME:
                switch(ge.getGameState()) {
                    case GAME_RESULT:
                        toukatuState = TOUKATU_RESULT;
                        this.getContentPane().removeAll();
                        rp = new ResultPanel();
                        rp.model.addObserver(this);
                        this.add(rp, BorderLayout.CENTER);
                        this.addKeyListener(rp.getController());
                        this.revalidate();
                        this.repaint();
                        break;
                    case GAME_START:
                        new QuitGame();
                        break;
                }
                break;
            case TOUKATU_RESULT:
                switch(rp.updateResult()) { // 選択したコマンドに応じた実行.
                    case RESULT_START:
                        this.getContentPane().removeAll();
                        toukatuState = TOUKATU_START;
                        start = new StartPanel();
                        start.model.addObserver(this);
                        this.add(start, BorderLayout.CENTER);
                        this.addKeyListener(start.getController());
                        this.revalidate();
                        this.repaint();
                        break;
                    case RESULT_QUIT:
                        rp.toQuit();
                        new QuitGame();
                        break;
                    case RESULT_UPDATE:
                        // do nothing;
                        break;
                    default:
                        System.out.println("リザルト画面でコマンド選択エラーが発生しました.");
                        new QuitGame();
            }
        }
    }


    /* main関数 */
    public static void main(String argv[]) {
        new Toukatu();
    }
}
