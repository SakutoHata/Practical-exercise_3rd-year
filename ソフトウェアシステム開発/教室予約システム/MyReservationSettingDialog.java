package client_system;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

public class MyReservationSettingDialog extends Dialog implements ActionListener,WindowListener{
		// 自己予約確認Option設定ダイアログ	追加（課題２）
		boolean canceled;		//キャンセルフラグ
		boolean ResetSetting;	//初期化フラグ
		ReservationControl rc;
		
		//パネル
		Panel	panelNorth;
		Panel	panelNorthSub1;
		Panel	panelNorthSub2;
		Panel	panelSouth;
		
		//入力用コンポーネント
		ChoiceFacility	choiceFacility;
		TextField		tfYear_start, tfMonth_start, tfDay_start,tfYear_end, tfMonth_end, tfDay_end;
		
		//ボタン
		Button			buttonOK;
		Button			buttonCancel;
		Button			buttonReset;
		
		//コンストラクタ
		public	MyReservationSettingDialog( Frame owner, ReservationControl rc) {
			
			//基底クラスのコンストラクタを呼び出す
			super( owner, "自己予約確認Opiton設定", true);
			
			this.rc = rc;
			
			//初期値キャンセル
			canceled = true;
			
			ResetSetting = false;
			
			//教室選択ボックスの生成
			List<String> facilityId = new ArrayList<String>();
			facilityId = rc.getFacilityId();
			choiceFacility = new ChoiceFacility(facilityId);
			
			//テキストフィールドの生成（年月日）
			tfYear_start		= new TextField("",4);
			tfMonth_start		= new TextField("",2);
			tfDay_start			= new TextField("",2);
			tfYear_end			= new TextField("",4);
			tfMonth_end			= new TextField("",2);
			tfDay_end			= new TextField("",2);

			//ボタン生成
			buttonOK		= new Button("設定完了");
			buttonCancel	= new Button("キャンセル");
			buttonReset	= new Button("初期化");
			
			//パネル生成
			panelNorth		= new Panel();
			panelNorthSub1  = new Panel();
			panelNorthSub2  = new Panel();
			panelSouth		= new Panel();
			
			//上部パネル生成
			panelNorthSub1.add( new Label("教室"));
			panelNorthSub1.add( choiceFacility);
			panelNorthSub1.add( new Label("指定期間"));
			panelNorthSub1.add( new Label(" 開始日"));
			panelNorthSub1.add( tfYear_start);
			panelNorthSub1.add( new Label("年"));
			panelNorthSub1.add( tfMonth_start);
			panelNorthSub1.add( new Label("月"));
			panelNorthSub1.add( tfDay_start);
			panelNorthSub1.add( new Label("日"));
			panelNorthSub2.add( new Label("～"));
			panelNorthSub2.add( new Label(" 終了日"));
			panelNorthSub2.add( tfYear_end);
			panelNorthSub2.add( new Label("年"));
			panelNorthSub2.add( tfMonth_end);
			panelNorthSub2.add( new Label("月"));
			panelNorthSub2.add( tfDay_end);
			panelNorthSub2.add( new Label("日"));
			
			//上部パネルに上下２つのパネルを追加・変更（課題1）
			panelNorth = new Panel( new BorderLayout());
			panelNorth.add( panelNorthSub1, BorderLayout.NORTH);
			panelNorth.add( panelNorthSub2, BorderLayout.CENTER);
			
			//下部パネル生成
			panelSouth.add( buttonCancel);
			panelSouth.add( new Label("  "));
			panelSouth.add( buttonReset);
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
			buttonReset.addActionListener( this);

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
			}else if(e.getSource()==buttonOK) {		//設定完了ボタン押下時
				canceled=false;						//	操作キャンセル
				setVisible(false);					//	Window非表示
				dispose();							//	Windowのグラフィックリソースを開放
			}else if(e.getSource()==buttonReset) {		//初期化ボタン押下時	追加（課題2）
				ResetSetting = true;				//  設定初期化フラグ成立
				canceled=false;						//	操作キャンセル
				setVisible(false);					//	Window非表示
				dispose();							//	Windowのグラフィックリソースを開放
			}
	   }
}
