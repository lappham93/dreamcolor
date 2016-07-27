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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.distributor.DistributorDAO;
import com.mit.dao.video.VideoDAO;
import com.mit.dreamcolor.admin.common.Common;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.common.Paging;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.dreamcolor.admin.utils.UploadFormUtil;
import com.mit.entities.distributor.Distributor;
import com.mit.entities.photo.PhotoType;
import com.mit.entities.video.Video;
import com.mit.midutil.MIdNoise;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class DistributorHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(DistributorHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();
			renderDistributor(dic, req, resp);
			dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "distributor.xtm", req));

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
						if ("adddist".equalsIgnoreCase(action)) {
							addDistributor(req, resp, result, mapFile, params);
						} else if ("changethumb".equalsIgnoreCase(action)) {
							changeThumb(req, resp, result, mapFile, params);
						}
					}
				}
			} else {
				action = req.getParameter("action");
				callback = req.getParameter("callback");
				if (action != null && !action.isEmpty()) {
					if ("getdist".equalsIgnoreCase(action)) {
						getDistributor(req, resp, result);
					} else if ("editdist".equalsIgnoreCase(action)) {
						editDistributor(req, resp, result);
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

	private void renderDistributor(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException {
		Paging paging = new Paging();
		String spage = req.getParameter("page");
		spage = spage != null && !spage.isEmpty() ? spage : "1";
		int page = Integer.parseInt(spage);
		paging.currPage = page;
		paging.pageSize = Configuration.APP_PAGING_PAGE_SIZE;
		paging.numDisplay = Configuration.APP_PAGING_NUM_DISPLAY;
		paging.totalRecords = DistributorDAO.getInstance().totalAllIgnoreStatus();

		if (paging.totalRecords <= 0) {
			dic.addSection("table_empty");
			return;
		}

		int totalPages = paging.getTotalPages();
		paging.currPage = Paging.clamp(paging.currPage, 1, totalPages);
		int offset = (paging.currPage - 1) * paging.pageSize;
		List<Distributor> dists = DistributorDAO.getInstance().getSliceIgnoreStatus("", "", offset, paging.pageSize, "updateTime", false);
		if (dists != null && !dists.isEmpty()) {
			dic.addSection("HAS_TABLE");
			int i = offset + 1;
			for (Distributor dist : dists) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("ID", MIdNoise.enNoiseIId(dist.getId()));
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("NAME", dist.getName());
				
				loopRow.setVariable("CONTACT_NAME", dist.getContactName());
				loopRow.setVariable("ADDRESS1", dist.getAddressLine1());
				loopRow.setVariable("ADDRESS2", dist.getAddressLine2());
				loopRow.setVariable("CITY", dist.getCity());
				loopRow.setVariable("STATE", dist.getState());
				loopRow.setVariable("COUNTRY", dist.getCountry());
				loopRow.setVariable("CCODE", dist.getCountryCode());
				loopRow.setVariable("ZCODE", dist.getZipCode());
				loopRow.setVariable("PHONE", dist.getPhone());
				
				loopRow.setVariable("WEB", URLDecoder.decode(dist.getWebLink(), "UTF-8"));
				loopRow.setVariable("UPDATE", Common.timeAgo(dist.getUpdateTime()));
				if (dist.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}
				// photo
				String priPhoto = PhotoUtil.Instance.buildURIImg(dist.getPhoto(), PhotoType.DISTRIBUTOR);
				loopRow.setVariable("URI_DP", priPhoto);
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
		jsonPaging.set("action", "/web/admin/distributor");
		dic.setVariable("paging", jsonPaging.toString());
		dic.setVariable("keyword", "");
	}

	private void addDistributor(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String sname = params.containsKey("name") ? params.get("name") : "";
				String scname = params.containsKey("cname") ? params.get("cname") : "";
				String sadd1 = params.containsKey("add1") ? params.get("add1") : "";
				String sadd2 = params.containsKey("add2") ? params.get("add2") : "";
				String scity = params.containsKey("city") ? params.get("city") : "";
				String sstate = params.containsKey("state") ? params.get("state") : "";
				String scountry = params.containsKey("country") ? params.get("country") : "";
				String sccode = params.containsKey("ccode") ? params.get("ccode") : "";
				String szcode = params.containsKey("zcode") ? params.get("zcode") : "";
				String sphone = params.containsKey("phone") ? params.get("phone") : "";
				String sweb = params.containsKey("web") ? params.get("web") : "";
				String sstatus = params.containsKey("status") ? params.get("status") : "";
				FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
				if (sname != null && !sname.isEmpty() && sadd1 != null && !sadd1.isEmpty() && scity != null && !scity.isEmpty()
						&& sstate != null && !sstate.isEmpty() && scountry != null && !scountry.isEmpty() && sphone != null 
						&& !sphone.isEmpty() && thumb != null && thumb.getSize() > 0) {
					// save primary photo
					long priPhotoId = PhotoUtil.Instance.uploadPhoto(thumb, PhotoType.DISTRIBUTOR);
					if (priPhotoId < 0) {
						result.set("err", -1);
						result.set("msg", "Can't insert photo");
						return;
					}
					// save distributor
					int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
					String url = URLEncoder.encode(sweb, "UTF-8");
					Distributor dist = new Distributor(0, sname, scname, sadd1, sadd2, scity, sstate, scountry, szcode, sphone, priPhotoId, url, sccode);
					dist.setStatus(status);
					if (DistributorDAO.getInstance().insert(dist) >= 0) {
						result.set("err", 0);
						result.set("msg", "Add distributor successfully.");
					} else {
						result.set("err", -1);
						result.set("msg", "Add distributor fail.");
					}
				} else {
					result.set("err", -1);
					result.set("msg", "Parameter invalid.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Parameter invalid.");
			}
		} catch (Exception e) {
			logger.error("DistributorHandler.addDistributor: " + e, e);
		}
	}

	private void editDistributor(HttpServletRequest req, HttpServletResponse resp, JsonObject result)
			throws UnsupportedEncodingException {
		String seidp = req.getParameter("eid");
		String sname = req.getParameter("ename");
		String scname = req.getParameter("ecname");
		String sadd1 = req.getParameter("eadd1");
		String sadd2 = req.getParameter("eadd2");
		String scity = req.getParameter("ecity");
		String sstate = req.getParameter("estate");
		String scountry = req.getParameter("ecountry");
		String sccode = req.getParameter("eccode");
		String szcode = req.getParameter("ezcode");
		String sphone = req.getParameter("ephone");
		String sweb = req.getParameter("eweb");
		String sstatus = req.getParameter("estatus");
		if (sname != null && !sname.isEmpty() && sadd1 != null && !sadd1.isEmpty() && scity != null && !scity.isEmpty()
				&& sstate != null && !sstate.isEmpty() && scountry != null && !scountry.isEmpty() && sphone != null 
				&& !sphone.isEmpty() && seidp != null && !seidp.isEmpty()) {
			int pid = MIdNoise.deNoiseIId(seidp);
			Distributor dist = DistributorDAO.getInstance().getById(pid);
			if (dist != null) {
				int status = sstatus != null ? 1 : 0;
				dist.setName(sname);
				dist.setAddressLine1(sadd1);
				dist.setAddressLine2(sadd2);
				dist.setContactName(scname);
				dist.setCity(scity);
				dist.setState(sstate);
				dist.setCountry(scountry);
				dist.setCountryCode(sccode);
				dist.setZipCode(szcode);
				dist.setPhone(sphone);
				dist.setWebLink(URLEncoder.encode(sweb, "UTF-8"));
				dist.setStatus(status);
				int err = DistributorDAO.getInstance().update(dist);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Distributor successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Distributor fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Distributor info fail.");
			}

		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void changeThumb(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
				FileItem priphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
				if (siidp != null && !siidp.isEmpty() && priphoto != null && priphoto.getSize() > 0) {
					int distId = MIdNoise.deNoiseIId(siidp);
					Distributor dist = DistributorDAO.getInstance().getById(distId);
					if (dist != null) {
						// save image.
						long pId = PhotoUtil.Instance.uploadPhoto(priphoto, PhotoType.DISTRIBUTOR);
						if (pId > 0) {
							dist.setPhoto(pId);
							DistributorDAO.getInstance().update(dist);
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
						result.set("msg", "Video is not exist.");
						return;
					}
				}
			}
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		} catch (Exception e) {
			logger.error("DistributorHandler.changeThumb: " + e, e);
		}
	}

	private void getDistributor(HttpServletRequest req, HttpServletResponse resp, JsonObject result)
			throws UnsupportedEncodingException {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			int id = MIdNoise.deNoiseIId(sid);
			Distributor dist = DistributorDAO.getInstance().getById(id);
			if (dist != null) {
				JsonObject owb = new JsonObject();
				owb.set("id", sid);
				owb.set("name", dist.getName());
				owb.set("cname", dist.getContactName());
				owb.set("add1", dist.getAddressLine1());
				owb.set("add2", dist.getAddressLine2());
				owb.set("city", dist.getCity());
				owb.set("state", dist.getState());
				owb.set("country", dist.getCountry());
				owb.set("ccode", dist.getCountryCode());
				owb.set("zcode", dist.getZipCode());
				owb.set("phone", dist.getPhone());
				owb.set("web", URLDecoder.decode(dist.getWebLink(), "UTF-8"));
				owb.set("status", dist.getStatus() > 0 ? "on" : "off");
				result.set("err", 0);
				result.set("dist", owb);
				result.set("msg", "Get Distributor successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get Distributor fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
}
