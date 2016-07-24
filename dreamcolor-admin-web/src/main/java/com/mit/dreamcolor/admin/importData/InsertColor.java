package com.mit.dreamcolor.admin.importData;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.PhotoCommon;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;
import com.mit.entities.photo.PhotoType;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.utils.MIMETypeUtil;

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
//		insertCategory();
//		insertColor();
		
		final File root = new File("f:\\Google Drive\\Edited");	
		FilenameFilter pngFilter = new FilenameFilter() {
    	    public boolean accept(File dir, String name) {
    	        return name.toLowerCase().endsWith(".png");
    	    }
    	};
		
		for (final File catDir : root.listFiles()) {
	        if (catDir.isDirectory()) {
	        	String catName = catDir.getName();
	        	Category category = CategoryDAO.getInstance().getByName(catName);
	        	File[] colorFiles = catDir.listFiles(pngFilter);
	        	
	        	if (category == null) {	       
	        		System.out.println("Import category: " + catName); 		
	        		if (colorFiles.length > 0) {
	        			long pId = PhotoUtil.Instance.uploadPhoto(colorFiles[0], PhotoType.COLOR);
	        			
	        			if (pId > 0) {
	    	        		System.out.println("Import category: " + catName);
	        				category = new Category(0, catName, catName, pId);
	        				CategoryDAO.getInstance().insert(category);
	        			} else {
	    	        		System.out.println("Upload photo failed!"); 	
	        				break;
	        			}
	        		} else {
    	        		System.out.println("Skip empty folder."); 	
	        			continue;
	        		}
	        		
	        		category = new Category(0, "", "", 0);
	        	} else {
	        		System.out.println("Skip category: " + catName);
	        	}
	        	
	        	for (final File file : colorFiles) {
	        		String colorName = FilenameUtils.removeExtension(file.getName());
	        		Color color = ColorDAO.getInstance().getByCode(colorName);
	        		
	        		if (color == null) {
		        		System.out.println("Import color: " + colorName);
	        			long pId = PhotoUtil.Instance.uploadPhoto(file, PhotoType.COLOR);
	        			color = new Color(0, category.getId(), colorName, pId, false);
	        			ColorDAO.getInstance().insert(color);
	        		} else {
	        			System.out.println("Skip color: " + colorName);
	        		}
	    	    }
	        }
	    }
		
		System.out.println("Import completed.");
	}

}
