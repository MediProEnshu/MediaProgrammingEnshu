import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


// 終了時のモード
// 終了しないでタイトル戻ってもいい
public class ExitState implements ModeState{

	private final static int REPLAY	= 0;
	private final static int END		= 1;
	private int _cursorPos = REPLAY;

	// メインタイトルの位置
	private final static int TITLEPOSX	= 50;
	private final static int TITLEPOSY	= 150;

	// メインメニュー表示位置。表示間隔。カーソル位置（x座標のみ）
	private final static int MENUPOSX		= 200;
	private final static int MENUPOSY		= 280;
	private final static int MENUINTERVAL	= 50;
	private final static int CURSOR		= 150;

	// キーフラグ
	private boolean m_bKeyUp;
	public void KeyUp(boolean on){
		m_bKeyUp = on;
	}
	private boolean m_bKeyDown;
	public void KeyDown(boolean on){
		m_bKeyDown = on;
	}
	private boolean m_bKeyEnter;
	public void KeyEnter(boolean on){
		m_bKeyEnter = on;
	}

	public ExitState(){
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}
	
	// キー移動。決定とか
	public void run(GameManager gm)
	{
		if(m_bKeyUp)
		{
			if(_cursorPos != REPLAY)
				_cursorPos--;
		}
		else if(m_bKeyDown)
		{
			if(_cursorPos != END)
				_cursorPos++;
		}

		// Enter押したとき
		if(m_bKeyEnter)
		{
			// カーソル位置で分岐
			switch(_cursorPos)
			{
				case REPLAY:
					gm.ChangeMode(new MainGameState());
					break;
				case END:
					//gm.ChangeMode(new ExitState());
					System.exit(0);
					break;
			}
		}
	}

	@Override
	public void Show(Graphics2D g2) {
		g2.setFont(new Font("Arial", Font.BOLD, 28));

		// 戦績の表示
		g2.setPaint(Color.black);
		g2.drawString("あなたの戦績",TITLEPOSX,TITLEPOSY);

		// リプレイ
		if(_cursorPos == REPLAY)
			g2.setPaint(Color.blue);
		else
			g2.setPaint(Color.black);
		
		g2.drawString("もう一回プレイする",MENUPOSX,MENUPOSY);

		// ゲーム終了
		if(_cursorPos == END)
			g2.setPaint(Color.blue);
		else
			g2.setPaint(Color.black);

		g2.drawString("ゲーム終了",MENUPOSX,MENUPOSY + MENUINTERVAL);

		// かーそる
		g2.setPaint(Color.blue);
		switch(_cursorPos)
		{
			case REPLAY:
			g2.drawString("→",CURSOR,MENUPOSY);
				break;
			case END:
			g2.drawString("→",CURSOR,MENUPOSY + MENUINTERVAL);
				break;
		}

		// 操作表示
		g2.setPaint(Color.black);
		g2.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 20));
		g2.drawString("↑↓キーでカーソル移動。Enterキーで決定。", 50,600);
	}
	
	@Override
	public void KeyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			KeyEnter(true);
			break;
		case KeyEvent.VK_UP:
			KeyUp(true);
			break;
		case KeyEvent.VK_DOWN:
			KeyDown(true);
			break;
		}
	}

	@Override
	public void KeyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getKeyCode())
		{
		case KeyEvent.VK_ENTER:
			KeyEnter(false);
			break;
		case KeyEvent.VK_UP:
			KeyUp(false);
			break;
		case KeyEvent.VK_DOWN:
			KeyDown(false);
			break;
		}
	}
		
	@Override
	public void KeyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
