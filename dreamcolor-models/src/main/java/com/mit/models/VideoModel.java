package com.mit.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mit.dao.video.VideoDAO;
import com.mit.entities.video.LinkParser;
import com.mit.entities.video.Video;

public class VideoModel {
	public static final int NEW_SORT = 1;
	public static final int VIEW_SORT = 2;
	
	public static final VideoModel Instance = new VideoModel();
	
	private VideoModel(){};
	
	public Map<String, Object> getListVideo(int count, int from, int option) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		String fieldSort = (option == NEW_SORT) ? "createTime" : "views";
		List<Video> videos = VideoDAO.getInstance().getSlice(count + 1, from, fieldSort, false);
		if (videos != null && videos.size() > count) {
			videos = videos.subList(0, count);
			hasMore = true;
		}
		rs.put("err", err);
		rs.put("hasMore", hasMore);
		rs.put("products", videos);
		return rs;
	}
	
	public Map<String, Object> viewVideo(long videoId) {
		Map<String, Object> rs = new HashMap<>();
		int err = ModelError.SUCCESS;
		int temp = VideoDAO.getInstance().updateView(videoId);
		if (temp > 0) {
			Video video = VideoDAO.getInstance().getById(videoId);
			if (video != null) {
				rs.put("views", video.getViews());
			} else {
				err = ModelError.SERVER_ERROR;
			}
		} else {
			err = ModelError.VIDEO_NOT_EXIST;
		}
		rs.put("err", err);
		
		return rs;
	}

	public int addVideo(String link, String site, String title, String desc, long thumbnail) {
		if (VideoDAO.getInstance().getByLink(link) != null) {
			return -1;
		}
		if (site == "" && title == "" && desc == "" && thumbnail <= 0) {
			LinkParser parser = new LinkParser(link);
			if (parser.getStatus() == LinkParser.SUCCESS) {
				site = parser.parseSite();
				title = parser.parseTitle();
				desc = parser.parseDescription();
				//TODO parser thumbnail, upload thumbnail to server
				
			}
		}
		int rs = VideoDAO.getInstance().insert(new Video(0, link, site, title, desc, thumbnail));
		
		return rs;
	}
	
	public Map<String, Object> viewVideoLInk() {
		Map<String, Object> rs = new HashMap<String, Object>();
		
		
		return rs;
	}
	
}
