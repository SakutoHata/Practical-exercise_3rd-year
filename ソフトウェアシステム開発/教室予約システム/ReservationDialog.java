package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDialog extends Dialog implements ActionListener, WindowListener, ItemListener{
	boolean canceled;						//新規予約キャンセルステータス
	ReservationControl rc;					//ReservationControlインスタンス
	
	//パネル
	Panel	panelNorth;
	Panel	panelCenter;
	Panel	panelSouth;
	
	//入力用コンポーネント
	ChoiceFacility	choiceFacility;
	TextField		tfYear, tfMonth, tfDay;
	ChoiceHour		startHour;
	ChoiceMinute	startMinute;
	ChoiceHour		endHour;
	ChoiceMinute	endMinute;
	
	//ボタン
	Button			buttonOK;
	Button			buttonCancel;
	
	//コンストラクタ
	public	ReservationDialog( Frame owner, ReservationControl rc) {
		//基礎クラスのコンストラクタ呼び出し
		super( owner, "新規予約", true);
		
		this.rc = rc;
		
		//初期値キャンセルを設定
		canceled = true;
		
		//教室選択ボックス
		List<String> facilityId = new ArrayList<String>();
		facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);
		
		//テキストフィールドの生成（年月日）
		tfYear			= new TextField("",4);
		tfMonth			= new TextField("",2);
		tfDay			= new TextField("",2);
		
		//開始時刻（時分）選択ボックス生成
		startHour		= new ChoiceHour();
		startMinute		= new ChoiceMinute();
		//終了時刻（時分）選択ボックス生成
		endHour			= new ChoiceHour();
		endMinute		= new ChoiceMinute();
		
		//ボタン生成
		buttonOK		= new Button("予約実行");
		buttonCancel	= new Button("キャンセル");
		
		//パネル生成
		panelNorth		= new Panel();
		panelCenter		= new Panel();
		panelSouth		= new Panel();
		
		//上部パネルに教室選択ボックス、年月日入力欄を設置
		panelNorth.add( new Label("教室"));
		panelNorth.add( choiceFacility);
		panelNorth.add( new Label("予約日"));
		panelNorth.add( tfYear);
		panelNorth.add( new Label("年"));
		panelNorth.add( tfMonth);
		panelNorth.add( new Label("月"));
		panelNorth.add( tfDay);
		panelNorth.add( new Label("日"));
		
		//中央パネルに予約開始時刻、終了時刻入力用選択ボックスを追加
		panelCenter.add( new Label("予約時間"));
		panelCenter.add( startHour);
		panelCenter.add( new Label("時"));
		panelCenter.add( startMinute);
		panelCenter.add( new Label("分～"));
		panelCenter.add( endHour);
		panelCenter.add( new Label("時"));
		panelCenter.add( endMinute);
		panelCenter.add( new Label("分"));
		
		//下部パネルに２つのボタンを追加
		panelSouth.add( buttonCancel);
		panelSouth.add( new Label("  "));
		panelSouth.add( buttonOK);
		
		//ReservationDialogをBoderLayoutに設定、3つのパネル追加
		setLayout( new BorderLayout());
		add( panelNorth, BorderLayout.NORTH);
		add( panelCenter, BorderLayout.CENTER);
		add( panelSouth, BorderLayout.SOUTH);
		
		//WindowListener追加
		addWindowListener( this);
		
		//ボタンにアクションリスナ追加
		buttonOK.addActionListener( this);
		buttonCancel.addActionListener( this);
		
		//教室選択ボックス、時・分選択ボックスそれぞれにListener追加
		choiceFacility.addItemListener( this);
		startHour.addItemListener( this);
		endHour.addItemListener( this);
		
		//選択された教室の利用可能時間の範囲を設定
		resetTimeRange( choiceFacility.getSelectedItem());
		
		//大きさ、Windowサイズ変更不可を設定
		this.setBounds(100, 100, 500, 150);
		setResizable( false);
	}

	//開始時刻と終了時刻を初期化
	private void resetTimeRange( String facility) {
		int[]	availableTime;
		//指定教室の利用可能開始時刻、終了時刻を取得
		availableTime = rc.getAvailableTime( facility);
		startHour.resetRange( availableTime[0], availableTime[1]);
		endHour.resetRange( availableTime[0], availableTime[1]);
	}
	
	//コンボボックスで選択している情報が変化
	@Override
	public void itemStateChanged( ItemEvent e) {
		//選択教室が変わった時
		if( e.getSource() == choiceFacility) {
			String	startTime = startHour.getSelectedItem();
			String	endTime	  = endHour.getSelectedItem();
			resetTimeRange( choiceFacility.getSelectedItem());
			if( Integer.parseInt(startTime) < Integer.parseInt(startHour.getFirst())) {
				startTime = startHour.getFirst();
			}
			if( Integer.parseInt(endTime) < Integer.parseInt(endHour.getFirst())) {
				endTime = endHour.getFirst();
			}
			startHour.select( startTime);
			endHour.select( endTime);
			//利用開始時刻が変わった時
		}	else if( e.getSource() == startHour) {
			//開始時刻が変更→終了時刻を開始時刻と同じに
			int	start = Integer.parseInt( startHour.getSelectedItem());
			String	endTime = endHour.getSelectedItem();
			endHour.resetRange( start, Integer.parseInt( endHour.getLast()));
			if( Integer.parseInt( endTime) >= start) {
				endHour.select( endTime);
			}
			//利用終了時刻が変化したとき
		}	else if( e.getSource() == endHour) {
			//終了時刻が変更→開始時刻を終了時刻と同じに
			int	end = Integer.parseInt( endHour.getSelectedItem());
			String	startTime = startHour.getSelectedItem();
			startHour.resetRange( Integer.parseInt( startHour.getFirst()), end);
			if( Integer.parseInt( startTime) <= end) {
				startHour.select( startTime);
			}
				startHour.select( startTime);
			}
	}
	
	@Override
	public void windowOpened(WindowEvent e){
		
	}
	@Override
	public void windowClosing(WindowEvent e){
		setVisible(false);
		dispose();
		
	}
	@Override
	public void windowClosed(WindowEvent e){
		
	}
	@Override
	public void windowIconified(WindowEvent e){
		
	}
	@Override
	public void windowDeiconified(WindowEvent e){
		
	}
	@Override
	public void windowActivated(WindowEvent e){
		
	}
	@Override
	public void windowDeactivated(WindowEvent e){
		
	}
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==buttonCancel) {
			setVisible(false);
			dispose();
		}else if(e.getSource()==buttonOK) {
			canceled=false;
			setVisible(false);
			dispose();
		}
	}
}
