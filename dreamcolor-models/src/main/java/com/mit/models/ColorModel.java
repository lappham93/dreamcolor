package com.mit.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;

public class ColorModel {
	
	public static final ColorModel Instance = new ColorModel();
	
	private ColorModel(){};
	
	public Map<String, Object> getListCategory(int count, int from) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		List<Category> cates = CategoryDAO.getInstance().getSlice(count + 1, from, "name", true);
		if (cates != null && cates.size() > count) {
			cates = cates.subList(0, count);
			hasMore = true;
		}
		rs.put("err", err);
		rs.put("hasMore", hasMore);
		rs.put("categories", cates);
		
		return rs;
	}
	
	public Map<String, Object> getListColor(int categoryId, int count, int from) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		List<Color> colors = ColorDAO.getInstance().getSlice(categoryId, count + 1, from, "code", true);
		if (colors != null && colors.size() > count) {
			colors = colors.subList(0, count);
			hasMore = true;
		}
		rs.put("err", err);
		rs.put("hasMore", hasMore);
		rs.put("colors", Color.buildListColorView(colors));
		
		return rs;
	}
	
	public Map<String, Object> viewColor(long colorId) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		int tmp = ColorDAO.getInstance().updateView(colorId);
		if (tmp > 0) {
			Color color = ColorDAO.getInstance().getById(colorId);
			if (color != null) {
				rs.put("views", color.getViews());
			} else {
				err = ModelError.SERVER_ERROR;
			}
		} else {
			err = ModelError.COLOR_NOT_EXIST;
		}
		rs.put("err", err);
		
		return rs;
	}
}
