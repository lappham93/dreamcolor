package lap;

import java.util.Map;

import com.mit.dao.AppKeyDAO;
import com.mit.entities.app.AppKey;
import com.mit.models.ColorModel;
import com.mit.models.VideoModel;
import com.mit.utils.JsonUtils;

public class Test {
	public static void main(String[] args) {
		AppKey app = new AppKey(2, 1, "dreamcolor", 1, "dkmobility", "sajf3dc75e6fe8689e11f7ab597j192c", "ss2144ccc95099082f5705093ea2hh2");
		AppKeyDAO.getInstance().insert(app);
	}
}
