package com.mit.dreamcolor.admin.importData;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;
import com.mit.entities.photo.PhotoType;

public class InsertColor {
	public static final File root = new File("/Users/lappv/DMdata");	
	
	public static void insert() {
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
//	        			long pId = PhotoUtil.Instance.uploadPhoto(colorFiles[0], PhotoType.COLOR);
	        			long pId = -1;
    	        		System.out.println("Import category: " + catName);
        				category = new Category(0, catName, catName, pId);
        				CategoryDAO.getInstance().insert(category);
	        		} else {
    	        		System.out.println("Skip empty folder."); 	
	        			continue;
	        		}
	        	} else {
	        		System.out.println("Skip category: " + catName);
	        	}
	        	
	        	for (final File file : colorFiles) {
	        		String colorName = FilenameUtils.removeExtension(file.getName());
	        		Color color = ColorDAO.getInstance().getByCodeAndCategory(colorName, category.getId());
	        		
	        		if (color == null) {
		        		System.out.println("Import color: " + colorName);
//	        			long pId = PhotoUtil.Instance.uploadPhoto(file, PhotoType.COLOR);
	        			long pId = -1;
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
	
	public static void insertAndUpdatePhoto() {
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
	        	
	        	if (category == null || category.getPhotoNum() <= 0) {	       
	        		System.out.println("Import category: " + catName); 		
	        		if (colorFiles.length > 0) {
	        			long pId = PhotoUtil.Instance.uploadPhoto(colorFiles[0], PhotoType.COLOR);
    	        		System.out.println("Import category: " + catName);
    	        		if (category == null) {
	        				category = new Category(0, catName, catName, pId);
	        				CategoryDAO.getInstance().insert(category);
    	        		} else {
    	        			category.setPhoto(pId);
    	        			CategoryDAO.getInstance().update(category);
    	        		}
	        		} else {
    	        		System.out.println("Skip empty folder."); 	
	        			continue;
	        		}
	        	} else {
	        		System.out.println("Skip category: " + catName);
	        	}
	        	
	        	for (final File file : colorFiles) {
	        		String colorName = FilenameUtils.removeExtension(file.getName());
	        		Color color = ColorDAO.getInstance().getByCodeAndCategory(colorName, category.getId());
	        		
	        		if (color == null || color.getPhoto() <= 0) {
		        		System.out.println("Import color: " + colorName);
	        			long pId = PhotoUtil.Instance.uploadPhoto(file, PhotoType.COLOR);
	        			if (color == null) {
		        			color = new Color(0, category.getId(), colorName, pId, false);
		        			ColorDAO.getInstance().insert(color);
	        			} else {
	        				color.setPhoto(pId);
	        				ColorDAO.getInstance().update(color);
	        			}
	        		} else {
	        			System.out.println("Skip color: " + colorName);
	        		}
	    	    }
	        	
	        }
	    }
		
		System.out.println("Import completed.");
	}
	
	public static void main(String[] args) {
		insertAndUpdatePhoto();
	}

}
