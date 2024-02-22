package client_system;

import	java.sql.*;
import	java.awt.*;
import	java.util.List;
import	java.util.ArrayList;
import 	java.text.DateFormat;
import  java.text.ParseException;
import  java.text.SimpleDateFormat;
import  java.util.Calendar;
import java.util.Collections; //6.19 追加

public class ReservationControl {
	Connection  sqlCon;
	Statement   sqlStmt;
	String		sqlUserID = "puser";
	String		sqlPassword = "1234";
	//予約システム内のユーザID、Login状態
	String		reservationUserID;
	
	String Set_date1;		//自己予約確認設定開始日記憶用	追加(課題2)
	String Set_date2;		//自己予約確認設定終了日記憶用	追加（課題2）
	String facility_id;		//自己予約確認設定教室記憶用	追加（課題2）
	
	private boolean flagLogin;
	
	ReservationControl(){
		flagLogin = false;
	}
	
	private void connectDB() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			//MySQLに接続
			String url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
			sqlCon = DriverManager.getConnection( url, sqlUserID, sqlPassword);
			sqlStmt = sqlCon.createStatement();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void closeDB() {
		try {
			sqlStmt.close();
			sqlCon.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String loginLogout( MainFrame frame) {
		//結果
		String res = "";
		if( flagLogin) {
			flagLogin = false;
			frame.buttonLog.setLabel("ログイン");
			frame.tfLoginID.setText("未ログイン");
		} else {
			//ログインダイヤログ生成・表示
			LoginDialog ld = new LoginDialog( frame);
			ld.setBounds( 100, 100, 350, 150);
			ld.setResizable( false);
			ld.setVisible(true);
			ld.setModalityType( Dialog.ModalityType.APPLICATION_MODAL);
			
			//ユーザIDとパスが入力された場合の処理
			if ( ld.canceled) {
				return "";
			}
			//ユーザID、パスが入力された場合の処理
			reservationUserID = ld.tfUserID.getText();
			String password = ld.tfPassword.getText();
			
			connectDB();
		
			try {
			
				String sql = "SELECT * FROM db_reservation.user WHERE user_id ='" + reservationUserID + "';";
				ResultSet rs = sqlStmt.executeQuery( sql);
			
				if( rs.next())	{
					String password_from_db = rs.getString("Password");
					if ( password_from_db.equals(password))	{
						flagLogin = true;
						frame.buttonLog.setLabel("ログアウト");
						frame.tfLoginID.setText(reservationUserID);
						res = "";
					} else {
						res = "IDまたはパスワードが違います";
					}
				} else {
					res = "IDが違います";
				}
		
			} catch ( Exception e) {
				e.printStackTrace();
			}
			closeDB();
		}
		return res;
	}
	//ReservationControl class(2)
	///教室概要ボタン押下時の処理を行うメソッド
	public	String	getFacilityExplanation( String facility_id) {
		String	res = "";
		String	exp = "";
		String	openTime = "";
		String	closeTime = "";
		connectDB();
		try {
			String sql = "SELECT * FROM db_reservation.facility WHERE facility_id ='" + facility_id + "';";
			ResultSet rs = sqlStmt.executeQuery( sql);
			if( rs.next())	{
				exp = rs.getString("explanation");
				openTime = rs.getString("open_time");
				closeTime = rs.getString("close_time");
				//教室概要データ作成
				res = exp + "	利用可能時間:" + openTime.substring( 0,5) + "～" + closeTime.substring( 0,5);
			} else {
				res = "教室番号が違います";
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return res;
	}
	
	//ReservationControl class(3)
	///全てのfacility_idを取得するメソッド
	public List getFacilityId() {
		List<String> facilityId = new ArrayList<String>();
		connectDB();
		try {
			String	sql = "SELECT * FROM db_reservation.facility;";
			ResultSet rs = sqlStmt.executeQuery( sql);
			while( rs.next()) {
				facilityId.add( rs.getString("facility_id"));
			}
		} catch( Exception e){
			e.printStackTrace();
		}
		return facilityId;
	}
	
	//新規予約システム  追加（課題2）
	public String makeReservation( MainFrame frame) {
		String res = "";
		String startTime = "";
		String endTime = "";
		List<String> CheckList_start = new ArrayList<>();
		List<String> CheckList_end = new ArrayList<>();
		
		if(flagLogin) {
			ReservationDialog  rd = new ReservationDialog(frame,this);
			
			rd.setVisible(true);
			if(rd.canceled) {
				return res;
			}
			
			String ryear_str = rd.tfYear.getText();
			String rmonth_str = rd.tfMonth.getText();
			String rday_str = rd.tfDay.getText();
			
			Calendar justNow = Calendar.getInstance();
			SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now =resDate.format(justNow.getTime());
			String facility = rd.choiceFacility.getSelectedItem();
			String st = rd.startHour.getSelectedItem()+":"+rd.startMinute.getSelectedItem()+":00";
			String et = rd.endHour.getSelectedItem()+":"+rd.endMinute.getSelectedItem()+":00";
			
			if(rmonth_str.length()==1) {
				rmonth_str = "0" + rmonth_str;
			}
			if(rday_str.length()==1) {
				rday_str = "0" + rday_str;
			}
			String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			//入力された日付が正しいのかチェック
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setLenient(false);
				String convData = df.format( df.parse(rdate));
				if((! rdate.equals(convData))||(ryear_str.length()!= 4)) {
					res ="日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
					return res;
				}
				//予約時間と現在時刻を比較し、過去に対する予約を防止
				String reservationTimeCK = convData +" "+ st; 
				String rt =resDate.format(resDate.parse(reservationTimeCK));
				
				//デバッグ用
				//System.out.println("rt:" + rt);
				
				if (rt.compareTo(now)<0) {
					//デバッグ用
					//System.out.println("rt:" + rt);
					//System.out.println("now:" + now);
					
					res = "過去に対する予約はできません";
					return res;
				}
			}catch(ParseException p) {
				res = "日付の値を修正してください";
				return res;
			}
			// 開始/終了時刻のエラーチェック
			if(st.compareTo( et)>=0) {
				res = "開始時刻と終了時刻が同じか終了時刻の方が早くなっています";
			}else {
				connectDB();
				try {
					//Reservationテーブルから予約済みデータを取得
					String sql = "SELECT * FROM db_reservation.reservation WHERE day ='" + rdate + "' AND facility_id ='" + facility + "';";
					ResultSet rs = sqlStmt.executeQuery( sql);
					boolean isOverlap = false;
					while(rs.next()) {
						startTime = rs.getString("start_time");
						endTime = rs.getString("end_time");
						CheckList_start.add(startTime);
						CheckList_end.add(endTime);
					}
					int count = CheckList_start.size();
					//新規予約の内容が既にReservationテーブルに予約済みであるかチェック
					for (int i = 0; i < count; i++) {
						if(((CheckList_start.get(i)).compareTo(et) < 0 && (CheckList_end.get(i)).compareTo(st) > 0)) {
								isOverlap = true;
						}
					}
					//新規予約が予約済みデータと競合する場合
					if (isOverlap) {
						res = "既にその時間帯は予約されています";
						return res;
					} else {
					//新規予約が予約済みデータと競合しない場合
						String Sql = "INSERT INTO db_reservation.reservation(facility_id, user_id, date, day, start_time, end_time)VALUES('"
								+facility+"','"+reservationUserID+"','"+now+"','"+rdate+"','"+st+"','"+et+"');";
						sqlStmt.executeUpdate( Sql);
						res = "予約されました";
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				closeDB();
			}
		} else {
			res = "ログインしてください";
		}
		return res;
	}
	
	public int[] getAvailableTime( String facility) {
		int [] abailableTime = { 0, 0};
		connectDB();
		try {
			String sql = "SELECT * FROM db_reservation.facility WHERE facility_id =" + facility + ";";
			ResultSet rs = sqlStmt.executeQuery(sql);
			while (rs.next()) {
				String timeData = rs.getString("open_time");
				timeData = timeData.substring(0, 2);
				abailableTime[0] = Integer.parseInt(timeData);
				timeData = rs.getString("close_time");
				timeData = timeData.substring(0, 2);
				abailableTime[1] = Integer.parseInt(timeData);
			  }
		  } catch(Exception e) {
			  e.printStackTrace();
		  }
		  return abailableTime;
	}
	
	//教室予約状況確認機能 追加（課題1）
	public String getFacility_Reservation( MainFrame frame){
		String res = "";														//戻り値を設定,初期化
		String	date = "";														//reservationテーブル,"day"要素取得用
		String	startTime = "";													//reservationテーブル,"start_time"要素取得用
		String	endTime = "";													//reservationテーブル,"end_time"要素取得用
		List<String> TimeList = new ArrayList<>();								//reservationテーブル,"start_time","end_time"要素取得後、タイムテーブル作成用
		ReservationCheckDialog  rd = new ReservationCheckDialog(frame,this);	//ReservationCheckDialog読み込み用
		rd.setVisible(true);													//教室予約状況ダイアログを可視化
		
		//教室予約状況ダイアログでキャンセルが指定されたら，メソッドの処理を終了する
		if(rd.canceled) {		
				res = "予約状況の確認をキャンセルしました";
				return res;
			}
			
			String ryear_str = rd.tfYear.getText();								//教室予約状況ダイアログで指定された「年」を取得
			String rmonth_str = rd.tfMonth.getText();							//教室予約状況ダイアログで指定された「月」を取得
			String rday_str = rd.tfDay.getText();								//教室予約状況ダイアログで指定された「日」を取得
			String facility = rd.choiceFacility.getSelectedItem();				//教室予約状況ダイアログで指定された「教室」を取得
			
			//月と日は１桁で入力される可能性があるため，１桁なら数字の手前に0を入れる
			if(rmonth_str.length()==1) {										
				rmonth_str = "0" + rmonth_str;
			}
			if(rday_str.length()==1) {
				rday_str = "0" + rday_str;
			}
			String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			
			//入力された日付が正しいかチェック
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setLenient(false);
				String convData = df.format( df.parse(rdate));
				if((! rdate.equals(convData))||(ryear_str.length()!= 4)) {
					res ="日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
					return res;
				}	
			}catch(ParseException p) {
				res = "日付の値を修正してください";
				return res;
			
			}
			
			connectDB();														//MySQLと接続
			try {
				//教室予約状況確認の対象教室,日時のカラムを持っている、レコードの"start_time","end_time"を取得
				String sql = "SELECT * FROM db_reservation.reservation WHERE day ='" + rdate + "' AND facility_id ='" + facility + "';";
				ResultSet rs = sqlStmt.executeQuery( sql);
				while( rs.next())	{
					date = rs.getString("day");									//条件分岐用
					startTime = rs.getString("start_time");						//start_timeを取得
					endTime = rs.getString("end_time");							//end_timeを取得
					//"start_time"と"end_time"を結合して整形,タイムテーブルに記録準備
					TimeList.add(startTime.substring( 0,5)+"～"+endTime.substring( 0,5));
					
				} 	
			} catch( Exception e) {
				e.printStackTrace();
			}
			//入力した日付がreservationテーブルにある"day"に該当する者がある場合
			if (rdate.equals(date)) {
				
				Collections.sort(TimeList);										//予約時間順にソートする
				String TimeListString = String.join(", ", TimeList);			//予約済みのタイムテーブルを作成
				res = facility +"教室、"+ rdate +" に以下の時間帯が予約されています：	" +"\n"+ TimeListString;
			} 
			else {
				res = facility + "教室は予約可能です";
			}
			closeDB();															//MySQLとの接続を解除
			return res;
	}
	
	// 自己予約確認	追加(課題2)
	public String  MyReservation( MainFrame frame){
		String res = "";																	//戻り値を設定,初期化
		String	date = "";																	//reservationテーブル,"day"要素取得用
		String	startTime = "";																//reservationテーブル,"start_time"要素取得用
		String	endTime = "";																//reservationテーブル,"end_time"要素取得用
		String	facility = "";																//reservationテーブル,"facility_id"要素取得用
		List<String> MyList = new ArrayList<>();											//reservationテーブル,"start_time","end_time"要素取得後、タイムテーブル作成用
		MyReservationSettingDialog  mrs = new MyReservationSettingDialog(frame,this);		//MyReservationDialog読み込み用
		if(flagLogin) {
			String user_id = reservationUserID;												//現在ログイン中のユーザーIDを取得
		
			//自己予約確認の当日データ作成
			Calendar justNow = Calendar.getInstance();
			SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now =resDate.format(justNow.getTime());
			
			if( Set_date1 != null && mrs.ResetSetting == false) {
				facility_id = mrs.choiceFacility.getSelectedItem();							//選択教室指定用
				connectDB();																//MySQLと接続
				try {
					//自己予約確認の対象教室,日時,ユーザーIDのカラムを持っている、レコードの情報を取得
					String sql = "SELECT * FROM db_reservation.reservation WHERE '" + Set_date1 + "' <= day AND  day <='"  + Set_date2 + "' AND user_id = '" 
									+ user_id + "' AND facility_id = '" + facility_id +"';";
					ResultSet rs = sqlStmt.executeQuery( sql);
					while( rs.next())	{
						date = rs.getString("day");
						startTime = rs.getString("start_time");
						endTime = rs.getString("end_time");
						//"start_time"と"end_time"を結合して整形,タイムテーブルに記録準備
						MyList.add(facility_id + "教室,  " + date + ",  " +startTime.substring( 0,5)+"～"+endTime.substring( 0,5));
					} 
					Collections.sort(MyList);												//予約時間順にソートする
					int count = MyList.size();
					//入力した期間に該当する自己予約データがない場合
					if (date == "") {
						res = "指定された期間には," + user_id + "様の予約がありませんでした";
					} else {
					// dateに該当する日時があった場合
						res += user_id + "様の指定された," + Set_date1 + "～" + Set_date2 +" の期間の予約が見つかりました" + "\n";
						for (int i = 0; i < count; i++) {
							res += MyList.get(i)+ "\n";
						}
					}
					return res;
				} catch( Exception e) {
					e.printStackTrace();
				}
				closeDB();																	//MySQLとの接続を解除
				return res;
				
			} else {
				//デバッグ用
				//System.out.println("start:" + Set_date1);
				//System.out.println("end:" + Set_date2);
		    	connectDB();																//MySQLと接続
				try {
					//ReservationテーブルにてユーザーIDを検索、参照
					String sql = "SELECT * FROM db_reservation.reservation WHERE user_id ='" + user_id + "' AND day >='" + now +  "';";
					ResultSet rs = sqlStmt.executeQuery( sql);
					while( rs.next())  {
						date = rs.getString("day");
						facility = rs.getString("facility_id");
						startTime = rs.getString("start_time");
						endTime = rs.getString("end_time");
						//自己予約をリストデータとして記憶
						MyList.add(facility + "教室,  " + date + ",  " +startTime.substring( 0,5)+"～"+endTime.substring( 0,5));
					}} catch( Exception e) {
						e.printStackTrace();
					}
					if ( date.compareTo(now) >= 0) {
						//リストデータを日付順にソート
						Collections.sort(MyList);										//予約時間順にソートする
						int count = MyList.size();
						//戻り値を設定
						res = "ユーザーID：" + user_id + "様が登録された本日以降の予約は以下の通りです"+ "\n";
						for (int i = 0; i < Math.min(21, count); i++) {
							res += MyList.get(i)+ "\n";
						}
						res += "" + "\n";
						if( count >= 20) {
							res += "									予約表示件数（20/" + count +" 件）";
						} else {
							res += "									予約表示件数（"+count+"/" + count +" 件）";
						}
					}else{
						res = "本日以降の予約は見当たりません";
						return res;
					}
			} 
				
			closeDB();
			return res;
			
		}else {
			res = "ログインしてください";
		}
		return res;
	}
	
	//自己予約確認Option設定用	追加（課題2）
	public String  MyCheckSetting( MainFrame frame){
			String res = "";																	//戻り値を設定,初期化
			MyReservationSettingDialog  mrs = new MyReservationSettingDialog(frame,this);		//MyReservationDialog読み込み用
			
			//自己予約確認の当日データ作成
			Calendar justNow = Calendar.getInstance();
			SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd");
			String now =resDate.format(justNow.getTime());
			
			if(flagLogin) {
				mrs.setVisible(true);															//自己予約確認Option設定ダイアログを可視化
				//自己予約確認Option設定ダイアログでキャンセルが指定されたら，メソッドの処理を終了する
				if(mrs.canceled) {		
					res = "自己予約確認Option設定をキャンセルしました";
					return res;
				}
				if (mrs.ResetSetting) {
		            Set_date1 = null;
		            Set_date2 = null;
		            mrs.ResetSetting = false;
		            res =  "初期化成功";
		            return res;
		        }
				//指定期間設定
				String ryear_str1 = mrs.tfYear_start.getText();									//自己予約確認Option設定ダイアログで指定された期間開始「年」を取得
				String rmonth_str1 = mrs.tfMonth_start.getText();				       			//自己予約確認Option設定ダイアログで指定された期間開始「月」を取得
				String rday_str1 = mrs.tfDay_start.getText();									//自己予約確認Option設定ダイアログで指定された期間開始「日」を取得
				
				String ryear_str2 = mrs.tfYear_end.getText();									//自己予約確認Option設定ダイアログで指定された期間開始「年」を取得
				String rmonth_str2 = mrs.tfMonth_end.getText();									//自己予約確認Option設定ダイアログで指定された期間開始「月」を取得
				String rday_str2 = mrs.tfDay_end.getText();										//自己予約確認Option設定ダイアログで指定された期間開始「日」を取得
				
				//期間開始側 データ整形
				//月と日は１桁で入力される可能性があるため，１桁なら数字の手前に0を入れる
				if(rmonth_str1.length()==1) {										
					rmonth_str1 = "0" + rmonth_str1;
				}
				if(rday_str1.length()==1) {
					rday_str1 = "0" + rday_str1;
				}
				String rdate1 = ryear_str1 + "-" + rmonth_str1 + "-" + rday_str1;
				//期間終了側 データ整形
				//月と日は１桁で入力される可能性があるため，１桁なら数字の手前に0を入れる
				if(rmonth_str2.length()==1) {										
					rmonth_str2 = "0" + rmonth_str2;
				}
				if(rday_str2.length()==1) {
					rday_str2 = "0" + rday_str2;
				}
				String rdate2 = ryear_str2 + "-" + rmonth_str2 + "-" + rday_str2;
				
				//期間開始側 入力された日付が正しいかチェック
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					df.setLenient(false);
					String convData = df.format( df.parse(rdate1));
					if((! rdate1.equals(convData))||(ryear_str1.length()!= 4)) {
						res ="期間開始日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
						return res;
					}
					if (!(rdate1.compareTo(now)>=0)) {
						res ="期間開始日が本日以降でないため設定できません";
						return res;
					}
				}catch(ParseException p) {
					res = "期間開始日付の値を修正してください";
					return res;
				}
				//期間終了側 入力された日付正しいかチェック
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					df.setLenient(false);
					String convData = df.format( df.parse(rdate2));
					if((! rdate2.equals(convData))||(ryear_str2.length()!= 4)) {
						res ="期間終了日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
						return res;
					}	
				}catch(ParseException p) {
					res = "期間終了日付の値を修正してください";
					return res;
				}
				String day1 = rdate1;											//期間開始日時指定用
				String day2 = rdate2;											//期間終了日時指定用
				facility_id = mrs.choiceFacility.getSelectedItem();				//選択教室指定用
				connectDB();													//MySQLと接続
				try {
					//開始日と終了日が指定入力用コンポーネントに正しく設定されているかチェック
					if (day1.compareTo(day2) < 0) {
						Set_date1 = day1;
						Set_date2 = day2;
						res = "設定完了";
					} else {
						res = "指定期間が開始日～終了日に沿った入力でないため設定できません";
						return res;
					}
				} catch( Exception e) {
					e.printStackTrace();
				}
				closeDB();														//MySQLとの接続を解除
				return res;
			}else {
				res = "ログインしてください";
			}
			return res;
	}
    
	//予約キャンセル 追加（課題3）
	public String ReservationCancel(MainFrame frame) {
		String res = "";																	//戻り値を設定,初期化
		String startTime = "";																//予約キャンセル対象の開始時刻取得用
		String endTime = "";																//予約キャンセル対象の終了時刻取得用
		List<String> CancelList_start = new ArrayList<>();									//予約キャンセル対象の開始時刻確認用
		List<String> CancelList_end = new ArrayList<>();									//予約キャンセル対象の終了時刻確認用
		CancelDialog  cr = new CancelDialog(frame,this);									//CancelDialog読み込み用
		
		if(flagLogin) {																		//ログインしている場合
			cr.setVisible(true);															//	CancelDialogを可視化
			if(cr.canceled) {																//	操作中にキャンセルボタンを押下した場合
				res = "予約取り消しをキャンセルしました";											//		操作を中断し、戻り値を「予約取り消しをキャンセルしました」に設定する
				return res;																	
			}
			//入力された予約日の取得
			String ryear_str = cr.tfYear.getText();
			String rmonth_str = cr.tfMonth.getText();
			String rday_str = cr.tfDay.getText();
			//現在の日時を取得
			Calendar justNow = Calendar.getInstance();
			SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now =resDate.format(justNow.getTime());
			//選択された予約教室を取得
			String facility = cr.choiceFacility.getSelectedItem();
			//選択された予約時間を取得
			String st = cr.startHour.getSelectedItem()+":"+cr.startMinute.getSelectedItem()+":00";
			String et = cr.endHour.getSelectedItem()+":"+cr.endMinute.getSelectedItem()+":00";
			
			//入力された予約日のデータを整形
			if(rmonth_str.length()==1) {
				rmonth_str = "0" + rmonth_str;
			}
			if(rday_str.length()==1) {
				rday_str = "0" + rday_str;
			}
			String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
			//入力された日付が正しいのかチェック
			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setLenient(false);
				String convData = df.format( df.parse(rdate));
				if((! rdate.equals(convData))||(ryear_str.length()!= 4)) {
					res ="日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
					return res;
				}
				//予約時間と現在時刻を比較し、過去に対する予約キャンセルを防止
				String reservationTimeCK = convData +" "+ st; 
				String rt =resDate.format(resDate.parse(reservationTimeCK));
				//デバッグ用
				//System.out.println("rt:" + rt);
				
				if (rt.compareTo(now)<0) {
					//デバッグ用
					//System.out.println("rt:" + rt);
					//System.out.println("now:" + now);
					res = "過去に対する予約キャンセルはできません";
					return res;
				}
			}catch(ParseException p) {
				res = "日付の値を修正してください";
				return res;
			}
			// 開始/終了時刻のエラーチェック
			if(st.compareTo( et)>=0) {
				res = "開始時刻と終了時刻が同じか終了時刻の方が早くなっています";
			}else {
				connectDB();
				try {
					//Reservationテーブルから予約済みデータを取得
					String sql = "SELECT * FROM db_reservation.reservation WHERE day ='" + rdate + "' AND facility_id ='" + facility + "';";
					ResultSet rs = sqlStmt.executeQuery( sql);
					boolean isExist = false;
					while(rs.next()) {
						startTime = rs.getString("start_time");
						endTime = rs.getString("end_time");
						CancelList_start.add(startTime);
						CancelList_end.add(endTime);
					}
					int count = CancelList_start.size();
					//キャンセル内容がReservationテーブルに存在するか確認
					for (int i = 0; i < count; i++) {
						if(((CancelList_start.get(i)).compareTo(et) < 0 && (CancelList_end.get(i)).compareTo(st) > 0)) {
								isExist = true;
						}
					}
					//キャンセル対象が存在する場合
					if (isExist) {
						//DELETE文にてキャンセル対象の予約を取り消す
						String Sql = "DELETE FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "' AND facility_id = '" + facility
								+"' AND day = '" + rdate + "' AND start_time = '" + st + "' AND end_time = '" + et + "';";
						sqlStmt.executeUpdate( Sql);
						res = facility+"教室 "+rdate+", "+st.substring( 0,5)+"～"+et.substring( 0,5)+" 枠の予約キャンセルに成功しました";
					} else {
					//キャンセル対象が存在しない場合
						res = "キャンセル対象が存在しません";
						return res;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				closeDB();
			}
		} else {
			res = "ログインしてください";
		}	
		return res;
	}
}
