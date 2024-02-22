package client_system;

import java.awt.*;

public class ChoiceHour extends Choice{
	//コンストラクタ
	ChoiceHour(){
		resetRange(9,21);
	}
	//指定できる時刻の範囲を設定
	public void resetRange(int start,int end) {
	removeAll();
	while(start<=end) {
		String h=String.valueOf(start);
		if(h.length()==1) {
			h="0"+h;
		}
		add(h);
		start++;
	}
	}
	//設定されている一番早い時刻を返す
	public String getFirst() {
		return getItem(0);
	}
	//設定されている一番遅い時刻を返す
	public String getLast(){
		return getItem(getItemCount()-1);
	}
}