package start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;

/* View */
/////////////////////////////////////////////////////////
public class StartManager extends JPanel {
    /* セレクトオブジェクトの準備 */
    public BaseSelectModel model;
    public BaseSelectView view;
    public BaseSelectController controller;

    /* スタート画面での各処理用の定数、コマンドの位置と対応させる. */
    public static final int START_GAME = 0;
    public static final int START_EDIT = 1;
    public static final int START_QUIT = 2;
    public static final int START_UPDATE = 99;

    private int start_state; // 選択したボタンに応じて値が変化する（統括への通知用）

    public StartManager() {
        start_state = -1; // 初期化. 選ばれていない状態.

        String[] cmds = {"ゲームを始める", "ステージを作る", "ゲームを終わる" }; // コマンドを追加

        /* BaseSelectのMVCを呼ぶ */
        this.model = new BaseSelectModel(cmds);
        view = new BaseSelectView(model, "MS ゴシック", 30);
        controller = new BaseSelectController(model);
        this.addKeyListener(controller); // ここのコンポーネントに対する入力をBaseSelectControllerが受け付けるようにする.


        //画面のタイトル（title）と操作説明部分（notice）
        JLabel title = new JLabel("Submission Impossible");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Impact", Font.PLAIN, 60));
        JLabel notice = new JLabel("select : ↑↓　 Press Enter");
        notice.setHorizontalAlignment(JLabel.CENTER);
        notice.setFont(new Font("MS ゴシック", Font.PLAIN, 40));


        //画面にテキストを表示する
        this.setLayout(new GridLayout(3, 1));
        this.add(title); this.add(view); this.add(notice);
    }

    /* 擬似的なUpdate(). 情報を更新された結果を返す. */
    public int updateStart() {
        if ( model.getIsDetermined() ) { // 決定ボタンを押されていた場合.
            start_state = model.getCurrentPosition(); // コマンドの位置
            model.fleezeSelect(); // 選択入力の凍結(監視の取りやめ).
        }else { // カーソルを動かした場合.
            start_state = START_UPDATE;
            view.updateLabel();
            this.revalidate();
            this.repaint();
        }

        return start_state;
    }

    public BaseSelectController getController() {
        return controller;
    }

    ///
    ///toQuit : start画面からゲーム終了（ウインドウを閉じる）への遷移、2秒後にすべてのウインドウを閉じる
    ///
    public void toQuit(){
        JLabel endMessage = new JLabel("ゲームを終了しています…");
        view.removeAll();
        this.removeAll();
        view.add(endMessage, BorderLayout.CENTER);
        this.add(view, BorderLayout.CENTER);
    }
}
