import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


// ここはMVCでいえばVとCにあたる

// MVCの観点で組むとすると
// Mはゲーム全体の状態とそれに対する処理を管理するGameManagerクラス
// VはJPanelでの表示。GameManagerの状態に応じて処理をする
// どうせキー入力なんでCもJPanelにKeyListenerを入れて頑張ってもらう
// GameManagerには状態を持たせ、それを切り替えさせる
// stateパターンにまとめれば入力系もすっきり

public class STG extends JPanel implements Runnable, KeyListener{

	public static Thread mainThread = null;
	private Window w;
	// メイン関数
	public static void main(String args[])
	{
		// 適当なJフレームを用意
		JFrame frame = new JFrame();

		// メインパネル（シューティングを実行するパネル）を新規作成
		STG app = new STG();
		// フレームに登録
		frame.getContentPane().add(app);
		// 各種フレームの設定
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ウィンドウスタイル
		frame.setBounds(10, 10, 640, 720); // ウィンドウサイズ デフォで480:640, 変更時は4箇所変えること
		frame.setTitle("†陣地防衛ゲーム†"); // タイトル
		frame.setVisible(true); // 見えるようにしないとね

		// メインスレッド化
		mainThread = new Thread(app);
		
		// 設定終わったのでメインパネル初期化して開始
		app.init();
	}
	
	// ゲームマネージャの保持
	private GameManager _gmanager;

	// 描画対象バッファ
	private Image buffer;
	private Graphics bufferGraphics;
	
	public void init(){
		setBackground(Color.black);
		setForeground(Color.white);

		if (buffer == null){
			buffer = createImage(640, 720);
			bufferGraphics = buffer.getGraphics();
		}

		addKeyListener(this);
		requestFocus();
		
		_gmanager = new GameManager(this);
		
	    mainThread.start();
	}


	// スレッドで動く関数かもしれない
	public void run(){
		while (true){
			try{
				Thread.sleep(20);	// FPS調整・・・でも処理落ちしてるからあんま関係ねえっぽ
			}catch (InterruptedException e){
				break;
			}

			Graphics2D g2 = (Graphics2D) bufferGraphics;	// 2D使うため

			g2.setBackground(Color.white);
			g2.clearRect(0, 0, 640, 720);

			// アンチエイリアシング
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(4.0f));

			// ゲーム内処理
			_gmanager.GameMainUpdate();	
			
			// この辺で描画
			ShowObjects(g2);
			
			// ﾘﾍﾟｲﾝﾄ
			repaint();
		}
	}

	// 描画命令
	public void ShowObjects(Graphics2D g2)
	{
		_gmanager.State().Show(g2);
	}
	
	// 再描画命令の際にはこれを張りなおす
	public void paintComponent(Graphics g){
			g.setColor(Color.black);
			g.clearRect(0, 0, 640, 720);
			g.drawImage(buffer, 0, 0, this);
	}

	// 入力系。状態により切り替える
	public void keyPressed(KeyEvent e){
		_gmanager.State().KeyPressed(e);
	}

	public void keyReleased(KeyEvent e){
		_gmanager.State().KeyReleased(e);
	}
	public void keyTyped(KeyEvent e)
	{
		_gmanager.State().KeyTyped(e);
	}

}
