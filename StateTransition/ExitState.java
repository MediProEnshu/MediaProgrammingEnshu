import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


// �I�����̃��[�h
// �I�����Ȃ��Ń^�C�g���߂��Ă�����
public class ExitState implements ModeState{

	private final static int REPLAY	= 0;
	private final static int END		= 1;
	private int _cursorPos = REPLAY;

	// ���C���^�C�g���̈ʒu
	private final static int TITLEPOSX	= 50;
	private final static int TITLEPOSY	= 150;

	// ���C�����j���[�\���ʒu�B�\���Ԋu�B�J�[�\���ʒu�ix���W�̂݁j
	private final static int MENUPOSX		= 200;
	private final static int MENUPOSY		= 280;
	private final static int MENUINTERVAL	= 50;
	private final static int CURSOR		= 150;

	// �L�[�t���O
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
	
	// �L�[�ړ��B����Ƃ�
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

		// Enter�������Ƃ�
		if(m_bKeyEnter)
		{
			// �J�[�\���ʒu�ŕ���
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

		// ��т̕\��
		g2.setPaint(Color.black);
		g2.drawString("���Ȃ��̐��",TITLEPOSX,TITLEPOSY);

		// ���v���C
		if(_cursorPos == REPLAY)
			g2.setPaint(Color.blue);
		else
			g2.setPaint(Color.black);
		
		g2.drawString("�������v���C����",MENUPOSX,MENUPOSY);

		// �Q�[���I��
		if(_cursorPos == END)
			g2.setPaint(Color.blue);
		else
			g2.setPaint(Color.black);

		g2.drawString("�Q�[���I��",MENUPOSX,MENUPOSY + MENUINTERVAL);

		// ���[����
		g2.setPaint(Color.blue);
		switch(_cursorPos)
		{
			case REPLAY:
			g2.drawString("��",CURSOR,MENUPOSY);
				break;
			case END:
			g2.drawString("��",CURSOR,MENUPOSY + MENUINTERVAL);
				break;
		}

		// ����\��
		g2.setPaint(Color.black);
		g2.setFont(new Font("�l�r �S�V�b�N", Font.BOLD, 20));
		g2.drawString("�����L�[�ŃJ�[�\���ړ��BEnter�L�[�Ō���B", 50,600);
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