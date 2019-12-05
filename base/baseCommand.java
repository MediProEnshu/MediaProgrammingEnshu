import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* コマンドオブジェクト */

/* Model: コマンド文を保持 */
/////////////////////////////////////////////////////////
class BaseCommandModel {
  private String command;

  public BaseCommandModel(String text) {
    command = text;
  }

  public String get(){
    return this.command;
  }
}


/* View: コマンドラベルを表示 */
/////////////////////////////////////////////////////////
class BaseCommandView extends JLabel {
  public BaseCommandView(BaseCommandModel model) {
    this.setText(model.get());
  }
}


/* main: テスト用 */
/////////////////////////////////////////////////////////
class BaseCommand extends JFrame {
  public BaseCommand() {
    this.setTitle("BaseSelect テスト");
    this.setLayout(new GridLayout(6, 1));
    /* コマンド登録 */
    BaseCommandModel command1 = new BaseCommandModel("チーカマ");
    BaseCommandModel command2 = new BaseCommandModel("味噌田楽");
    BaseCommandModel command3 = new BaseCommandModel("しぐれ煮");
    BaseCommandModel command4 = new BaseCommandModel("煮っころがし");
    BaseCommandModel command5 = new BaseCommandModel("もつ煮");
    BaseCommandModel command6 = new BaseCommandModel("生ハム");

    this.add(new BaseCommandView(command1)); this.add(new BaseCommandView(command2));
    this.add(new BaseCommandView(command3)); this.add(new BaseCommandView(command4));
    this.add(new BaseCommandView(command5)); this.add(new BaseCommandView(command6));

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.pack();
    this.setSize(300,200);
    this.setVisible(true);
  }

  public static void main(String argv[]) {
    new BaseCommand();
  }
}