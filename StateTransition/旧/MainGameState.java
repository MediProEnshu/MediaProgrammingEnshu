import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class MainGameState implements ModeState{

	//private Fighter	_fighter;
	//public Fighter GetFighter(){return _fighter;}
	
	// ステージデータ読み込み用
	//private StageAnalyze _analyze;
	//public StageAnalyze GetStage(){return _analyze;}

	// 敵キャラの管理用
	//private EnemyManager _emanager;

	//Exit(Result)画面へ遷移するかを決める変数
	private boolean is_exit = false;

	//キーフラグ
	private boolean m_bKeyEnter;
	public void KeyEnter(boolean on){
		m_bKeyEnter = on;
	}
	
	public MainGameState()
	{
		init();
	}
	
	// 初期化用
	public void init()
	{	
/*
		// ステージデータだよー
		// ステージデータ読み込みは、ステージをstateパターンで実装してその中でやってもいい
		_analyze	= new StageAnalyze();
		_analyze.Analyze("stage1.txt");
*/

	}

	@Override
	public void Show(Graphics2D g2) {
		// TODO Auto-generated method stub
		//画面に表示したいものはここに書く
	}

	@Override
	public void run(GameManager gm) {		
		//Enterが押されたらExit画面へ遷移する
		if(m_bKeyEnter == true){
			is_exit = true;
		}

		if(is_exit == true){
			gm.ChangeMode(new ExitState());
		}
	}
	
	@Override
	public void KeyPressed(KeyEvent ev) {
		// TODO Auto-generated method stub
		
		switch(ev.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			KeyEnter(true);
			break;
		}
	}

	@Override
	public void KeyReleased(KeyEvent ev) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void KeyTyped(KeyEvent ev) {
		// TODO Auto-generated method stub
	}
}
