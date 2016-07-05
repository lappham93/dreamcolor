/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.kafka.consumer.email;

import hapax.TemplateDataDictionary;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.order.OrderDAO;
import com.mit.dao.order.OrderItemDAO;
import com.mit.dao.order.OrderPaymentDAO;
import com.mit.dao.order.OrderShippingDAO;
import com.mit.dao.order.ShippingOptionDAO;
import com.mit.dao.product.DiscountCodeDAO;
import com.mit.dao.user.UserInfoDAO;
import com.mit.entities.order.Order;
import com.mit.entities.order.OrderItem;
import com.mit.entities.order.OrderPayment;
import com.mit.entities.order.OrderShipping;
import com.mit.entities.order.PaymentOption;
import com.mit.entities.order.ShippingOption;
import com.mit.entities.user.DiscountCode;
import com.mit.entities.user.UserInfo;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.midutil.MIdNoise;
import com.mit.utils.BigDecimalUtils;
import com.mit.utils.ConfigUtils;

/**
 *
 * @author nghiatc
 * @since Jan 27, 2016
 */
public class ConsumerEmailOrder  extends ConsumerService {
	private final Logger logger = LoggerFactory.getLogger(ConsumerEmailOrder.class);
	private final static String topic = ProducerTopic.SEND_EMAIL_ORDER;

    private final String _smtpHost = ConfigUtils.getConfig().getString("email.smtp.host");
	private final int _smtpPort = ConfigUtils.getConfig().getInt("email.smtp.port");
	private final String _emailUserName = ConfigUtils.getConfig().getString("email.username");
	private final String _emailPassword = ConfigUtils.getConfig().getString("email.password");

    private final String _smtpHost2 = ConfigUtils.getConfig().getString("email2.smtp.host");
	private final int _smtpPort2 = ConfigUtils.getConfig().getInt("email2.smtp.port");
	private final String _emailUserName2 = ConfigUtils.getConfig().getString("email2.username");
	private final String _emailPassword2 = ConfigUtils.getConfig().getString("email2.password");
    
	public ConsumerEmailOrder() {
		super(topic);
	}

	@Override
	public String getTopic() {
		return topic;
	}

    @Override
    public void execute(byte[] data) {
        String msg = new String(data);
		try {
			if (msg != null && !msg.isEmpty()) {
				String[] dataArr = msg.split("\t");
				if(dataArr.length >= 6) {
					String emailTos = dataArr[1];
					String emailName = dataArr[2];
					String emailCCs = dataArr[3];
					String subject = dataArr[4];
                    String oidNoise = dataArr[5];
					
					String message = createContentOrder(oidNoise);
					boolean success = sendEmail(emailTos, emailName, emailCCs, subject, message, _smtpHost, 
							_smtpPort, _emailUserName, _emailPassword);

					if (!success) {
						success = sendEmail(emailTos, emailName, emailCCs, subject, message, _smtpHost2, 
								_smtpPort2, _emailUserName2, _emailPassword2);
						
						if (!success) {
							logger.error("Send mail error " + msg);
						}
					}
				} else {
	        		logger.error("Data format error " + msg);
	        	}
			}
		} catch (Exception e) {
			logger.error("consumer error " + topic, e);
		}
    }
    
    private boolean sendEmail(String emailTos, String emailName, String emailCCs, String subject, String message,
    		String smtpHost, int smtpPort, String emailUserName, String emailPassword) {
    	try {
	    	HtmlEmail sender = new HtmlEmail();
			sender.setHostName(smtpHost);
			sender.setSmtpPort(smtpPort);
			sender.setAuthenticator(new DefaultAuthenticator(emailUserName, emailPassword));
			sender.setStartTLSRequired(true);
			sender.setStartTLSEnabled(true);
			sender.setFrom(emailUserName, emailName);
			sender.setSubject(subject);
			sender.setHtmlMsg(message);
			for (String emailTo: emailTos.split(",")) {
				try {
					sender.addTo(emailTo);
				} catch (Exception e) {
					
				}
			}
			
			String[] emailCCList = emailCCs.split(",");
			if (sender.getToAddresses().isEmpty() && emailCCList.length > 0) {
				sender.addTo(emailCCList[0]);
			}
			
			if (!emailCCs.isEmpty()) {
				for (String emailCC: emailCCList) {
					sender.addBcc(emailCC);
				}
			}
						
			for (int i = 0; i < 3; i++) {
				try {
					String rs = sender.send();
					logger.debug(emailTos + "\t" + rs);
					return true;
				} catch (EmailException e) {
					logger.error("Send mail error " + emailTos, e);
				}
			}
    	} catch (Exception e) {
    		logger.error("Send mail error ", e);
    	}
    	
    	return false;
    }
    
    private static String createContentOrder(String oidNoise){
        TemplateDataDictionary dic = EmailCommon.getDictionary();
        if(oidNoise != null && !oidNoise.isEmpty()){
            long oid = MIdNoise.deNoiseLId(oidNoise);
            //get order info.
            Order order = OrderDAO.getInstance().getById(oid);
            if(order != null){
                // render Discount Code.
                long dcid = order.getDiscountCodeId();
                if(dcid > 0){
                    DiscountCode dc = DiscountCodeDAO.getInstance().getById(dcid);
                    if(dc != null){
                        dic.setVariable("DISCODE", dc.getCode());
                    }
                }
                //render User Info.
                int uid = order.getUserId();
                UserInfo ui = UserInfoDAO.getInstance().getById(uid);
                if(ui != null){
                    dic.setVariable("NAME_CUSTOMER", ui.getFirstName() + " " + ui.getLastName());
                }
                //render Order Summary.
                BigDecimal totalOrder = BigDecimal.ZERO;
                List<OrderItem> listOI = OrderItemDAO.getInstance().getListByOrderId(oid);
                if(listOI != null && !listOI.isEmpty()){
                    for(OrderItem oi : listOI){
                        TemplateDataDictionary loopOrderItem = dic.addSection("loop_order_item");
                        loopOrderItem.setVariable("NAME_SKU", oi.getName());
                        int qty = oi.getQuantity();
                        loopOrderItem.setVariable("QUANTITY", String.valueOf(qty));
                        BigDecimal price = oi.getSalePrice();
                        loopOrderItem.setVariable("PRICE", BigDecimalUtils.Instance.formatMoney(price));
                        BigDecimal total = price.multiply(new BigDecimal(qty));
                        totalOrder = totalOrder.add(total);
                        loopOrderItem.setVariable("TOTAL", BigDecimalUtils.Instance.formatMoney(total));
                    }
                }
                dic.setVariable("TOTAL_ORDER", BigDecimalUtils.Instance.formatMoney(order.getOrderTotal()));
                dic.setVariable("ORDER_NUMBER", order.getOrderNumber());                
                dic.setVariable("TOTAL_RETAIL_PRICE", BigDecimalUtils.Instance.formatMoney(order.getTotalRetailPrice()));
                dic.setVariable("SUBTOTAL", BigDecimalUtils.Instance.formatMoney(order.getOrderSubtotal()));
                dic.setVariable("PROMOTION", BigDecimalUtils.Instance.formatMoney(order.getDiscountAmount()));
                dic.setVariable("TOTAL_TAX", BigDecimalUtils.Instance.formatMoney(order.getTax()));
                dic.setVariable("TOTAL_SHIPPING", BigDecimalUtils.Instance.formatMoney(order.getTotalShipping()));
                
                //render Shipping Information.
                OrderShipping os = OrderShippingDAO.getInstance().getByIdIgnoreStatus(oid);
                if(os != null){
                    dic.setVariable("BUSINESS_NAME", os.getBusinessName());
                    dic.setVariable("SHIPPING_NAME", os.getFirstName() + " " + os.getLastName());
                    dic.setVariable("SHIPPING_PHONE", os.getPhone());
                    dic.setVariable("SHIPPING_ADDRESS1", os.getAddressLine1());
                    dic.setVariable("SHIPPING_ADDRESS2", os.getAddressLine2());
                    dic.setVariable("SHIPPING_CITY", os.getCity() + " " + os.getState() + " " + os.getZipCode());
                    dic.setVariable("SHIPPING_COUNTRY", os.getCountry());
                    //render Shipping Method.
                    int soid = os.getShippingOptionId();
                    ShippingOption sho = ShippingOptionDAO.getInstance().getById(soid);
                    if(sho != null){
                        dic.setVariable("SHIPPING_OPTION_NAME", sho.getName());
                        dic.setVariable("SHIPPING_OPTION_DESC", sho.getDesc());
                    }
                }

                //render Billing Information.
                OrderPayment op = OrderPaymentDAO.getInstance().getByIdIgnoreStatus(oid);
                if(op != null){
                    String paymentType = PaymentOption.get(op.getPaymentOption()).getName();
                    dic.setVariable("PAYMENT_TYPE", paymentType);
                    dic.setVariable("AMOUNT_PAID", order.getOrderTotal().toString());
                    
                    dic.setVariable("BILLING_NAME", op.getFirstName() + " " + op.getLastName());
                    dic.setVariable("BILLING_PHONE", op.getPhone());
                    dic.setVariable("BILLING_ADDRESS1", op.getAddressLine1());
                    dic.setVariable("BILLING_ADDRESS2", op.getAddressLine2());
                    dic.setVariable("BILLING_CITY", op.getCity() + " " + op.getState() + " " + op.getZipCode());
                    dic.setVariable("BILLING_COUNTRY", op.getCountry());
                }
            }
        }
        return EmailCommon.applyTemplate(dic, "email/orderConfirmation-email.xtm");
    }
}
