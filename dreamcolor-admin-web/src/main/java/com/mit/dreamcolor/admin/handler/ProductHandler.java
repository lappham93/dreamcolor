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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.product.CategoryDAO;
import com.mit.dao.product.ProductDAO;
import com.mit.dreamcolor.admin.client.ProductPhotoClient;
import com.mit.dreamcolor.admin.common.Common;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.common.Paging;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.entities.product.Category;
import com.mit.entities.product.Product;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhotoResult;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 24, 2015
 */
public class ProductHandler extends BaseHandler {
	private static Logger logger = LoggerFactory.getLogger(ProductHandler.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			TemplateDataDictionary dic = getDictionary();

			// select template.
			String option = req.getParameter("option");
			if ("pro".equalsIgnoreCase(option)) {
				renderPageProduct(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "product.xtm", req));
			} else {
				renderPageCategory(dic, req, resp);
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "category.xtm", req));
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

			String action = req.getParameter("action");
			String callback = req.getParameter("callback");
			if (action != null && !action.isEmpty()) {
				if ("addcate".equalsIgnoreCase(action)) {
					addCategory(req, resp, result);
				} else if ("getcate".equalsIgnoreCase(action)) {
					getCategory(req, resp, result);
				} else if ("editcate".equalsIgnoreCase(action)) {
					editCategory(req, resp, result);
				} else if ("addpro".equalsIgnoreCase(action)) {
					addProduction(req, resp, result);
				} else if ("getpro".equalsIgnoreCase(action)) {
					getProduction(req, resp, result);
				} else if ("editpro".equalsIgnoreCase(action)) {
					editProduction(req, resp, result);
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

	private void renderPageCategory(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {

		List<Category> cates = CategoryDAO.getInstance().listAllIgnoreStatus();
		if (cates != null && !cates.isEmpty()) {
			Map<Integer, String> mapIN = new HashMap<Integer, String>();
			for (Category cate : cates) {
				mapIN.put(cate.getId(), cate.getName());
			}
			int i = 1;
			for (Category cate : cates) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("ID", MIdNoise.enNoiseIId(cate.getId()));
				loopRow.setVariable("NAME", cate.getName());
				int pid = cate.getParentId();
				if (pid > 0) {
					String nameParent = mapIN.containsKey(pid) ? mapIN.get(pid) : "";
					loopRow.setVariable("PATH", nameParent);
				} else {
					loopRow.setVariable("PATH", "ROOT");
				}
				loopRow.setVariable("POSITION", String.valueOf(cate.getPosition()));
				if (cate.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}

				TemplateDataDictionary loopOption = dic.addSection("loop_option");
				loopOption.setVariable("KEY", MIdNoise.enNoiseIId(cate.getId()));
				loopOption.setVariable("VALUE", cate.getName());
				i++;
			}
		} else {
			dic.addSection("table_empty");
		}
	}

	private void renderPageProduct(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) {

		// render selection.
		Map<Integer, String> mapCate = new HashMap<Integer, String>();
		List<Category> cates = CategoryDAO.getInstance().listAll();
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
		paging.totalRecords = ProductDAO.getInstance().totalAll();

		if (paging.totalRecords <= 0) {
			dic.addSection("empty");
			return;
		}

		int totalPages = paging.getTotalPages();
		paging.currPage = Paging.clamp(paging.currPage, 1, totalPages);
		int offset = (paging.currPage - 1) * paging.pageSize;
		boolean ascOrder = false;
		List<Product> listPro = ProductDAO.getInstance().getSlideAll(offset, paging.pageSize, "updateTime", ascOrder);

		if (listPro != null && !listPro.isEmpty()) {
			dic.addSection("HAS_TABLE");
			int i = offset + 1;
			for (Product pro : listPro) {
				TemplateDataDictionary loopRow = dic.addSection("loop_row");
				loopRow.setVariable("NO", String.valueOf(i));
				loopRow.setVariable("ID", MIdNoise.enNoiseLId(pro.getId()));
				loopRow.setVariable("MODEL", pro.getModel());
				loopRow.setVariable("MANU", pro.getManufacturer());
				String cate = !mapCate.isEmpty() && mapCate.containsKey(pro.getCategoryId())
						? mapCate.get(pro.getCategoryId()) : "";
				loopRow.setVariable("CATE", cate);
				loopRow.setVariable("UPDATE", Common.timeAgo(pro.getUpdateTime()));
				if (pro.getStatus() > 0) {
					loopRow.addSection("STATUS_ON");
				} else {
					loopRow.addSection("STATUS_OFF");
				}
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
		jsonPaging.set("action", "/web/admin/product");
		dic.setVariable("paging", jsonPaging.toString());
		dic.setVariable("keyword", "option=pro");
	}

	public String buildURIImg(long pid) {
		try {
			TMPhotoResult tmprs = ProductPhotoClient.getInstance().getMPhoto(pid);
			if (tmprs != null && tmprs.value != null) {
				byte[] dataImg = tmprs.value.getData();
				String filename = tmprs.value.getFilename();
				String ext = FilenameUtils.getExtension(filename);
				String pidn = MIdNoise.enNoiseLId(pid);
				String pathSaveImg = Common.swdirPDB + File.separator + "pt" + File.separator + pidn + "." + ext;
				String uriImg = Configuration.APP_STATIC_DOMAIN + "/pdb/pt/" + pidn + "." + ext;
				File img = new File(pathSaveImg);
				if (!img.exists()) {
					FileOutputStream fos = new FileOutputStream(img);
					fos.write(dataImg, 0, dataImg.length);
					fos.close();
				}
				return uriImg;
			}
		} catch (Exception e) {
		}
		return "";
	}

	private void addCategory(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String sname = req.getParameter("name");
		String spc = req.getParameter("pc");
		String sposition = req.getParameter("position");
		String sstatus = req.getParameter("status");
		if (sname != null && !sname.isEmpty() && spc != null && !spc.isEmpty() && sposition != null
				&& !sposition.isEmpty()) {
			int position = Integer.valueOf(sposition);
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			int pc = 0;
			String path = "";
			if (!"0".equalsIgnoreCase(spc)) {
				pc = MIdNoise.deNoiseIId(spc);
				Category catePar = CategoryDAO.getInstance().getById(pc);
				if (catePar != null) {
					path = getFullPath(catePar);
				} else {
					pc = 0;
				}
			}
			Category cate = new Category(0, sname, path, pc, position);
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
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void getCategory(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			int id = MIdNoise.deNoiseIId(sid);
			Category cate = CategoryDAO.getInstance().getById(id);
			if (cate != null) {
				JsonObject ocate = new JsonObject();
				ocate.set("id", cate.getId());
				ocate.set("name", cate.getName());
				int pid = cate.getParentId();
				if (pid > 0) {
					ocate.set("pcateid", MIdNoise.enNoiseIId(pid));
					Category pc = CategoryDAO.getInstance().getById(pid);
					if (pc != null) {
						ocate.set("pcate", pc.getName());
					} else {
						ocate.set("pcate", "");
					}
				} else {
					ocate.set("pcateid", "0");
					ocate.set("pcate", "ROOT");
				}
				ocate.set("post", cate.getPosition());
				String status = "";
				if (cate.getStatus() == 0) {
					status = "off";
				} else {
					status = "on";
				}
				ocate.set("status", status);
				result.set("err", 0);
				result.set("cate", ocate);
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
		String seidcate = req.getParameter("eidcate");
		String sname = req.getParameter("ename");
		String spc = req.getParameter("epc");
		String sposition = req.getParameter("eposition");
		String sstatus = req.getParameter("estatus");
		// System.out.println("sstatus: " + sstatus);
		if (sname != null && !sname.isEmpty() && spc != null && !spc.isEmpty() && sposition != null
				&& !sposition.isEmpty() && seidcate != null && !seidcate.isEmpty()) {
			int idcate = MIdNoise.deNoiseIId(seidcate);
			int position = Integer.valueOf(sposition);
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			int pc = 0;
			String path = "";
			if (!"0".equalsIgnoreCase(spc)) {
				pc = MIdNoise.deNoiseIId(spc);
				Category catePar = CategoryDAO.getInstance().getById(pc);
				if (catePar != null) {
					path = getFullPath(catePar);
				} else {
					pc = 0;
				}
			}
			Category cate = CategoryDAO.getInstance().getById(idcate);
			if (cate != null) {
				cate.setName(sname);
				cate.setPosition(position);
				cate.setStatus(status);
				cate.setParentId(pc);
				cate.setPath(path);
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

	private void addProduction(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String smodel = req.getParameter("model");
		String smanu = req.getParameter("manu");
		String scateid = req.getParameter("cateid");
		String sstatus = req.getParameter("status");
		// System.out.println("sstatus: " + sstatus);
		if (smodel != null && !smodel.isEmpty() && scateid != null && !scateid.isEmpty() && smanu != null
				&& !smanu.isEmpty()) {
			int cateid = 0;
			if (!"0".equalsIgnoreCase(scateid)) {
				cateid = MIdNoise.deNoiseIId(scateid);
			}
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			Product pro = new Product();
			pro.setId(-1);
			pro.setModel(smodel);
			pro.setManufacturer(smanu);
			pro.setCategoryId(cateid);
			pro.setStatus(status);
			pro.setCreateTime(System.currentTimeMillis());
			pro.setUpdateTime(System.currentTimeMillis());
			int err = ProductDAO.getInstance().insert(pro);
			if (err >= 0) {
				result.set("err", 0);
				result.set("msg", "Add Product successfully.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void getProduction(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String spid = req.getParameter("id");
		if (spid != null && !spid.isEmpty()) {
			long pid = MIdNoise.deNoiseLId(spid);
			Product pro = ProductDAO.getInstance().getById(pid);
			if (pro != null) {
				JsonObject opro = new JsonObject();
				opro.set("id", pro.getId());
				opro.set("model", pro.getModel());
				opro.set("manu", pro.getManufacturer());
				opro.set("cateid", MIdNoise.enNoiseIId(pro.getCategoryId()));
				Category cate = CategoryDAO.getInstance().getById(pro.getCategoryId());
				if (cate != null) {
					opro.set("namecate", cate.getName());
				} else {
					opro.set("namecate", "");
				}
				if (pro.getStatus() == 0) {
					opro.set("status", "off");
				} else {
					opro.set("status", "on");
				}
				result.set("err", 0);
				result.set("pro", opro);
				result.set("msg", "Get SKU successfully.");
			} else {
				result.set("err", -1);
				result.set("msg", "Get product info fail.");
			}
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	private void editProduction(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String seidp = req.getParameter("eidp");
		String smodel = req.getParameter("emodel");
		String smanu = req.getParameter("emanu");
		String scateid = req.getParameter("ecateid");
		String sstatus = req.getParameter("estatus");
		// System.out.println("sstatus: " + sstatus);
		if (smodel != null && !smodel.isEmpty() && scateid != null && !scateid.isEmpty() && smanu != null
				&& !smanu.isEmpty() && seidp != null && !seidp.isEmpty()) {
			int cateid = 0;
			if (!"0".equalsIgnoreCase(scateid)) {
				cateid = MIdNoise.deNoiseIId(scateid);
			}
			int status = "on".equalsIgnoreCase(sstatus) ? 1 : 0;
			long pid = MIdNoise.deNoiseLId(seidp);
			Product pro = ProductDAO.getInstance().getById(pid);
			if (pro != null) {
				pro.setModel(smodel);
				pro.setManufacturer(smanu);
				pro.setCategoryId(cateid);
				pro.setStatus(status);
				int err = ProductDAO.getInstance().update(pro);
				if (err >= 0) {
					result.set("err", 0);
					result.set("msg", "Edit Product successfully.");
				} else {
					result.set("err", -1);
					result.set("msg", "Edit Product fail.");
				}
			} else {
				result.set("err", -1);
				result.set("msg", "Get Product info fail.");
			}

		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}

	public String getFullPath(Category category) {
		String path = null;

		if (category != null) {
			String prefix;
			if (category.getPath() == null || category.getPath().isEmpty()) {
				prefix = ",";
			} else {
				prefix = category.getPath();
			}
			path = prefix + category.getId() + ",";
		}

		return path;
	}

	public static void main(String[] args) {

	}
}
