package client_system;

import java.awt.*;
import java.util.List;

//追加クラス
public class ChoiceFacility extends Choice{
	//ChoiceFacilityコンストラクタ
	ChoiceFacility( List<String> facility){	//引数でfacility_idのリストを受け取る
		for( String id:facility) {			//facility_idを「id」に１つづつ取り出す
			add(id);						//choiceにfacility_idを１つ追加する
		}
	}
}
