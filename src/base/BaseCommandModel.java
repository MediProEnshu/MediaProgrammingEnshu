package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* コマンドオブジェクト */

/* Model: コマンド文を保持 */
/////////////////////////////////////////////////////////
public class BaseCommandModel {
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