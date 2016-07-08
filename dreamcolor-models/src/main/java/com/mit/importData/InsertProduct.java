package com.mit.importData;

import java.util.List;

import com.mit.dao.product.CategoryDAO;
import com.mit.dao.product.ProductDAO;
import com.mit.entities.product.Category;
import com.mit.entities.product.Product;

public class InsertProduct {
	public static void insertCategory() {
		for (int i = 1; i < 5; i ++) {
			Category cate = new Category(0, "Category " + i, "", 0, 1);
			CategoryDAO.getInstance().insert(cate);
		}
	}
	
	public static void insertProduct() {
		List<Category> cates = CategoryDAO.getInstance().listAll();
		if (cates != null) {
			int i = 0;
			for (Category cate : cates) {
				int count = i + 5;
				for (; i < count; i++) {
					Product pro = new Product(0, cate.getId(), "Gel " + cate.getId(), "CND", "name " + i, "desc " + i, i, null);
					ProductDAO.getInstance().insert(pro);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		insertCategory();
		insertProduct();
	}
}
