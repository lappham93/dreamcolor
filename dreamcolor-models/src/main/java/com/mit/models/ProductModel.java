package com.mit.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mit.dao.product.ProductDAO;
import com.mit.entities.product.Product;

public class ProductModel {
	
	public static final ProductModel Instance = new ProductModel();
	
	private ProductModel(){};
	
	public Map<String, Object> getListProduct(int count, int from) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		List<Product> pros = ProductDAO.getInstance().getSlideAll(from, count + 1, "views", false);
		if (pros != null && pros.size() > count) {
			pros = pros.subList(0, count);
			hasMore = true;
		}
		rs.put("err", err);
		rs.put("hasMore", hasMore);
		rs.put("products", Product.buildListProductViewBasic(pros));
		return rs;
	}
	
	public Map<String, Object> getProductDetail(long productId) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		Product pro = ProductDAO.getInstance().getById(productId);
		if (pro != null) {
			rs.put("product", pro.buildProductViewDetail());
			List<Product> relatePros = ProductDAO.getInstance().getListRelateProduct(pro, "views", false);
			rs.put("relatePros", Product.buildListProductViewBasic(relatePros));
		} else {
			err = ModelError.PRODUCT_NOT_EXIST;
		}
		rs.put("err", err);
		
		return rs;
	}
	
	public Map<String, Object> viewProduct(long productId) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		int temp = ProductDAO.getInstance().updateView(productId);
		if (temp > 0) {
			Product pro = ProductDAO.getInstance().getById(productId);
			if (pro != null) {
				rs.put("views", pro.getViews());
			} else {
				err = ModelError.PRODUCT_NOT_EXIST;
			}
		} else {
			err = ModelError.SERVER_ERROR;
		}
		rs.put("err", err);
		
		return rs;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
