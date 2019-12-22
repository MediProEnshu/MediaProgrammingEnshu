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

/* View */
/////////////////////////////////////////////////////////
class ToukatuSelect extends JFrame implements Observer{
    StartSelect ss;
    MapPanel mp;
    // ResultSelect rs;

    //画面遷移用の変数
    private int state;
    final int GAME_START = 0;
    final int GAME_QUIT = 1;
    final int GAME_RESULT = 2;
    final int UPDATE_CURSOR = 3;

    private boolean is_start;
    private boolean is_result;

    public ToukatuSelect() {
        state = -1;
        is_start = true;

        //oserverの設定
        ss = new StartSelect();
        ss.model.addObserver(this);
        this.add(ss, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
        is_start = true;
        this.addKeyListener(ss.getBaseSelectController());
        this.setSize(800, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    //modelが更新されると実行される
    public void update(Observable o, Object arg){//備え付けのほうが小文字
        if(is_start == true){//ss
            state = ss.Update();//勝手に作ったほうが大文字
            switch(state){ // 選択したコマンドに応じた実行.
                case GAME_START:
                    // ss.ToGame();
                    this.removeAll();
                    try {
                        mp = new MapPanel();
                    } catch(IOException e){
                        System.out.println("ゲーム部分に関するエラー");
                    }
                    this.add(mp, BorderLayout.CENTER);
                    this.revalidate();
                    this.repaint();
/*
		    this.removeAll();
	            rs = new ResultSelect();
		    rs.model.addObserver(this);
                    this.add(rs, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
                    is_result = true;
                    is_start = false;
	            this.addKeyListener(rs.getBaseSelectController());
                    this.revalidate(); // GUIの更新
                    this.repaint(); // revalidate()に加えて必要らしい
*/
                    break;
                case GAME_QUIT:
                    ss.ToQuit();//あとでToQuitメソッドはまとめる
                    break;
                case UPDATE_CURSOR:
                    ss.Update_Cursor();
                    break;
      	    }
        }else if(is_result == true){//rs
        }
    }


    /* main関数 */
    public static void main(String argv[]) {
        new ToukatuSelect();
    }
}
