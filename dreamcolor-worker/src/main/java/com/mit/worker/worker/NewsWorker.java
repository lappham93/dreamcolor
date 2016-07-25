package com.mit.worker.worker;

import java.util.List;

import com.mit.dao.notification.UserNewsDAO;
import com.mit.dao.user.DeviceTokenDAO;
import com.mit.entities.user.DeviceToken;

public class NewsWorker {
	public static void viewNews(String imei, List<Long> newsIds) {
		DeviceToken device = DeviceTokenDAO.getInstance().getByImei(imei);
		if (device != null) {
			UserNewsDAO.getInstance().updateView(device.getId(), newsIds);
		}
	}
}
