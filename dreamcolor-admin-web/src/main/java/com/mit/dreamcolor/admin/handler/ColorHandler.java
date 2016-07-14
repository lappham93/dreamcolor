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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.dreamcolor.admin.common.Common;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.common.Paging;
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
public class ColorHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(ColorHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();

			// select template.
			String option = req.getParameter("option");
			if ("color".equalsIgnoreCase(option)) {
				renderPageColor(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "color.xtm", req));
			} else {
				renderPageCategory(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "color_category.xtm", req));
			}

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
				Map<String, FileItem> mapFile = new HashMap<String, FileItem>();
				Map<String, String> params = new HashMap<String, String>();
				UploadFormUtil.getInstance().getMapFormUpload(req, mapFile, params);
				if (params != null && !params.isEmpty()) {
					action = params.containsKey("action") ? params.get("action") : "";
					callback = params.containsKey("callback") ? params.get("callback") : "";
					if (action != null && !action.isEmpty()) {
						if ("addcate".equalsIgnoreCase(action)) {
							addCategory(req, resp, result, mapFile, params);
						} else if ("changecp".equalsIgnoreCase(action)) {
							changeCategoryPhoto(req, resp, result, mapFile, params);
						} else if ("addcolor".equalsIgnoreCase(action)) {
							addColor(req, resp, result, mapFile, params);
						} else if ("changecop".equalsIgnoreCase(action)) {
							changeColorPhoto(req, resp, result, mapFile, params);
						}
					}
				}
			} else {
				action = req.getParameter("action");
				callback = req.getParameter("callback");
				if (action != null && !action.isEmpty()) {
					if ("getcate".equalsIgnoreCase(action)) {
						getCategory(req, resp, result);
					} else if ("editcate".equalsIgnoreCase(action)) {
						editCategory(req, resp, result);
					} else if ("getcolor".equalsIgnoreCase(action)) {
						getColor(req, resp, result);
					} else if ("editcolor".equalsIgnoreCase(action)) {
						editProduct(req, resp, result);
					}
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

	private void renderPageCategory(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {

		List<Category> cates = CategoryDAO.getInstance().getAllIgnoreStatus("updateTime", false);
		if (cates != null && !cates.isEmpty()) {
			int i = 1;
			for (Category cate : cates) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("ID", MIdNoise.enNoiseIId(cate.getId()));
				loopRow.setVariable("NAME", cate.getName());
				loopRow.setVariable("DESC", cate.getDescription());
				if (cate.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}
				String uri = PhotoUtil.Instance.buildURIImg(cate.getPhotoNum(), PhotoType.COLOR);
				loopRow.setVariable("URI_CP", uri);
				i++;
			}
		} else {
			dic.addSection("table_empty");
		}
	}

	private void renderPageColor(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {

		// render selection.
		Map<Integer, String> mapCate = new HashMap<Integer, String>();
		List<Category> cates = CategoryDAO.getInstance().getAll("updateTime", false);
		if (cates != null && !cates.isEmpty()) {
			for (Category cate : cates) {
				mapCate.put(cate.getId(), cate.getName());
			}
			for (Category cate : cates) {
				if (cate.getStatus() > 0) {
					TemplateDataDictionary loopOption = dic.addSection("loop_option");
					loopOption.setVariable("KEY", MIdNoise.enNoiseIId(cate.getId()));
					loopOption.setVariable("VALUE", cate.getName());
				}
			}
		}

		// render table.
		Paging paging = new Paging();
		String spage = req.getParameter("page");
		spage = spage != null && !spage.isEmpty() ? spage : "1";
		int page = Integer.parseInt(spage);
		paging.currPage = page;
		paging.pageSize = Configuration.APP_PAGING_PAGE_SIZE;
		paging.numDisplay = Configuration.APP_PAGING_NUM_DISPLAY;
		paging.totalRecords = ColorDAO.getInstance().totalAllIgnoreStatus();

		if (paging.totalRecords <= 0) {
			dic.addSection("empty");
			return;
		}

		int totalPages = paging.getTotalPages();
		paging.currPage = Paging.clamp(paging.currPage, 1, totalPages);
		int offset = (paging.currPage - 1) * paging.pageSize;
		List<Color> colors = ColorDAO.getInstance().getSliceIgnoreStatus(paging.pageSize, offset, "updateTime", false);

		if (colors != null && !colors.isEmpty()) {
			dic.addSection("HAS_TABLE");
			int i = offset + 1;
			for (Color color : colors) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("CODE", color.getCode());
				loopRow.setVariable("ID", MIdNoise.enNoiseLId(color.getId()));
				String cate = !mapCate.isEmpty() && mapCate.containsKey(color.getCategoryId())
						? mapCate.get(color.getCategoryId()) : "";
				loopRow.setVariable("CATE", cate);
				loopRow.setVariable("VIEWS", String.valueOf(color.getViews()));
				if (color.getIsFeature()) {
					loopRow.addSection("FEATURE_ON");
				} else {
					loopRow.addSection("FEATURE_OFF");
				}
				loopRow.setVariable("UPDATE", Common.timeAgo(color.getUpdateTime()));
				if (color.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}
				// photo
				String priPhoto = PhotoUtil.Instance.buildURIImg(color.getPhoto(), PhotoType.COLOR);
				loopRow.setVariable("URI_CP", priPhoto);
				i++;
			}
		} else {
			dic.addSection("table_empty");
		}

		// paging.
		JsonObject jsonPaging = new JsonObject();
		jsonPaging.set("currentPage", paging.currPage);
		jsonPaging.set("pageSize", paging.pageSize);
		jsonPaging.set("numDisplay", paging.numDisplay);
		jsonPaging.set("totalRecord", paging.totalRecords);
		jsonPaging.set("action", "/web/admin/color");
		dic.setVariable("paging", jsonPaging.toString());
		dic.setVariable("keyword", "option=color");
	}

	private void getCategory(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			int id = MIdNoise.deNoiseIId(sid);
			Category cate = CategoryDAO.getInstance().getById(id);
			if (cate != null) {
				JsonObject ocate = new JsonObject();
				// ocate.set("id", cate.getId());
				ocate.set("name", cate.getName());
				ocate.set("desc", cate.getDescription());
				String status = "";
				if (cate.getStatus() == 0) {
					status = "off";
				} else {
					status = "on";
				}
				ocate.set("status", status);
				result.set("err", 0);
				result.set("cc", ocate);
				result.set("msg", "Get category successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get category fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void editCategory(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String seidcate = req.getParameter("eid");
		String sname = req.getParameter("ename");
		String sdesc = req.getParameter("edesc");
		String sstatus = req.getParameter("estatus");
		if (sname != null && !sname.isEmpty() && sdesc != null && !sdesc.isEmpty() && seidcate != null
				&& !seidcate.isEmpty()) {
			int idcate = MIdNoise.deNoiseIId(seidcate);
			int status = sstatus != null ? 1 : 0;
			Category cate = CategoryDAO.getInstance().getById(idcate);
			if (cate != null) {
				cate.setName(sname.trim());
				cate.setDescription(sdesc.trim());
				cate.setStatus(status);
				int err = CategoryDAO.getInstance().update(cate);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit category successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit category fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Category not exist.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void addCategory(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) {
		String sname = params.containsKey("name") ? params.get("name") : "";
		String sdesc = params.containsKey("desc") ? params.get("desc") : "";
		String sstatus = params.containsKey("status") ? params.get("status") : "";
		FileItem photo = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;

		if (sname != null && !sname.isEmpty() && sdesc != null && !sdesc.isEmpty() && sstatus != null && photo != null
				&& photo.getSize() > 0) {
			// save photo
			long priPhotoId = PhotoUtil.Instance.uploadPhoto(photo, PhotoType.COLOR);
			if (priPhotoId < 0) {
				result.set("err", -1);
				result.set("msg", "Can't insert photo");
				return;
			}
			// save product
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			Category cate = new Category(0, sname, sdesc, priPhotoId);
			cate.setStatus(status);
			int err = CategoryDAO.getInstance().insert(cate);
			if (err >= 0) {
				result.set("err", 0);
				result.set("msg", "Add category successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Add category fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		}
	}

	private void changeCategoryPhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String cateId = params.containsKey("iidp") ? params.get("iidp") : "";
				FileItem priphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
				if (cateId != null && !cateId.isEmpty() && priphoto != null && priphoto.getSize() > 0) {
					int pid = MIdNoise.deNoiseIId(cateId);
					Category cate = CategoryDAO.getInstance().getById(pid);
					if (cate != null) {
						// save image.
						long pId = PhotoUtil.Instance.uploadPhoto(priphoto, PhotoType.COLOR);
						if (pId > 0) {
							cate.setPhoto(pId);
							CategoryDAO.getInstance().update(cate);
							result.set("err", 0);
							result.set("msg", "Change photo successfully.");
							return;
						} else {
							result.set("err", -1);
							result.set("msg", "Can't insert photo");
							return;
						}
					} else {
						result.set("err", -1);
						result.set("msg", "Category is not exist.");
						return;
					}
				}
			}
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		} catch (Exception e) {
			logger.error("ColorHandler.changeCategoryPhoto: " + e, e);
		}
	}

	private void addColor(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) {
		String scode = params.containsKey("code") ? params.get("code") : "";
		String scate = params.containsKey("cate") ? params.get("cate") : "";
		String sstatus = params.containsKey("status") ? params.get("status") : "";
		String sisfeature = params.containsKey("isfeature") ? params.get("isfeature") : "";
		FileItem photo = mapFile.containsKey("photo") ? mapFile.get("photo") : null;

		if (scode != null && !scode.isEmpty() && scate != null && !scate.isEmpty() && sstatus != null
				&& sisfeature != null && photo != null && photo.getSize() > 0) {
			// save photo
			long priPhotoId = PhotoUtil.Instance.uploadPhoto(photo, PhotoType.COLOR);
			if (priPhotoId < 0) {
				result.set("err", -1);
				result.set("msg", "Can't insert photo");
				return;
			}
			// save color
			int cateId = MIdNoise.deNoiseIId(scate);
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			boolean isFeature = "on".equalsIgnoreCase(sisfeature);
			Color color = new Color(0, cateId, scode, priPhotoId, isFeature);
			color.setStatus(status);
			int err = ColorDAO.getInstance().insert(color);
			if (err >= 0) {
				result.set("err", 0);
				result.set("msg", "Add color successfully.");
				//notify
				
			} else {
				result.set("err", -1);
				result.set("msg", "Add color fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		}
	}

	private void getColor(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String spid = req.getParameter("id");
		if (spid != null && !spid.isEmpty()) {
			long pid = MIdNoise.deNoiseLId(spid);
			Color pro = ColorDAO.getInstance().getById(pid);
			if (pro != null) {
				JsonObject opro = new JsonObject();
				opro.set("code", pro.getCode());
				opro.set("cateid", MIdNoise.enNoiseIId(pro.getCategoryId()));
				Category cate = CategoryDAO.getInstance().getById(pro.getCategoryId());
				String cateName = cate != null ? cate.getName() : "";
				opro.set("namecate", cateName);
				opro.set("status", pro.getStatus() > 0 ? "on" : "off");
				opro.set("isfeature", pro.getIsFeature() ? "yes" : "no");
				result.set("err", 0);
				result.set("color", opro);
				result.set("msg", "Get color successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get color info fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void changeColorPhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String scolorId = params.containsKey("iidp") ? params.get("iidp") : "";
				FileItem priphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
				if (scolorId != null && !scolorId.isEmpty() && priphoto != null && priphoto.getSize() > 0) {
					long colorId = MIdNoise.deNoiseLId(scolorId);
					Color color = ColorDAO.getInstance().getById(colorId);
					if (color != null) {
						// save image.
						long pId = PhotoUtil.Instance.uploadPhoto(priphoto, PhotoType.COLOR);
						if (pId > 0) {
							color.setPhoto(pId);
							ColorDAO.getInstance().update(color);
							result.set("err", 0);
							result.set("msg", "Change photo successfully.");
							return;
						} else {
							result.set("err", -1);
							result.set("msg", "Can't insert photo");
							return;
						}
					} else {
						result.set("err", -1);
						result.set("msg", "Color is not exist.");
						return;
					}
				}
			}
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		} catch (Exception e) {
			logger.error("ColorHandler.changeCategoryPhoto: " + e, e);
		}
	}

	private void editProduct(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String seidp = req.getParameter("eid");
		String scode = req.getParameter("ecode");
		String scateid = req.getParameter("ecate");
		String sisfeature = req.getParameter("eisfeature");
		String sstatus = req.getParameter("estatus");
		if (seidp != null && !seidp.isEmpty() && scode != null && !scode.isEmpty() && scateid != null
				&& !scateid.isEmpty()) {
			int cateid = MIdNoise.deNoiseIId(scateid);
			int status = sstatus != null ? 1 : 0;
			boolean isFeature = sisfeature != null;
			long pid = MIdNoise.deNoiseLId(seidp);
			Color color = ColorDAO.getInstance().getById(pid);
			if (color != null) {
				color.setCode(scode);
				color.setCategoryId(cateid);
				color.setIsFeature(isFeature);
				color.setStatus(status);
				int err = ColorDAO.getInstance().update(color);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Color successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Color fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Color info fail.");
			}

		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
}
