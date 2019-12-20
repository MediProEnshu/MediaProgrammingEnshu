
public class GameManager{
	// ゲーム全体で必要な変数とかはここに保持するといいでしょう
	// 例：起動してからの時間など
	
	// モード切替用
	private ModeState _modeState = null;
	public ModeState State(){return _modeState;}

	public GameManager(STG stg)
	{
		init();
	}
	
	// 初期化用
	public void init()
	{
		// 最初はタイトルへ遷移
		_modeState = new TitleState();
	}
	
	// モード変更用
	public void ChangeMode(ModeState mode)
	{
		_modeState = mode;
	}

	// メイン処理部
	public void GameMainUpdate()
	{
		_modeState.run(this);
	}
}
