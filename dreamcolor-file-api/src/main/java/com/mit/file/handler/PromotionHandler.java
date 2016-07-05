package com.mit.file.handler;

import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.mit.dao.biz.BizInfoDAO;
import com.mit.dao.promotion.PromotionTargetDAO;
import com.mit.dao.service.ServiceMenuDAO;
import com.mit.entities.biz.BizInfo;
import com.mit.entities.promotion.Promotion;
import com.mit.entities.promotion.PromotionContent;
import com.mit.entities.promotion.PromotionTypeEnum;
import com.mit.entities.service.ServiceMenu;
import com.mit.html.HapaxTemplate;
import com.mit.promotion.utils.PromotionNoise;
import com.mit.qr.thrift.QRItem;
import com.mit.qr.thrift.QRType;
import com.mit.qr.thrift.wrapper.QRWriteServiceClient;
import com.mit.utils.ConfigUtils;
import com.mit.utils.JsonUtils;

public class PromotionHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(PromotionHandler.class);

	private static final String _facebookDescription = "For services: {} at {}. Register NOW with shop code: {} to get the promotion!";
	private static final int FACEBOOK_MAX_SERVICE_SHOW = 3;
	private static final String _defaultAvatar = ConfigUtils.getConfig().getString("avatar.default");
	private static final String _staticDomain = ConfigUtils.getConfig().getString("domain.static");
	private static final String _staticUri = ConfigUtils.getConfig().getString("uri.static");
	private static final int _staticUriLen = _staticUri.split("/").length;
	private static final String _publicDomain = ConfigUtils.getConfig().getString("domain.public");

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/img/promo/share/";
		if (target.startsWith(uri)) {
			String[] targetSplit = target.replace(uri, "").split("/");

			String id = null;
			if (targetSplit.length > 0) {
				id = targetSplit[0];
			}

			try {
				if (id != null && !id.trim().isEmpty()) {
					int promotionId = PromotionNoise.decrypt(id);
					Promotion promotion = PromotionTargetDAO.getInstance()
							.getById(promotionId);
					if (promotion != null) {
						TemplateDataDictionary template = TemplateDictionary
								.create();
						template.addSection("CONTENT");
						PromotionContent content = JsonUtils.Instance
								.getObject(PromotionContent.class,
										promotion.getContent());

						BizInfo biz = BizInfoDAO.getInstance().getById(promotion.getBizId());
						if (content != null && biz != null) {
							String title = PromotionTypeEnum.find(content.getType()).name() + " " + content.getDiscount();
							String briefSrvNames;
							String fullSrvNames;
							List<Integer> srvIds = content.getServices();
							if (srvIds.size() == 0) {
								briefSrvNames = ServiceMenu.ANY_SERVICE;
								fullSrvNames = ServiceMenu.ANY_SERVICE;
							} else {
								Map<Integer, String> serviceNames = ServiceMenuDAO.getInstance().getNameByListId(srvIds);
								List<String> srvNames = new ArrayList<String>(serviceNames.values());
								briefSrvNames = org.apache.commons.lang.StringUtils.join(srvNames.subList(0, Math.min(srvNames.size(), FACEBOOK_MAX_SERVICE_SHOW)), ", ");
								if (serviceNames.size() > FACEBOOK_MAX_SERVICE_SHOW) {
									briefSrvNames += ", etc. ";
								}
								fullSrvNames = org.apache.commons.lang.StringUtils.join(srvNames, ", ");
							}
							template.setVariable("TITLE", title);
							template.setVariable("DESCRIPTION", MessageFormatter.arrayFormat(_facebookDescription, new Object[] {briefSrvNames, biz.getName(), biz.getBizCode() }).getMessage());

							template.setVariable("PROMO_TITLE", content.getTitle());
							template.setVariable("PROMO_DESC", content.getDesc());
							template.setVariable("PROMO_ID", id);

							PromotionTypeEnum promoType = PromotionTypeEnum.find(content.getType());
							template.setVariable("PROMO_CONTENT", promoType.name() + ": " + content.getDiscount());
							template.setVariable("PROMO_SERVICE", "For service: " + fullSrvNames);

							template.setVariable("SHOP_NAME", biz.getName());
							template.setVariable("SHOP_ADDR", biz.getAddress() + " " + biz.getCity() + ", " + biz.getState() + " " + biz.getZipCode() + ", " + biz.getCountry());
							template.setVariable("SHOPCODE", biz.getBizCode());
							template.setVariable("SHOP_PHONE", biz.getPhone());
						}


						String url = _publicDomain + target;
						template.setVariable("URL", url);

						String qrData = url;//_staticDomain + "/loop/img/promo/share/" + id;
//						String prTemplateFile = "";
//						if (promotion.getTemplateId() > 0) {
//							PromotionTemplate prTemplate = PromotionTemplateDAO.getInstance().getById(promotion.getTemplateId());
//							if(prTemplate != null) {
//								prTemplateFile = "1440/" + prTemplate.getImage();
//							}
//						}

						QRItem item = new QRItem("", promotionId, "", QRType.PROMOTION_SHARE, qrData, promotion.getTemplateId() + "", biz.getId()  + "/" + biz.getAvtVer());
						String qrId = QRWriteServiceClient.Instance.genShopQR(item);
						String image = _staticDomain + "/loop/img/qrcode/" + qrId;
						template.setVariable("IMAGE", image);

						Template tmp = HapaxTemplate.Loader
								.getTemplate("promotion/promotion.xtm");
						String html = tmp.renderToString(template);
						printHtml(request, response, html);
					} else {
						response.setStatus(HttpStatus.FORBIDDEN_403);
					}
				} else {
					response.setStatus(HttpStatus.FORBIDDEN_403);
				}
			} catch (Exception e) {
				_logger.error("promotion share ", e);
				response.setStatus(HttpStatus.FORBIDDEN_403);
			}

			baseRequest.setHandled(true);
		}
	}

	public void printHtml(HttpServletRequest req, ServletResponse resp,
			String htmlString) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.write(htmlString);
		writer.close();
		_logger.debug("\tEND_REQUEST\t" + req.getMethod() + "\t"
				+ req.getContextPath() + "\t" + htmlString);
	}
}
