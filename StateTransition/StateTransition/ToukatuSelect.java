import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/* View */
/////////////////////////////////////////////////////////
class ToukatuSelect extends JFrame implements Observer{
    StartSelect ss;
    //GameManager gm;
    ResultSelect rs;

    //画面遷移用の変数
    private int state = -1;
    final int GAME_START = 0;
    final int GAME_QUIT = 1; 
    //final int GAME_RESULT = 2;
    final int UPDATE_CURSOR = 3;

    private boolean is_start = false;
    //final boolean is_game;
    private boolean is_result = false;

    public ToukatuSelect() {
        //oserverの設定
	ss = new StartSelect();
        ss.model.addObserver(this);
        this.add(ss, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
        is_start = true;
	this.addKeyListener(ss.getBaseSelectController());
        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    //modelが更新されると実行される
    public void update(Observable o, Object arg){//備え付けのほうが小文字
        if(is_start == true){//ss
	    state = ss.Update();//勝手に作ったほうが大文字
	    switch(state){ // 選択したコマンドに応じた実行.
	        case GAME_START:
		    ss.ToGame();

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
