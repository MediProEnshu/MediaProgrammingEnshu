package game;

import java.util.*;

public class StageEditModel extends Observable {
    private boolean quitPushed;

    public StageEditModel() {
       this.quitPushed = false;
    }

    public void toBack() {
        this.quitPushed = true;
        setChanged();
        notifyObservers();
    }

    public boolean isQuit() {
        return quitPushed;
    }
}