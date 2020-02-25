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
    GamePanel game;
    GameEscape ge;
    ResultPanel result;

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
            this.startManager();
            break;
        case TOUKATU_GAME:
            this.gameManager();
            break;
        case TOUKATU_RESULT:
            this.resultManager();
            break;
        default:
            System.out.println("統括処理部分でのエラー");
            new QuitGame();
        }
    }

    /* スタート画面での処理 */
    private void startManager() {
        switch(start.updateStart()) { // 選択したコマンドに応じた実行.
        case START_GAME:
            this.getContentPane().removeAll();
            toukatuState = TOUKATU_GAME;
            try {
                ge = new GameEscape();
                ge.addObserver(this);
                game = new GamePanel("map5.txt", ge);
            } catch(IOException e) {
                System.out.println("ゲーム部分でのエラー");
                new QuitGame();
            }
            this.add(game, BorderLayout.CENTER);
            this.revalidate();
            this.repaint();
            break;
        case START_EDIT:
            this.getContentPane().removeAll();
            toukatuState = TOUKATU_EDIT;
            try {
                ge = new GameEscape();
                ge.addObserver(this);
                game = new GamePanel("map6.txt", ge);
            } catch(IOException e) {
                System.out.println("ステージエディト部分でのエラー");
                new QuitGame();
            }
            this.add(game, BorderLayout.CENTER);
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
            System.out.println("スタート処理部分でのエラー");
            new QuitGame();
        }
    }

    /* ゲーム画面での処理 */
    private void gameManager() {
        switch(ge.getGameState()) {
        case GAME_RESULT:
            toukatuState = TOUKATU_RESULT;
            this.getContentPane().removeAll();
            result = new ResultPanel();
            result.model.addObserver(this);
            this.add(result, BorderLayout.CENTER);
            this.addKeyListener(result.getController());
            this.revalidate();
            this.repaint();
            break;
        case GAME_START:
            new QuitGame();
            break;
        default:
            System.out.println("ゲーム処理部分でのエラー");
            new QuitGame();
        }
    }

    /* リザルト画面での処理 */
    private void resultManager() {
        switch(result.updateResult()) { // 選択したコマンドに応じた実行.
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
            result.toQuit();
            new QuitGame();
            break;
        case RESULT_UPDATE:
            // do nothing;
            break;
        default:
            System.out.println("リザルト処理部分でのエラー");
            new QuitGame();
        }
    }

    /* main関数 */
    public static void main(String argv[]) {
        new Toukatu();
    }
}
