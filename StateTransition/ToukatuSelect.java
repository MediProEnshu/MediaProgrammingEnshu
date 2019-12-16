import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/* View */
/////////////////////////////////////////////////////////
class ToukatuSelect extends JFrame implements Observer{
    StartSelect ss = new StartSelect();
    ResultSelect rs;

    //画面遷移用の変数
    private int state = -1;
    final int GAME_START = 0;
    final int GAME_QUIT = 1; 
    final int UPDATE_CURSOR = 2;

    public ToukatuSelect() {
        //oserverの設定
        ss.model.addObserver(this);
        this.add(ss, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    //modelが更新されると実行される
    public void update(Observable o, Object arg){

	  state = ss.Update();
          switch(state){ // 選択したコマンドに応じた実行.
              case GAME_START:
                  rs = new ResultSelect();
                  rs.ToGame();
                  break;
              case GAME_QUIT:
                  ss.ToQuit();
                  break;
              case UPDATE_CURSOR:
                  ss.Update_Cursor();
                  break;
          }
    }


    /* main関数 */
    public static void main(String argv[]) {
        new ToukatuSelect();
    }
}
