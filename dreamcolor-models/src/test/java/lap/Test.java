package lap;

import java.util.Map;

import com.mit.models.ColorModel;
import com.mit.utils.JsonUtils;

public class Test {
	public static void main(String[] args) {
		Map<String, Object> rs = ColorModel.Instance.getListCategory(10, 0);
		System.out.println(JsonUtils.Instance.toJson(rs));
	}
}
