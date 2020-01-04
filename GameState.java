class GameState {//ゲームの全体の状態を統括。大体目に見えない部分を処理
    private boolean moveFlag;//カーソルの判定が移動になってるか
    private boolean summonFlag;//カーソルの判定が召喚になってるか
    private boolean battleFlag;//カーソルの判定がこうげきになってるか。//つまるとこ画面左のどのボタンが押されたか
    private int player1Mana;//プレイヤー1の残りマナ
    private int player2Mana;//プレイヤー2の残りマナ
    private int nowPlayer;//今操作を行えるプレイヤーを表す
    private BaseCharacter nowSummon;//今召喚カーソルをクリックして召喚できるキャラクターを表す

    public GameState() {//コンストラクタ初期マナと初期プレイヤーとかを設定
        moveFlag = false;
        summonFlag = false;
        battleFlag = false;
        player1Mana = 5;
        player2Mana = 5;
        nowPlayer = 1;
    }
    public void setMove() {
        moveFlag = true;
        summonFlag = false;
        battleFlag = false;
    }
    public void setSummon() {
        moveFlag = false;
        summonFlag = true;
        battleFlag = false;
    }
    public void setBattle() {
        moveFlag = false;
        summonFlag = false;
        battleFlag = true;
    }
    public boolean getMoveFlag() {
        return moveFlag;
    }
    public boolean getSummonFlag() {
        return summonFlag;
    }
    public boolean getBattleFlag() {
        return battleFlag;
    }
    public void setNowPlayer(int player) {
        nowPlayer = player;
    }
    public int getNowPlayer() {
        return nowPlayer;
    }
    public void setPlayer1Mana(int cost) {//マナの加減算処理(プレイヤー１)
        player1Mana -= cost;
        if(player1Mana < 0) {
            player1Mana = 0;
        }
    }
    public void setPlayer2Mana(int cost) {//マナの加減算処理(プレイヤー2)
        player2Mana -= cost;
        if(player2Mana < 0) {
            player2Mana = 0;
        }
    }
    public int getPlayer1Mana() {//今のマナを取得
        return player1Mana;
    }
    public int getPlayer2Mana() {//同様
        return player2Mana;
    }
    public void setNowSummon(BaseCharacter character) {//召喚ボタンを押してキャラクターをクリックした後に処理
        nowSummon = character;
    }
    public BaseCharacter getNowSummon() {//今召喚するキャラを取得
        return nowSummon;
    }
}
