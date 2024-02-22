package client_system;

import java.awt.*;
import java.awt.event.*;

public class LoginDialog extends Dialog implements ActionListener,WindowListener {
	boolean canceled;						//キャンセル：true, OK:false
	
	TextField tfUserID;						//ユーザID入力用テキストフィールド
	TextField tfPassword;					//パスワード入力用テキストフィールド
	
    Button buttonOK;						//OKボタンインスタンス
    Button buttonCancel;					//キャンセルボタンインスタンス
    
    Panel panelNorth;						//上部パネル
    Panel panelCenter;						//中央パネル
    Panel panelSouth;						//下部パネル
    
    //LoginDialogクラスコンストラクタ
    public LoginDialog(Frame arg0) {
    	super( arg0,"Login", true);			//引数は、Dialog所有者、タイトル、モーダル指定
    	canceled = true;					//キャンセル状態
    	
    	//テキストフィールド生成
    	tfUserID = new TextField("", 10);
    	tfPassword = new TextField("", 10);
    	tfPassword.setEchoChar('*');
    	//ボタン生成
    	buttonOK = new Button("OK");
    	buttonCancel = new Button("キャンセル");
    	//パネル生成
    	panelNorth = new Panel();
    	panelCenter = new Panel();
    	panelSouth = new Panel();
    	
    	//上部パネルにユーザIDテキストフィールドを配置
    	panelNorth.add(new Label("ユーザID"));
    	panelNorth.add(tfUserID);
    	
    	//中央パネルにユーザIDテキストフィールドを配置
    	panelCenter.add(new Label("パスワード"));
    	panelCenter.add(tfPassword);
    	
    	//下部パネルにユーザIDテキストフィールドを配置
    	panelSouth.add(buttonCancel);
    	panelSouth.add(buttonOK);
    	
    	//LoginDialogをBoderLayoutに設定、3つのパネル追加
    	setLayout(new BorderLayout());
    	add( panelNorth, BorderLayout.NORTH);
    	add( panelCenter, BorderLayout.CENTER);
    	add( panelSouth, BorderLayout.SOUTH);
    	
    	//WindowListenerを追加
    	addWindowListener( this);
    	
    	buttonOK.addActionListener( this);
    	buttonCancel.addActionListener( this);
    }
    
    @Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		setVisible(false);
		canceled = true;
		dispose();
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonCancel) {
			canceled = true;
		}else if(e.getSource() == buttonOK) {
			canceled = false;
		}
		setVisible( false);
		dispose();
	}    
}
