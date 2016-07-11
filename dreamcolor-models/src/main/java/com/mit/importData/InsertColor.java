package com.mit.importData;

import java.util.List;

import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;

public class InsertColor {
	
	public static void insertCategory() {
		for (int i = 1; i < 10; i++) {
			Category cate = new Category(0, "Category " + i, "Description " + i, i);
			CategoryDAO.getInstance().insert(cate);
		}
	}
	
	public static void insertColor() {
		List<Category> cates = CategoryDAO.getInstance().getSlice(10, 0, "name", true);
		if (cates != null) {
			int i = 0;
			for (Category cate : cates) {
				int count = i + 5;
				for (; i < count; i++) {
					Color color = new Color(0, cate.getId(), "code " + i, new Long(i), false);
					ColorDAO.getInstance().insert(color);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		insertCategory();
		insertColor();

	}

}
