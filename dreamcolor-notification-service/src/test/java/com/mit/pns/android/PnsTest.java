package com.mit.pns.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mit.pns.android.entities.GCMContent;
import com.mit.pns.android.entities.GCMResult;
import com.mit.utils.JsonUtils;


public class PnsTest {
	public static void main(String[] args) {
		List<String> arrRegistra = new ArrayList<String>();
//		List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByDevice(1);
//		for (DeviceToken token : tokens) {
//			arrRegistra.add(token.getDeviceToken());
//		}
		arrRegistra.add("cEdkyZxaTQI:APA91bHHvNc-M988DkPyfdcrwFj1oJvlV6wlhhcYWplExXERagGXu5PNaisNhyjoomDnV2VNrlFHC2n8r0RaOUrbmw2OAJx-6yuY8ceivGlnxZc0msYyFFoBDwHFiTmZH-69hzCm5_Su");

		GCMContent content = new GCMContent();
		//content.setDryRun(true);
		content.setRegistrationIds(arrRegistra);

		Map<String, Object> contentData = new HashMap<String, Object>();
		contentData.put("type", "1");
		contentData.put("id", "1");
		contentData.put("content", "Message");
		content.setData(contentData);
		try {
			GCMResult rs = AndroidGCMSender.send(JsonUtils.Instance.toJson(content), null);
			System.out.println(JsonUtils.Instance.toJson(rs));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
