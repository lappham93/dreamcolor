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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.dreamcolor.admin.utils.UploadFormUtil;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;
import com.mit.entities.photo.PhotoType;
import com.mit.midutil.MIdNoise;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class UploadMultiColorHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(UploadMultiColorHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();
			renderPageUpload(dic, req, resp);
			dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "upload.xtm", req));

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
			String action = "";
			String callback = "";
			if (HttpHelper.isMultipartRequest(req)) {
				List<FileItem> files = new ArrayList<FileItem>();
				Map<String, String> params = new HashMap<String, String>();
				UploadFormUtil.getInstance().getMapFormUpload(req, files, params);
				if (params != null && !params.isEmpty()) {
					action = params.containsKey("action") ? params.get("action") : "";
					callback = params.containsKey("callback") ? params.get("callback") : "";
					if (action != null && !action.isEmpty()) {
						if ("upload".equalsIgnoreCase(action)) {
							uploadMultiColor(req, resp, result, files, params);
						}
					}
				}
			} else {
				action = req.getParameter("action");
				callback = req.getParameter("callback");
				if (action != null && !action.isEmpty()) {
				}
			}

			if (action != null && !action.isEmpty()) {
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

	private void renderPageUpload(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {
		List<Category> cates = CategoryDAO.getInstance().getAll("updateTime", false);
		for (Category cate : cates) {
			if (cate.getStatus() > 0) {
				TemplateDataDictionary loopOption = dic.addSection("loop_option");
				loopOption.setVariable("KEY", MIdNoise.enNoiseIId(cate.getId()));
				loopOption.setVariable("VALUE", cate.getName());
			}
		}
	}

	/*
	 * upload multiple photos pre condition: photos in a category basename of
	 * file become code of color
	 */
	private void uploadMultiColor(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			List<FileItem> photos, Map<String, String> params) {
		int cateId = 0;
		try {
			cateId = MIdNoise.deNoiseIId(params.get("cate"));
		} catch (Exception e) {
			cateId = 0;
		}
		if (photos != null && !photos.isEmpty() && cateId > 0) {
			List<Color> colors = new ArrayList<Color>();
			for (FileItem photo : photos) {
				// upload photo
				long photoId = PhotoUtil.Instance.uploadPhoto(photo, PhotoType.COLOR);
				if (photoId <= 0) {
					result.set("err", -1);
					result.set("msg", "Can't insert photo");
					return;
				}
				String code = FilenameUtils.getBaseName(photo.getName());
				Color color = new Color(0, cateId, code, photoId, false);
				colors.add(color);
			}
			if (ColorDAO.getInstance().insertBatch(colors) >= 0) {
				result.set("err", 0);
				result.set("msg", "Upload successfully");
			} else {
				result.set("err", -1);
				result.set("msg", "Upload fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		}
	}
}
