package start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;

/* View */
/////////////////////////////////////////////////////////
public class StartSelect extends JPanel implements ActionListener{
    public BaseSelectModel model;
    public BaseSelectView view;
    public BaseSelectController controller;
    private JLabel result; // コマンド選択に応じて表示するラベル

    final int GAME_START = 0;
    final int GAME_QUIT = 1;
    //final int GAME_RESULT = 2;
    final int UPDATE_CURSOR = 3;
    private int start_state = -1; // 選択したボタンに応じて値が変化する（統括への通知用）

    //ゲーム終了時（Start2Quit）に使う,utilにもTimerあるので冗長になってる
    private javax.swing.Timer timer;

    public StartSelect() {
        String[] cmds = {"ゲームを始める", "ゲームを終わる" }; // コマンドを追加
        /* BaseSelectのMVCを呼ぶ */
        this.model = new BaseSelectModel(cmds);
        view = new BaseSelectView(model);
        controller = new BaseSelectController(model);

	//画面のタイトル（Title）と操作説明部分（Notice）
	JLabel Title = new JLabel("†陣地防衛ゲーム†");
	JLabel Notice = new JLabel("↑↓キーで選択　Enterキーで決定");

        //model.addObserver(this); // コマンド選択用のObservableを監視.
        this.addKeyListener(controller); // ここのコンポーネントに対する入力をBaseSelectControllerが受け付けるようにする.

        //画面にテキストを表示する
	this.add(Title, BorderLayout.NORTH);
        this.add(view, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
	this.add(Notice, BorderLayout.SOUTH);

    }

    public int Update(/*Observable o,Object arg*/){
      if ( model.getIsDetermined() ){ // 決定ボタンを押されていた場合.
          model.fleezeSelect(); // 選択入力の凍結(監視の取りやめ).
          int comm = model.getCurrentPosition(); // コマンドの位置
          switch(comm){ // 選択したコマンドに応じた実行.
              case 0:
                  start_state = GAME_START;
                  break;
              case 1:
                  start_state = GAME_QUIT;
                  break;
          }
      }else{ // カーソルを動かした場合.
          start_state = UPDATE_CURSOR;
      }

      return start_state;
    }

    public BaseSelectController getBaseSelectController(){
        return controller;
    }

    ///
    ///ToGame : start画面からゲーム画面への遷移
    ///
    public void ToGame(){
        result = new JLabel("みんなの太陽一等賞!!");
	JPanel waffle = new JPanel();
        view.removeAll();
        view.add(result, BorderLayout.CENTER);
        this.add(view, BorderLayout.CENTER);
        this.revalidate(); // GUIの更新
        this.repaint(); // revalidate()に加えて必要らしい
    }

    ///
    ///ToQuit : start画面からゲーム終了（ウインドウを閉じる）への遷移、2秒後にすべてのウインドウを閉じる
    ///
    public void ToQuit(){
        //指定秒数遅らせるためのtimer
        timer = new javax.swing.Timer(2000, this);
        timer.start();

        result = new JLabel("ゲームを終了しています…");
        view.removeAll();
        view.add(result, BorderLayout.CENTER);
        this.add(view, BorderLayout.CENTER);
        this.revalidate(); // GUIの更新
        this.repaint(); // revalidate()に加えて必要らしい
    }


    ///
    ///Update_Cursor : カーソルの位置を更新する
    ///
    public void Update_Cursor(){
      view.updateLabel(); // カーソルの状態更新(ここでやらないと都合が悪いので、ここで呼び出してください、ごめんなさい).
      this.revalidate(); // GUIの更新
      this.repaint(); // revalidate()に加えて必要らしい
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == timer){
            System.exit(0);
        }
    }

    /* main関数 */
    public static void main(String argv[]) {
      new StartSelect();
    }
}
