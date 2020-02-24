package game;

import java.util.*;

public class GameEscape extends Observable {
    /* ゲーム画面からの遷移の処理用定数 */
    public static final int GAME_RESULT = 0;
    public static final int GAME_START = 1;

    /* ゲーム画面からの遷移の処理用変数 */
    private int game_state;

    public GameEscape() {
        game_state = -1;
    }

    public void toResult() {
        game_state = GAME_RESULT;
        setChanged();
        notifyObservers();
    }

    public void toStart() {
        game_state = GAME_START;
        setChanged();
        notifyObservers();
    }

    public int getGameState() {
        return game_state;
    }


}
