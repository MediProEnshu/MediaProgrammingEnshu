import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* コマンドオブジェクト */
// コマンドを保持するためのオブジェクトなので、外部的にはパラメタをもつJLabelとして振舞う.
// そのため、 View, Controllerは不要 */

/* Model: コマンド文を保持 */
/////////////////////////////////////////////////////////
class BaseCommandModel {
    private int serialID; //コマンド列のコマンド1つ1つが持つ. 0から始める.
    private String command;

    public BaseCommandModel(int num, String text) {
        this.serialID = num;
        this.command = text;
    }

    public String getCommand() {
        return this.command;
    }

    public int getSerialID() {
        return this.serialID;
    }
}


/* Object: コマンドラベルオブジェクト(Modelから取り出した情報を使う) */
/* Modelを与えられると対応する JLabel+パラメタ を作るオブジェクト. */
/////////////////////////////////////////////////////////
class BaseCommandLabel extends JLabel {
    public BaseCommandLabel(BaseCommandModel model) {
        this.setText(model.getCommand());
    }
}


/* main: テスト用 */
/////////////////////////////////////////////////////////
class BaseCommandTest extends JFrame {
    public BaseCommandTest() {
        this.setTitle("BaseSelect テスト");
        this.setLayout(new GridLayout(6, 1));
        /* コマンド登録 */
        BaseCommandModel command1 = new BaseCommandModel(0, "チーカマ");
        BaseCommandModel command2 = new BaseCommandModel(1, "味噌田楽");
        BaseCommandModel command3 = new BaseCommandModel(2, "しぐれ煮");
        BaseCommandModel command4 = new BaseCommandModel(3, "煮っころがし");
        BaseCommandModel command5 = new BaseCommandModel(4, "もつ煮");
        BaseCommandModel command6 = new BaseCommandModel(5, "生ハム");

        this.add(new BaseCommandLabel(command1)); this.add(new BaseCommandLabel(command2));
        this.add(new BaseCommandLabel(command3)); this.add(new BaseCommandLabel(command4));
        this.add(new BaseCommandLabel(command5)); this.add(new BaseCommandLabel(command6));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(300,200);
        this.setVisible(true);
    }

    public static void main(String argv[]) {
        new BaseCommandTest();
    }
}
