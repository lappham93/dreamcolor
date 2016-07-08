import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.UrlEncoded;

import com.mit.http.HttpRequest;
import com.mit.utils.AESDigestUtils;

public class TestApi {
	static String privateKey = "e1a652b16f33c8cf8e3b5113db7367d2";

	static String userDomain = "api.spabee.com";
	static String adsDomain = "api.spabee.com";
	static String userName = "0936024920";
	static String password = "kids1988@";
	static String imei = "123456789";
	static String sessionKey = "wKcrUpt47MLXidNf5ChFSG1D9zYL4kXBDlUFsVRUcg8=";

	public static void main(String[] args) {
		try {
//			String rs = login();
//			String sessionKey = String.valueOf(((Map)JsonUtils.Instance.getMapObject(rs).get("data")).get("sessionKey"));
			System.out.println(sessionKey);
			uploadImage(sessionKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static String login() throws Exception {
		String params = "{\"userName\": \"" + userName + "\", \"password\": \"" + password + "\", \"imei\": \"" + imei + "\"}";
		String url = "http://" + userDomain + "/api/mobile/login?api_key=32c1d2d609c77fc34d6beb5a88b96c6e&timestamp=" + System.currentTimeMillis();
		url += "&params=" + UrlEncoded.encodeString(AESDigestUtils.encrypt(params, privateKey));
		ContentResponse response = HttpRequest.Instance.doGetResponse(url);
		return response.getContentAsString();
	}

	public static void uploadImage(String sessionKey) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost post = new HttpPost("http://" + adsDomain + "/api/mobile/upload/ads_image");

		Path filePath = Paths.get("./2-th.jpg");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int)filePath.toFile().length());
		Files.copy(filePath, outputStream);

		byte[] bytes = outputStream.toByteArray();

		String params = "{\"token\": \"AAAAFS/H2NRbGMBSyaGrQr/jqzcrtrzNUZieJA==\", \"sessionKey\": \"" + sessionKey  + "\"}";
		UrlEncoded.encodeString(AESDigestUtils.encrypt(params, privateKey));

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addTextBody("api_key", "32c1d2d609c77fc34d6beb5a88b96c6e", ContentType.TEXT_PLAIN);
		builder.addTextBody("timestamp", System.currentTimeMillis() + "", ContentType.TEXT_PLAIN);
		builder.addTextBody("params", AESDigestUtils.encrypt(params, privateKey), ContentType.TEXT_PLAIN);
		builder.addBinaryBody("file", bytes, ContentType.APPLICATION_OCTET_STREAM, "1.jpg");
		//
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		try {
			HttpResponse response = client.execute(post);
			HttpEntity rs = response.getEntity();
			rs.writeTo(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
