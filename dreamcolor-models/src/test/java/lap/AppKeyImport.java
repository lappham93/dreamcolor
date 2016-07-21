package lap;

import com.mit.dao.AppKeyDAO;
import com.mit.entities.app.AppKey;

public class AppKeyImport {

	public static void main(String[] args) {
//		AppKeyDAO.getInstance().truncate();
		AppKey appKeyAnd = new AppKey(1, 1, "DreaMau", 1, "DreaMau", "d1a29c9a29608ead984483df8dabbb18", "000ec71ebb7d4dc72eb3511c3a31040a");
		AppKey appKeyIOS = new AppKey(2, 1, "DreaMau", 1, "DreaMau", "8eb16baaa913cbb306c755bb4c7b77a2", "71e5c3533e29b864530655e9d5ed1397");
		AppKeyDAO.getInstance().insert(appKeyAnd);
		AppKeyDAO.getInstance().insert(appKeyIOS);

//		UserSettings setting = new UserSettings(3, new NearbySetting(), new GlobalSetting(), Collections.emptyList(), Collections.emptyList());
//		UserSettingDAO.getInstance().updateNearBySetting(3, new NearbySetting());
//		UserSettings setting1 = new UserSettings(4, new NearbySetting(), new GlobalSetting(), Collections.emptyList(), Collections.emptyList());
//		UserSettingDAO.getInstance().updateNearBySetting(4, new NearbySetting());
//		System.out.println(UserSettingDAO.getInstance().getNearBySetting(3).toDocument());
		//System.out.println(UserSettingDAO.getInstance().isBanMsg(4, 3));
//		System.out.println(IDGeneration.Instance.generateId("license_acceptance"));
	}

}