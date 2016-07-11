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
import com.mit.dao.banner.BannerDAO;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.dreamcolor.admin.utils.UploadFormUtil;
import com.mit.entities.banner.Banner;
import com.mit.entities.banner.BannerType;
import com.mit.entities.banner.VideoBanner;
import com.mit.entities.banner.WebBanner;
import com.mit.entities.banner.WelcomeBanner;
import com.mit.entities.photo.PhotoType;
import com.mit.midutil.MIdNoise;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class BannerHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(BannerHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();

			// select template.
			String option = req.getParameter("option");
			if ("bwel".equalsIgnoreCase(option)) {
				renderBannerWelcome(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerwelcome.xtm", req));
			} else {
				renderBannerWeb(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerweb.xtm", req));
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
						if ("addbweb".equalsIgnoreCase(action)) {
							addBannerWeb(req, resp, result, mapFile, params);
						} else if ("addbwel".equalsIgnoreCase(action)) {
							addBannerWelcome(req, resp, result, mapFile, params);
						} else if ("cimg".equalsIgnoreCase(action)) {
							changePhoto(req, resp, result, mapFile, params);
						}
					}
				}
			} else {
				action = req.getParameter("action");
				callback = req.getParameter("callback");
				if (action != null && !action.isEmpty()) {
					if ("getbanner".equalsIgnoreCase(action)) {
						getBanner(req, resp, result);
					} else if ("editwb".equalsIgnoreCase(action)) {
						editBannerWeb(req, resp, result);
					} else if ("editwel".equalsIgnoreCase(action)) {
						editBannerWelcome(req, resp, result);
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

	private void renderBannerWeb(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException {
		List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.WEB.getValue());
		if (listB != null && !listB.isEmpty()) {
			int i = 1;
			for (Banner b : listB) {
				if (b.getType() == BannerType.WEB.getValue()) {
					WebBanner wb = (WebBanner) b;
					// System.out.println("wb: " + wb);
					TemplateDataDictionary loopRow = dic.addSection("loop_row");
					loopRow.setVariable("NO", String.valueOf(i));
					loopRow.setVariable("ID", MIdNoise.enNoiseLId(wb.getuId()));
					String uripri = PhotoUtil.Instance.buildURIImg(wb.getThumb(), PhotoType.BANNER);
					uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
					loopRow.setVariable("URI_TP", uripri);
					loopRow.setVariable("URL", URLDecoder.decode(wb.getId(), "UTF-8"));
					loopRow.setVariable("MSG", wb.getMsg());
					if (wb.getStatus() > 0) {
						loopRow.addSection("STATUS_ON");
					} else {
						loopRow.addSection("STATUS_OFF");
					}

					i++;
				}
			}
		} else {
			dic.addSection("table_empty");
		}

	}
	
	private void renderBannerWelcome(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException {
		List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.WELCOME.getValue());
		if (listB != null && !listB.isEmpty()) {
			int i = 1;
			for (Banner b : listB) {
				if (b.getType() == BannerType.WELCOME.getValue()) {
					WelcomeBanner wb = (WelcomeBanner) b;
					// System.out.println("wb: " + wb);
					TemplateDataDictionary loopRow = dic.addSection("loop_row");
					loopRow.setVariable("NO", String.valueOf(i));
					loopRow.setVariable("ID", MIdNoise.enNoiseLId(wb.getuId()));
					String uripri = PhotoUtil.Instance.buildURIImg(wb.getThumb(), PhotoType.BANNER);
					uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
					loopRow.setVariable("URI_TP", uripri);
					loopRow.setVariable("MSG", wb.getMsg());
					if (wb.getStatus() > 0) {
						loopRow.addSection("STATUS_ON");
					} else {
						loopRow.addSection("STATUS_OFF");
					}

					i++;
				}
			}
		} else {
			dic.addSection("table_empty");
		}

	}

	private void addBannerWeb(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String surl = params.containsKey("url") ? params.get("url") : "";
				String smessage = params.containsKey("message") ? params.get("message") : "";
				String sstatus = params.containsKey("status") ? params.get("status") : "";
				FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
				if (surl != null && !surl.isEmpty() && smessage != null && !smessage.isEmpty() && thumb != null
						&& thumb.getSize() > 0) {
					// save primary photo
					long priPhotoId = PhotoUtil.Instance.uploadPhoto(thumb, PhotoType.BANNER);
					if (priPhotoId < 0) {
						result.set("err", -1);
						result.set("msg", "Can't insert primary photo");
						return;
					}
					// save banner
					int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
					String url = URLEncoder.encode(surl, "UTF-8");
					WebBanner wb = new WebBanner(0, url, smessage, priPhotoId, status);
					if (BannerDAO.getInstance().insert(wb) >= 0) {
						result.set("err", 0);
						result.set("msg", "Add banner web successfully.");
					} else {
						result.set("err", -1);
						result.set("msg", "Add banner web fail.");
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
			logger.error("BannerHandler.addBannerWeb: " + e, e);
		}
	}
	
	private void addBannerWelcome(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String smessage = params.containsKey("message") ? params.get("message") : "";
				String sstatus = params.containsKey("status") ? params.get("status") : "";
				FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
				if (smessage != null && !smessage.isEmpty() && thumb != null
						&& thumb.getSize() > 0) {
					// save primary photo
					long priPhotoId = PhotoUtil.Instance.uploadPhoto(thumb, PhotoType.BANNER);
					if (priPhotoId < 0) {
						result.set("err", -1);
						result.set("msg", "Can't insert primary photo");
						return;
					}
					// save banner
					int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
					WelcomeBanner wb = new WelcomeBanner(0, smessage, priPhotoId, status);
					if (BannerDAO.getInstance().insert(wb) >= 0) {
						result.set("err", 0);
						result.set("msg", "Add banner welcome successfully.");
					} else {
						result.set("err", -1);
						result.set("msg", "Add banner welcome fail.");
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
			logger.error("BannerHandler.addBannerWeb: " + e, e);
		}
	}


	private void editBannerWeb(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String seidp = req.getParameter("eidp");
		String surl = req.getParameter("eurl");
		String smsg = req.getParameter("emsg");
		String sstatus = req.getParameter("estatus");
		if (seidp != null && !seidp.isEmpty() && surl != null && !surl.isEmpty() && smsg != null && !smsg.isEmpty()) {
			int status = sstatus != null ? 1 : 0;
			long pid = MIdNoise.deNoiseLId(seidp);
			WebBanner pro = (WebBanner) BannerDAO.getInstance().getById(pid);
			if (pro != null) {
				pro.setMsg(smsg);
				pro.setId(surl);
				pro.setStatus(status);
				int err = BannerDAO.getInstance().update(pro);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Banner successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Banner fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Banner info fail.");
			}

		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
	
	private void editBannerWelcome(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String seidp = req.getParameter("eidp");
		String smsg = req.getParameter("emsg");
		String sstatus = req.getParameter("estatus");
		if (seidp != null && !seidp.isEmpty() && smsg != null && !smsg.isEmpty()) {
			int status = sstatus != null ? 1 : 0;
			long pid = MIdNoise.deNoiseLId(seidp);
			WelcomeBanner pro = (WelcomeBanner) BannerDAO.getInstance().getById(pid);
			if (pro != null) {
				pro.setMsg(smsg);
				pro.setStatus(status);
				int err = BannerDAO.getInstance().update(pro);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Banner successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Banner fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Banner info fail.");
			}

		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void changePhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
				FileItem priphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
				if (siidp != null && !siidp.isEmpty() && priphoto != null && priphoto.getSize() > 0) {
					long bannerId = MIdNoise.deNoiseLId(siidp);
					Banner banner = BannerDAO.getInstance().getById(bannerId);
					if (banner != null) {
						// save image.
						long pId = PhotoUtil.Instance.uploadPhoto(priphoto, PhotoType.BANNER);
						if (pId > 0) {
							if (banner instanceof WebBanner) {
								WebBanner wb = (WebBanner) banner;
								wb.setThumb(pId);
							} else if (banner instanceof VideoBanner) {
								VideoBanner vb = (VideoBanner) banner;
								vb.setThumb(pId);
							} else if (banner instanceof WelcomeBanner) {
								WelcomeBanner wb = (WelcomeBanner) banner;
								wb.setThumb(pId);
							}
							BannerDAO.getInstance().update(banner);
							result.set("err", 0);
							result.set("msg", "Change photo successfully.");
							return;
						} else {
							result.set("err", -1);
							result.set("msg", "Can't insert primary photo");
							return;
						}
					} else {
						result.set("err", -1);
						result.set("msg", "Banner is not exist.");
						return;
					}
				}
			}
			result.set("err", -1);
			result.set("msg", "Parameter invalid.");
		} catch (Exception e) {
			logger.error("BannerHandler.changePrimaryPhoto: " + e, e);
		}
	}

	private void getBanner(HttpServletRequest req, HttpServletResponse resp, JsonObject result)
			throws UnsupportedEncodingException {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			long id = MIdNoise.deNoiseLId(sid);
			Banner banner = BannerDAO.getInstance().getById(id);
			if (banner != null) {
				JsonObject owb = new JsonObject();
				owb.set("id", sid);
				owb.set("status", banner.getStatus() > 0 ? "on" : "off");
				owb.set("idstatus", banner.getStatus());
				if (banner instanceof WebBanner) {
					WebBanner wb = (WebBanner) banner;
					owb.set("url", URLDecoder.decode(wb.getId(), "UTF-8"));
					owb.set("msg", wb.getMsg());
				} else if (banner instanceof VideoBanner) {
					VideoBanner vb = (VideoBanner) banner;
					owb.set("msg", vb.getMsg());
				} else if (banner instanceof WelcomeBanner) {
					WelcomeBanner wb = (WelcomeBanner) banner;
					owb.set("msg", wb.getMsg());
				}

				result.set("err", 0);
				result.set("banner", owb);
				result.set("msg", "Get Banner successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get Banner fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
}
