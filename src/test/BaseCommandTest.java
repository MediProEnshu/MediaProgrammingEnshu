package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import base.*;

/* コマンドオブジェクト */

/* main: テスト用 */
/////////////////////////////////////////////////////////
public class BaseCommandTest extends JFrame {
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
