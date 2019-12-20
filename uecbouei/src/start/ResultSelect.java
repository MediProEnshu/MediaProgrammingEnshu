package start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;


/* View */
/////////////////////////////////////////////////////////
public class ResultSelect extends JPanel implements ActionListener{
    BaseSelectModel model;
    BaseSelectView view;
    BaseSelectController controller;
    JLabel result; // コマンド選択に応じて表示するラベル

    final int GAME_START = 0;
    final int GAME_QUIT = 1;
    //final int GAME_RESULT = 2;
    final int UPDATE_CURSOR = 3;
    private int result_state = -1; // 選択したボタンに応じて値が変化する（統括への通知用）

    //ゲーム終了時（Start2Quit）に使う,utilにもTimerがあるため冗長になってる
    private javax.swing.Timer timer;

    public ResultSelect() {
        String[] cmds = {"もう一回プレイする", "ゲーム終了"}; // コマンドを追加
        /* BaseSelectのMVCを呼ぶ */
        model = new BaseSelectModel(cmds);
        view = new BaseSelectView(model);
        controller = new BaseSelectController(model);

	//画面のタイトル（Title）と操作説明部分（Notice）
	JLabel Title = new JLabel("†あなたの戦績");
	JLabel Notice = new JLabel("↑↓キーで選択　Enterキーで決定");

        //model.addObserver(this); // コマンド選択用のObservableを監視.
        this.addKeyListener(controller); // ここのコンポーネントに対する入力をBaseSelectControllerが受け付けるようにする.

	//画面にテキストを表示する
	this.add(Title, BorderLayout.NORTH);
        this.add(view, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
	this.add(Notice, BorderLayout.SOUTH);

    }

    public int Update(){
      if ( model.getIsDetermined() ){ // 決定ボタンを押されていた場合.
          model.fleezeSelect(); // 選択入力の凍結(監視の取りやめ).
          int comm = model.getCurrentPosition(); // コマンドの位置
          switch(comm){ // 選択したコマンドに応じた実行.
              case 0:
                  result_state = GAME_START;
                  break;
              case 1:
                  result_state = GAME_QUIT;
                  break;
          }
      }else{ // カーソルを動かした場合.
          result_state = UPDATE_CURSOR;
      }

        return result_state;
    }

    public BaseSelectController getBaseSelectController(){
        return controller;
    }


    ///
    ///ToGame : start画面からゲーム画面への遷移
    ///
    public void ToGame(){
        result = new JLabel("みんなの太陽一等賞!!");
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
        new ResultSelect();
    }
}
