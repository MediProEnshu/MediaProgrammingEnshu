import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* BaseSelectのテスト用 */
/* BaseSelectを使用する上層オブジェクトを想定. */
/* BaseSelectを使用する際の参考にどうぞ. */

/* View */
/////////////////////////////////////////////////////////
class BaseSelectTestView extends JFrame implements Observer{
    BaseSelectModel model;
    BaseSelectView view;
    BaseSelectController controller;
    JLabel result; // コマンド選択に応じて表示するラベル

    public BaseSelectTestView() {
        String[] cmds = {"1.小宮果穂", "2.園田智代子", "3.西城樹里", "4.杜野凛世", "5.有栖川夏葉", "6.放課後クライマックスガールズ" }; // コマンドを追加
        /* BaseSelectのMVCを呼ぶ */
        model = new BaseSelectModel(cmds);
        view = new BaseSelectView(model);
        controller = new BaseSelectController(model);

        model.addObserver(this); // コマンド選択用のObservableを監視.
        this.addKeyListener(controller); // ここのコンポーネントに対する入力をBaseSelectControllerが受け付けるようにする.
        this.add(view, BorderLayout.CENTER); // Viewの追加 ここではボーダーレイアウトの中央に追加している.
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void update(Observable o, Object arg){
      if ( model.getIsDetermined() ){ // 決定ボタンを押されていた場合.
          model.fleezeSelect(); // 選択入力の凍結(監視の取りやめ).
          int comm = model.getCurrentPosition(); // コマンドの位置
          switch(comm){ // 選択したコマンドに応じた実行.
              case 0:
                  result = new JLabel("みんなの太陽一等賞!!");
                  break;
              case 1:
                  result = new JLabel("かわいい一等賞!!");
                  break;
              case 2:
                  result = new JLabel("やさしい一等賞!!");
                  break;
              case 3:
                  result = new JLabel("大和なでしこ一等賞!!");
                  break;
              case 4:
                  result = new JLabel("筋肉一等賞!!");
                  break;
              case 5:
                  result = new JLabel("ナンバーワンッ!!!!!");
                  break;
          }
          view.removeAll();
          view.add(result, BorderLayout.CENTER);
          this.add(view, BorderLayout.CENTER);
      }else{ // カーソルを動かした場合.
          view.updateLabel(); // カーソルの状態更新(ここでやらないと都合が悪いので、ここで呼び出してください、ごめんなさい).
      }
      this.revalidate(); // GUIの更新
      this.repaint(); // revalidate()に加えて必要らしい
    }

    /* main関数 */
    public static void main(String argv[]) {
        new BaseSelectTestView();
    }
}