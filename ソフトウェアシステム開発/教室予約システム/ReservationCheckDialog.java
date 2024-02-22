package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


//ReservationDialog をクローニングした 追加（6.13)
public class ReservationCheckDialog  extends Dialog implements ActionListener, WindowListener{
		boolean canceled;
		ReservationControl rc;
		
		//パネル
		Panel	panelNorth;
		Panel	panelSouth;
		
		//入力用コンポーネント
		ChoiceFacility	choiceFacility;
		TextField		tfYear, tfMonth, tfDay;
		
		//ボタン
		Button			buttonOK;
		Button			buttonCancel;
		
		//コンストラクタ
		public	ReservationCheckDialog( Frame owner, ReservationControl rc) {
			//基底クラスのコンストラクタを呼び出す
			super( owner, "教室予約状況", true);
			
			this.rc = rc;
			
			//初期値キャンセル
			canceled = true;
			
			//教室選択ボックスの生成
			List<String> facilityId = new ArrayList<String>();
			facilityId = rc.getFacilityId();
			choiceFacility = new ChoiceFacility(facilityId);
			
			//テキストフィールドの生成（年月日）
			tfYear			= new TextField("",4);
			tfMonth			= new TextField("",2);
			tfDay			= new TextField("",2);

			//ボタン生成
			buttonOK		= new Button("予約状況確認");
			buttonCancel	= new Button("キャンセル");
			
			//パネル生成
			panelNorth		= new Panel();
			panelSouth		= new Panel();
			
			//上部パネル生成
			panelNorth.add( new Label("教室"));
			panelNorth.add( choiceFacility);
			panelNorth.add( new Label("予約日"));
			panelNorth.add( tfYear);
			panelNorth.add( new Label("年"));
			panelNorth.add( tfMonth);
			panelNorth.add( new Label("月"));
			panelNorth.add( tfDay);
			panelNorth.add( new Label("日"));
			
			
			//下部パネル生成
			panelSouth.add( buttonCancel);
			panelSouth.add( new Label("  "));
			panelSouth.add( buttonOK);
			
			//ReservationCheckDialogをBoderLayoutに設定、2つのパネル生成
			setLayout( new BorderLayout());
			add( panelNorth, BorderLayout.NORTH);
			add( panelSouth, BorderLayout.SOUTH);
			
			//WindowListener追加
			addWindowListener( this);
			
			//ボタンにアクションリスナ追加
			buttonOK.addActionListener( this);
			buttonCancel.addActionListener( this);

			//大きさ、Windowサイズ変更不可の設定
			this.setBounds(100, 100, 500, 150);
			setResizable( false);
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
			if(e.getSource()==buttonCancel) {		//キャンセルボタン押下時
				setVisible(false);					//	Window非表示
				dispose();							//	Windowのグラフィックリソースを開放
			}else if(e.getSource()==buttonOK) {		//教室予約状況ボタン押下時
				canceled=false;						//	操作キャンセル
				setVisible(false);					//	Window非表示
				dispose();							//	Windowのグラフィックリソースを開放
			}
		}
}
