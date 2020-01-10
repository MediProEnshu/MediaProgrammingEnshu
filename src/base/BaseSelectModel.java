package base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.System;

/* セレクトオブジェクト */

/* Model: BaseCommandの集合とカーソルの位置を保持, コマンドで動かしたいオブジェクトに監視させる. */
/////////////////////////////////////////////////////////
public class BaseSelectModel extends Observable {
    private ArrayList<BaseCommandModel> commands;
    private int selectPosition, maxPosition;
    private boolean isDetermined;

    public BaseSelectModel(String[] cmdTexts) {
        isDetermined = false;
        BaseCommandModel cmd;
        maxPosition = 0;
        commands = new ArrayList<BaseCommandModel>();
        for(String s : cmdTexts) {
            cmd = new BaseCommandModel(maxPosition, s);
            commands.add(cmd);
            maxPosition++;
        }
    }

    public ArrayList<BaseCommandModel> getCommands() {
        return commands;
    }

    public int getCommandSize() {
        return maxPosition;
    }

    public int getCurrentPosition() {
        while(selectPosition < 0) { selectPosition += maxPosition; }
        return selectPosition % maxPosition;
    }

    public boolean getIsDetermined() {
        return isDetermined;
    }

    /* 監視から逃れる */
    public void fleezeSelect() {
        this.deleteObservers();
    }

    public void determined() {
        isDetermined = true;
        setChanged();
        notifyObservers();
    }

    public void upped() {
        selectPosition--; //上の選択肢ほど小さい値であることに注意.
        setChanged();
        notifyObservers();
    }

    public void downed() {
        selectPosition++; //上の選択肢ほど小さい値であることに注意.
        setChanged();
        notifyObservers();
    }
}