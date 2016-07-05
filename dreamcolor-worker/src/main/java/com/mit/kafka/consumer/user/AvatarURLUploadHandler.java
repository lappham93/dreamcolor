package com.mit.kafka.consumer.user;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.PhotoCommon;
import com.mit.dao.photo.UserPhotoClient;
import com.mit.dao.user.UserInfoDAO;
import com.mit.http.HttpsRequest;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;

public class AvatarURLUploadHandler extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(AvatarURLUploadHandler.class);
	private final static String topic = ProducerTopic.UPLOAD_AVATAR_FROM_URL;

	public AvatarURLUploadHandler() {
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
				if (dataArr.length >= 3) {
					int userId = NumberUtils.toInt(dataArr[1]);
					String avatar = dataArr[2];
					ContentResponse response = HttpsRequest.Instance.doGet(avatar);
					
					if (response.getStatus() == 200) {						
						long pId = MIdGenLongDAO.getInstance(PhotoCommon.userPhotoIdGen).getNext();
	                    if(pId >= 0){	                    	
	                    	String ext = "";
	                    	
	                    	try {
	                    		URI uri = new URI(avatar);
	                    		String path = uri.getPath();
		                        ext = FilenameUtils.getExtension(path);
	                    	} catch (URISyntaxException e) {
	                    	}
	                    	
	                    	if (ext.isEmpty()) {
	                    		ext = "jpg";
	                    	}
	                    	
	                        TMPhoto tmp = new TMPhoto();
	                        tmp.setId(pId);
	                        String filename = MIdNoise.enNoiseLId(userId) + "." + ext;
	                        tmp.setFilename(filename);
	                        tmp.setData(response.getContent());
	                        String contentType = response.getHeaders().get(HttpHeader.CONTENT_TYPE);
	                        tmp.setContentType(contentType);
	                        int err = UserPhotoClient.getInstance().putMPhoto(tmp);
	                        
	                        if (err < 0){
	                        	_logger.error("Error no insert photo primary photo.");
	                        } else {
		                        UserInfoDAO.getInstance().updatePhoto(userId, pId);
	                        }
	                    } 
					}
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
	
	public static void main(String[] args) throws URISyntaxException {
//		String url = "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/v/t1.0-1/c33.33.409.409/601480_2427941435562_1747386626_n";
//		int lastSeperator = url.lastIndexOf("/");
//		int dot = url.indexOf("?", lastSeperator+1);
//		String filename;
//		if (dot >= 0) {
//			filename = url.substring(lastSeperator+1, dot);
//		} else {
//			filename = url.substring(lastSeperator+1);
//		}
//		System.out.println(filename);
//		String ext = filename.substring(filename.lastIndexOf('.')+1);
//		System.out.println(ext);
		
//		String url = "http://www.example.com/def/something.jpg";
//		URI uri = new URI(url);
//		System.out.println(uri.getPath());
//		String ext = FilenameUtils.getExtension(uri.getPath());;
//		System.out.println(ext);
		
		int userId = 188;
		String avatar = "https://lh6.googleusercontent.com/-s8pLs2DP31A/AAAAAAAAAAI/AAAAAAAAAEo/MA2gIIt6uq8/photo.jpg?sz=800";
		ContentResponse response = HttpsRequest.Instance.doGet(avatar);
		
		UserInfoDAO.getInstance().updatePhoto(userId, 0);
	}
}
