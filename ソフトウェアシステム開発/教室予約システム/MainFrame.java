package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends Frame implements ActionListener, WindowListener {
	ReservationControl	reservationControl;			//ReservationControlクラスのインスタンス生成
	//パネルインスタンスの生成
	Panel	panelNorth;			//上部パネル
	Panel	panelNorthSub1;		//上部パネルの上
	Panel	panelNorthSub2;		//上部パネルの下
	Panel	panelCenter;		//中央パネル
	Panel	panelSouth;			//下部パネル
	Panel	panelSouthSub1;		//下部パネルの上	追加（課題2）
	Panel	panelSouthSub2;		//下部パネルの下	追加（課題2）
	
	//ボタンインスタンスの生成
	Button	buttonLog;			//ログイン・ログアウトボタン
	Button	buttonExplanation;	//教室概要ボタン
	Button	buttonReservationCK;//教室予約状況確認
	Button	buttonReservation;	//新規予約画面		
	
	//ボタンインスタンス生成（追加、課題2）
	Button  buttonMyReservation; //自己予約確認ボタン
	Button	buttonMyReservationSetting; //自己予約確認Option設定ボタン
	
	//ボタンインスタンス生成（追加、課題3）
	Button	buttonCancel;		 //予約キャンセル機能
	
	//コンボボックスのインスタンス生成
	ChoiceFacility	choiceFacility;//教室選択用コンボボックス

	//テキストフィールドのインスタンス生成
	TextField	tfLoginID;		//ログインIDを表示するテキストフィールド
	
	//テキストエリアのインスタンス生成
	TextArea	textMessage;	//結果表示用メッセージ欄
	
	//MainFrameコンストラクタ	
	public	MainFrame( ReservationControl rc) {
		reservationControl = rc;
		//ボタン 生成
		buttonLog = new Button("ログイン");
		buttonExplanation = new Button("教室概要");	//追加	教室ボタンのインスタンス生成
		buttonReservationCK = new Button("教室予約状況");// 教室予約状況確認 追加（課題1）
		buttonReservation = new Button("新規予約");		//新規予約	追加（課題1）
		buttonMyReservation = new Button("自己予約確認"); //自己予約確認 追加（課題2）
		buttonMyReservationSetting = new Button("自己予約確認設定"); //自己予約確認 Option仕様実装 追加（課題2）
		buttonCancel = new Button("予約キャンセル"); //自己予約確認 Option仕様実装 追加（課題2）
		
		//追加	教室選択用コンボボックス生成
		List<String> facilityId = new ArrayList<String>();
		facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);
		
		//ログインID表示用ボックスの生成
		tfLoginID = new TextField("未ログイン", 12);
		tfLoginID.setEditable( false);
		
		//上と中央パネルを使うため、レイアウトマネージャにBoderLayoutを設定
		setLayout( new BorderLayout());
		
		//追加	上部パネルの上に「教室システム」ラベル・「ログイン」ボタンを表示
		panelNorthSub1 = new Panel();
		panelNorthSub1.add( new Label("教室予約システム "));
		panelNorthSub1.add(buttonLog);
		panelNorthSub1.add( new Label("          ログインID:"));
		panelNorthSub1.add(tfLoginID);
		
		//追加	上部パネルの下に「教室選択」、「教室概要」ボタンを表示
		panelNorthSub2 = new Panel();
		panelNorthSub2.add( new Label("教室 "));
		panelNorthSub2.add( choiceFacility);
		panelNorthSub2.add( new Label(" "));
		panelNorthSub2.add( buttonExplanation);
		
		//教室予約状況確認 追加（課題1）
		panelNorthSub2.add( buttonReservationCK);
		
		//自己予約確認	追加（課題2）
		panelNorthSub2.add( buttonMyReservation);
		
		//上部パネルに上下２つのパネルを追加・変更（課題1）
		panelNorth = new Panel( new BorderLayout());
		panelNorth.add( panelNorthSub1, BorderLayout.NORTH);
		panelNorth.add( panelNorthSub2, BorderLayout.CENTER);
		
		add( panelNorth, BorderLayout.NORTH);
		
		panelCenter = new Panel();
		textMessage = new TextArea( 20, 80);
		textMessage.setEditable( false);
		panelCenter.add( textMessage);
		
		add( panelCenter, BorderLayout.CENTER);
		
		//下部パネルの上に「自己予約確認」ボタン,「自己予約確認設定」ボタンを表示	追加（課題2）	
		panelSouthSub1 = new Panel();
		panelSouthSub1.add( buttonMyReservation);
		panelSouthSub1.add( buttonMyReservationSetting);
		
		//下部パネルの下に「新規予約」ボタンを表示	追加（課題1）	
		panelSouthSub2 = new Panel();
		panelSouthSub2.add( buttonReservation);
		
		//下部パネルの下に「予約キャンセルボタン」を表示 追加（課題3）
		panelSouthSub2.add( buttonCancel);
		
		//下部パネルに上下2つのパネルを追加・変更（課題2）
		panelSouth = new Panel( new BorderLayout());
		panelSouth.add( panelSouthSub1, BorderLayout.NORTH);
		panelSouth.add( panelSouthSub2, BorderLayout.CENTER);
		add( panelSouth, BorderLayout.SOUTH);
		
		
		buttonLog.addActionListener( this);
		//追加	ボタンアクションリスナの追加
		buttonExplanation.addActionListener( this);
		//06.08 追加
		buttonReservation.addActionListener( this);
		addWindowListener( this);
		
		//教室予約状況確認 追加（課題1）
		buttonReservationCK.addActionListener( this);
		addWindowListener( this);
		
		//自己予約確認 追加（課題2）
		buttonMyReservation.addActionListener( this);
		//自己予約確認Option設定 追加（課題2）
		buttonMyReservationSetting.addActionListener( this);
		addWindowListener( this);
		
		//予約キャンセル 追加（課題3）
		buttonCancel.addActionListener( this);
		addWindowListener( this);
	}
	
//MainFrame class(3)
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String result = new String();
		
		if ( e.getSource() == buttonLog) {
			result = reservationControl.loginLogout( this);
		}//追加
		else if( e.getSource() == buttonExplanation){
			result = reservationControl.getFacilityExplanation( choiceFacility.getSelectedItem());
		}
		else if( e.getSource() == buttonReservation){
			//追加	新規予約ボタン押下時（課題1）
			result = reservationControl.makeReservation( this);
		}
		else if( e.getSource() == buttonReservationCK){
			//追加	教室予約状況確認ボタン押下時（課題1）
			result = reservationControl.getFacility_Reservation( this);
		}
		else if( e.getSource() == buttonMyReservation){
			//追加	自己予約確認ボタン押下時（課題2）
			result = reservationControl.MyReservation( this);
		}
		else if( e.getSource() == buttonMyReservationSetting){
			//追加	自己予約確認設定ボタン押下時（課題2）
			result = reservationControl.MyCheckSetting( this);
		}
		else if( e.getSource() == buttonCancel){
			//追加	予約キャンセルボタン押下時（課題3）
			result = reservationControl.ReservationCancel( this);
		}
		textMessage.setText( result);
	}
	
	
	
}
