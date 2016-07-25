/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.dreamcolor.admin.handler;

import java.util.Arrays;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.color.ColorDAO;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.entities.color.Color;
import com.mit.models.NotificationModel;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class NotificationHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(NotificationHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();

			renderPageNotification(dic, req, resp);
			dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "notification.xtm", req));
			print(applyTemplateLayoutMain(dic, req, resp), resp);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();
			JsonObject result = new JsonObject();
			result.set("err", -1);
			result.set("msg", "Execute fail. Please try again.");

			String action = req.getParameter("action");
			String callback = req.getParameter("callback");
			if (action != null && !action.isEmpty()) {
				if ("noticolor".equalsIgnoreCase(action)) {
					notifyColor(req, resp, result);
				}

				if (HttpHelper.isAjaxRequest(req)) {
					if (callback != null && !callback.isEmpty()) {
						printStrJSON(callback + "(" + result.toString() + ")", resp);
					} else {
						printStrJSON(result.toString(), resp);
					}
				} else {
					dic.setVariable("callback", callback);
					dic.setVariable("data", result.toString());
					print(applyTemplate(dic, "iframe_callback", req), resp);
				}
			} else {
				printStrJSON(result.toString(), resp);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void renderPageNotification(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {

//		List<Color> colors = ColorDAO.getInstance().getSlice(10, 0, "createTime", false);
//		if (colors != null && !colors.isEmpty()) {
//			for (Color color : colors) {
//				TemplateDataDictionary colorDic = dic.addSection("loop_option_color");
//				colorDic.setVariable("COLOR_ID", MIdNoise.enNoiseLId(color.getId()));
//				colorDic.setVariable("COLOR_CODE", color.getCode());
//			}
//		}
	}

	private void notifyColor(ServletRequest req, ServletResponse resp, JsonObject result) {
//		long colorId = 0;
		String msg = req.getParameter("msg");
//		try {
//			colorId = MIdNoise.deNoiseLId(req.getParameter("color"));
//			msg = req.getParameter("msg");
//		} catch (Exception e) {
//			colorId = 0;
//		}
		
		if (!msg.isEmpty()) {
			Color color = ColorDAO.getInstance().getNewest();
			if (color != null) {
				NotificationModel.Instance.notifyColor(Arrays.asList(color.getId()), color.getPhoto(), msg);
				result.set("err", 0);
				result.set("msg", "Notify color successfully");
			} else {
				result.set("err", -1);
				result.set("msg", "Notify color fail");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Param invalid");
		}
	}
}
