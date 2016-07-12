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
import com.mit.dao.video.VideoDAO;
import com.mit.dreamcolor.admin.common.Common;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.common.Paging;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.dreamcolor.admin.utils.UploadFormUtil;
import com.mit.entities.photo.PhotoType;
import com.mit.entities.video.Video;
import com.mit.midutil.MIdNoise;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class VideoHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(VideoHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();
			renderVideo(dic, req, resp);
			dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "video.xtm", req));

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
						if ("addvideo".equalsIgnoreCase(action)) {
							addVideo(req, resp, result, mapFile, params);
						} else if ("changethumb".equalsIgnoreCase(action)) {
							changeThumb(req, resp, result, mapFile, params);
						}
					}
				}
			} else {
				action = req.getParameter("action");
				callback = req.getParameter("callback");
				if (action != null && !action.isEmpty()) {
					if ("getvideo".equalsIgnoreCase(action)) {
						getVideo(req, resp, result);
					} else if ("editvideo".equalsIgnoreCase(action)) {
						editVideo(req, resp, result);
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

	private void renderVideo(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException {
		Paging paging = new Paging();
		String spage = req.getParameter("page");
		spage = spage != null && !spage.isEmpty() ? spage : "1";
		int page = Integer.parseInt(spage);
		paging.currPage = page;
		paging.pageSize = Configuration.APP_PAGING_PAGE_SIZE;
		paging.numDisplay = Configuration.APP_PAGING_NUM_DISPLAY;
		paging.totalRecords = VideoDAO.getInstance().totalAllIgnoreStatus();

		if (paging.totalRecords <= 0) {
			dic.addSection("empty");
			return;
		}

		int totalPages = paging.getTotalPages();
		paging.currPage = Paging.clamp(paging.currPage, 1, totalPages);
		int offset = (paging.currPage - 1) * paging.pageSize;
		List<Video> videos = VideoDAO.getInstance().getSliceIgnoreStatus(paging.pageSize, offset, "updateTime", false);

		if (videos != null && !videos.isEmpty()) {
			dic.addSection("HAS_TABLE");
			int i = offset + 1;
			for (Video video : videos) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("ID", MIdNoise.enNoiseLId(video.getId()));
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("URL", URLDecoder.decode(video.getLink(), "UTF-8"));
				loopRow.setVariable("TITLE", video.getTitle());
				loopRow.setVariable("DESC", video.getDesc());
				loopRow.setVariable("VIEWS", String.valueOf(video.getViews()));
				loopRow.setVariable("UPDATE", Common.timeAgo(video.getUpdateTime()));
				if (video.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}
				// photo
				String priPhoto = PhotoUtil.Instance.buildURIImg(video.getThumbnailNum(), PhotoType.VIDEO_THUMBNAIL);
				loopRow.setVariable("URI_VP", priPhoto);
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
		jsonPaging.set("action", "/web/admin/video");
		dic.setVariable("paging", jsonPaging.toString());
		dic.setVariable("keyword", "");
	}

	private void addVideo(HttpServletRequest req, HttpServletResponse resp, JsonObject result,
			Map<String, FileItem> mapFile, Map<String, String> params) throws IOException {
		try {
			if (params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()) {
				String surl = params.containsKey("link") ? params.get("link") : "";
				String stitle = params.containsKey("title") ? params.get("title") : "";
				String sdesc = params.containsKey("desc") ? params.get("desc") : "";
				String sstatus = params.containsKey("status") ? params.get("status") : "";
				FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
				if (surl != null && !surl.isEmpty() && stitle != null && !stitle.isEmpty() && sdesc != null
						&& !sdesc.isEmpty() && thumb != null && thumb.getSize() > 0) {
					// save primary photo
					long priPhotoId = PhotoUtil.Instance.uploadPhoto(thumb, PhotoType.VIDEO_THUMBNAIL);
					if (priPhotoId < 0) {
						result.set("err", -1);
						result.set("msg", "Can't insert photo");
						return;
					}
					// save banner
					int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
					String url = URLEncoder.encode(surl, "UTF-8");
					Video video = new Video(0, url, "", stitle, sdesc, priPhotoId);
					video.setStatus(status);
					if (VideoDAO.getInstance().insert(video) >= 0) {
						result.set("err", 0);
						result.set("msg", "Add video successfully.");
					} else {
						result.set("err", -1);
						result.set("msg", "Add video fail.");
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
			logger.error("VideoHandler.addVideo: " + e, e);
		}
	}

	private void editVideo(HttpServletRequest req, HttpServletResponse resp, JsonObject result)
			throws UnsupportedEncodingException {
		String seidp = req.getParameter("eid");
		String surl = req.getParameter("elink");
		String stitle = req.getParameter("etitle");
		String sdesc = req.getParameter("edesc");
		String sstatus = req.getParameter("estatus");
		if (seidp != null && !seidp.isEmpty() && surl != null && !surl.isEmpty() && stitle != null && !stitle.isEmpty()
				&& sdesc != null && !sdesc.isEmpty()) {
			long pid = MIdNoise.deNoiseLId(seidp);
			Video pro = VideoDAO.getInstance().getById(pid);
			if (pro != null) {
				String url = URLEncoder.encode(surl, "UTF-8");
				int status = sstatus != null ? 1 : 0;
				pro.setLink(url);
				pro.setTitle(stitle);
				pro.setDesc(sdesc);
				pro.setStatus(status);
				int err = VideoDAO.getInstance().update(pro);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Video successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Video fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Video info fail.");
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
					long videoId = MIdNoise.deNoiseLId(siidp);
					Video video = VideoDAO.getInstance().getById(videoId);
					if (video != null) {
						// save image.
						long pId = PhotoUtil.Instance.uploadPhoto(priphoto, PhotoType.VIDEO_THUMBNAIL);
						if (pId > 0) {
							video.setThumbnail(pId);
							VideoDAO.getInstance().update(video);
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
			logger.error("VideoHandler.changeThumb: " + e, e);
		}
	}

	private void getVideo(HttpServletRequest req, HttpServletResponse resp, JsonObject result)
			throws UnsupportedEncodingException {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			long id = MIdNoise.deNoiseLId(sid);
			Video video = VideoDAO.getInstance().getById(id);
			if (video != null) {
				JsonObject owb = new JsonObject();
				owb.set("id", sid);
				owb.set("link", URLDecoder.decode(video.getLink(), "UTF-8"));
				owb.set("title", video.getTitle());
				owb.set("desc", video.getDesc());
				owb.set("status", video.getStatus() > 0 ? "on" : "off");
				result.set("err", 0);
				result.set("video", owb);
				result.set("msg", "Get Video successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get Video fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
}
