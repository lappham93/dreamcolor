package lap;

import java.util.List;

import com.mit.dao.banner.BannerDAO;
import com.mit.entities.banner.Banner;
import com.mit.entities.banner.BannerType;
import com.mit.utils.JsonUtils;

public class Test {
	public static void main(String[] args) {
		List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.WELCOME.getValue());
		System.out.println(JsonUtils.Instance.toJson(listB));
	}
}
