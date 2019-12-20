import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class MainGameState implements ModeState{

	//private Fighter	_fighter;
	//public Fighter GetFighter(){return _fighter;}
	
	// �X�e�[�W�f�[�^�ǂݍ��ݗp
	//private StageAnalyze _analyze;
	//public StageAnalyze GetStage(){return _analyze;}

	// �G�L�����̊Ǘ��p
	//private EnemyManager _emanager;

	//Exit(Result)��ʂ֑J�ڂ��邩�����߂�ϐ�
	private boolean is_exit = false;

	//�L�[�t���O
	private boolean m_bKeyEnter;
	public void KeyEnter(boolean on){
		m_bKeyEnter = on;
	}
	
	public MainGameState()
	{
		init();
	}
	
	// �������p
	public void init()
	{	
/*
		// �X�e�[�W�f�[�^����[
		// �X�e�[�W�f�[�^�ǂݍ��݂́A�X�e�[�W��state�p�^�[���Ŏ������Ă��̒��ł���Ă�����
		_analyze	= new StageAnalyze();
		_analyze.Analyze("stage1.txt");
*/

	}

	@Override
	public void Show(Graphics2D g2) {
		// TODO Auto-generated method stub
		//��ʂɕ\�����������̂͂����ɏ���
	}

	@Override
	public void run(GameManager gm) {		
		//Enter�������ꂽ��Exit��ʂ֑J�ڂ���
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
