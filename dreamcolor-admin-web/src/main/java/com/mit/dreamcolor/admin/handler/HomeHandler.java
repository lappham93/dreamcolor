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

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dreamcolor.admin.utils.FormatHelper;
import com.mit.dreamcolor.admin.utils.HttpHelper;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 18, 2015
 */
public class HomeHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(HomeHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
            
            renderHome(dic, req, resp);
            
            dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "home.xtm", req));
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
            if(action != null && !action.isEmpty()) {
                
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
            } else{
                printStrJSON(result.toString(), resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    private void renderHome(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
//        long totalUsers = UserInfoDAO.getInstance().getTotalActive(1);
//        dic.setVariable("NUM_USERS", String.valueOf(totalUsers));
        
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.DAY_OF_YEAR, start.get(Calendar.DAY_OF_YEAR)-2);
        //System.out.println("start getYYMDHMS: " + FormatHelper.getYYMDHMS(start));
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MINUTE, 59);
        //System.out.println("end getYYMDHMS: " + FormatHelper.getYYMDHMS(end));
//        long totalDayOrders = OrderDAO.getInstance().getTotalOrderFromRange(start.getTimeInMillis(), end.getTimeInMillis());
//        dic.setVariable("DAY_ORDERS", String.valueOf(totalDayOrders));
//        
//        long totalOnHoldPayment = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.ON_HOLD.getValue());
//        dic.setVariable("ON_HOLD", String.valueOf(totalOnHoldPayment));
//        
//        long totalPendingPayment = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.PENDING_PAYMENT.getValue());
//        dic.setVariable("PENDING_ORDERS", String.valueOf(totalPendingPayment));
//        
//        long totalCanceled = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.CANCELED.getValue());
//        dic.setVariable("CANCELED_ORDERS", String.valueOf(totalCanceled));
//        
//        long totalPaymentReceived = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.PAYMENT_RECEIVED.getValue());
//        dic.setVariable("PAYMENT_RECEIVED", String.valueOf(totalPaymentReceived));
//        
//        long totalPackaging = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.PACKAGING.getValue());
//        dic.setVariable("PACKAGING", String.valueOf(totalPackaging));
//        
//        long totalShipping = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.ORDER_SHIPPED.getValue());
//        dic.setVariable("SHIPPING", String.valueOf(totalShipping));
//        
//        long totalArchived = OrderDAO.getInstance().getTotalOrderByStatus(Order.OrderStatus.ORDER_ARCHIVED.getValue());
//        dic.setVariable("ARCHIVE", String.valueOf(totalArchived));
//        
//        long totalOrders = OrderDAO.getInstance().getTotal();
//        dic.setVariable("TT_ORDERS", String.valueOf(totalOrders));
//        
    }
    
    public static void main(String[] args) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MINUTE, 0);
        System.out.println("start getYYMDHMS: " + FormatHelper.getYYMDHMS(start));
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MINUTE, 59);
        System.out.println("end getYYMDHMS: " + FormatHelper.getYYMDHMS(end));
    }
    
    
    
}
