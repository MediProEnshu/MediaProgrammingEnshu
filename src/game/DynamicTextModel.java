package game;

import java.util.*;

public class DynamicTextModel extends Observable{
    /* 最初と最後の境目を表現するため、1つダミーを用意 */
    protected final int MEMORY_SIZE = 6;
    protected final String EMPTY_STR = "here_is_empty.";
    protected final String DUMMY_STR = "here_is_dummy."; // 最初だけ使う
    protected String texts[] = new String[MEMORY_SIZE]; // リンクバッファとして使う
    /* 表示している場所のindex, ダミーのindex, 最後に記録された場所のindex */
    protected int index, dum_index, cur_index;

    public DynamicTextModel(String str) {
        //Arrays.fill(texts, EMPTY_STR);
        index = 0; dum_index = 0;
        texts[index] = DUMMY_STR;
        texts[++index] = str;
        cur_index = index;
    }

    protected int previousIndex(int i) {
        return --i >= 0 ? i : MEMORY_SIZE-1;
    }

    protected int nextIndex(int i) {
        return ++i < MEMORY_SIZE ? i : 0;
    }

    public String getText() {
        return texts[index];
    }

    /* テキストを加える */
    public void changeText(String str) {
        cur_index = this.nextIndex(cur_index);
        index = cur_index; // indexを最新にする

        if(index == dum_index) { // ダミーなら現在地の次を空にする
            dum_index = this.nextIndex(index);
        }
        texts[index] = str; // 文字列代入

        setChanged();
        notifyObservers();
    }

    /* 1つ前のテキストを表示 */
    public void toPrevious() {
        if(this.isFirstNow()) { return; } // これ以上戻れない

        index = this.previousIndex(index);

        setChanged();
        notifyObservers();
    }

    /* 1つ後のテキストを表示 */
    public void toNext() {
        if(this.isLastNow()) { return; } // これ以上進めない

        index = this.nextIndex(index);

        setChanged();
        notifyObservers();
    }

    /* cur_indexに移動 */
    public void toCurrent() {
        index = cur_index;

        setChanged();
        notifyObservers();
    }

    /* 現在指しているindexは最初のindex? */
    public boolean isFirstNow() {
        return this.previousIndex(index) == dum_index;
    }

    /* 現在指しているindexは最後のindex? */
    public boolean isLastNow() {
        return this.nextIndex(index) == dum_index ||
               texts[ this.nextIndex(index) ] == EMPTY_STR;
    }
}