package lap;

import java.util.Map;

import com.mit.models.ColorModel;
import com.mit.models.VideoModel;
import com.mit.utils.JsonUtils;

public class Test {
	public static void main(String[] args) {
		Map<String, Object> rs = VideoModel.Instance.getListVideo(10, 0, 1);
		System.out.println(JsonUtils.Instance.toJson(rs));
	}
}
