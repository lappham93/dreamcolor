package com.mit.worker.worker;

import java.util.List;

import com.mit.dao.order.OrderDAO;
import com.mit.dao.order.OrderItemDAO;
import com.mit.dao.order.OrderPaymentDAO;
import com.mit.dao.order.OrderShippingDAO;
import com.mit.entities.order.Order;
import com.mit.entities.order.OrderItem;
import com.mit.entities.order.OrderPayment;
import com.mit.entities.order.OrderShipping;
import com.mit.utils.ShippingEasyUtils;

public class OrderWorker {	
	public static void sendOrderToShippingEasy(long orderId) {
		Order order = OrderDAO.getInstance().getById(orderId);
		OrderPayment orderPayment = OrderPaymentDAO.getInstance().getByIdIgnoreStatus(orderId);
		OrderShipping orderShipping = OrderShippingDAO.getInstance().getByIdIgnoreStatus(orderId);
		List<OrderItem> orderItems = OrderItemDAO.getInstance().getListByOrderId(orderId);
		
		if (order != null && orderPayment != null && orderShipping != null && orderItems != null) {
			ShippingEasyUtils.sendOrder(order, orderShipping, orderPayment, orderItems);
		}
	}
	
	public static void main(String[] args) {
		OrderWorker.sendOrderToShippingEasy(32);
	}
}
