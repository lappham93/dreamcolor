package com.mit.entities.video;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkParser {
	
	public static final int FAIL = -1;
	public static final int SUCCESS = 0;
	// define structure search
	private static final String[][] TITLE = { { "meta", "name", "title", "content" }, { "meta", "property", "og:title", "content" } };
	private static final String[][] DESCRIPTION = { { "meta", "name", "description", "content" }, { "meta", "property", "og:description", "content" } };
	private static final String[][] THUMBNAIL = { { "meta", "property", "og:image", "content" },
			{ "img", null, null, "src" } };

	private Document parser = null;
	private String link;
	private int status;

	public LinkParser(String link) {
		this.link = link;
		try {
			this.parser = Jsoup.connect(link).get();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		this.status = (this.parser == null) ? FAIL : SUCCESS;
	}

	public String parseTitle() {
		String rs = "";
		if (this.parser != null) {
			rs = this.parser.title();
			if (rs.isEmpty()) {
				boolean exit = false;
				for (String[] filter : TITLE) {
					Elements eles = this.parser.getElementsByTag(filter[0]);
					for (Element ele : eles) {
						if (filter[1] == null || filter[2] == null
								|| (ele.hasAttr(filter[1]) && ele.attr(filter[1]).equalsIgnoreCase(filter[2]))) {
							rs = ele.attr(filter[3]);
							exit = true;
							break;
						}
					}
					if (exit) {
						break;
					}
				}
			}
		}

		return rs;
	}

	public String parseDescription() {
		String rs = "";
		if (this.parser != null) {
			boolean exit = false;
			for (String[] filter : DESCRIPTION) {
				Elements eles = this.parser.getElementsByTag(filter[0]);
				for (Element ele : eles) {
					if (filter[1] == null || filter[2] == null
							|| (ele.hasAttr(filter[1]) && ele.attr(filter[1]).equalsIgnoreCase(filter[2]))) {
						rs = ele.attr(filter[3]);
						exit = true;
						break;
					}
				}
				if (exit) {
					break;
				}
			}
		}

		return rs;
	}

	public String parseSite() {
		String rs = "";
		if (parser != null) {
			rs = parser.baseUri();
			Pattern pattern = Pattern.compile("http(s?)://(www\\.)?([^/]*).*$");
			Matcher match = pattern.matcher(rs);
			if (match.find()) {
				rs = match.group(3);
			}
		}

		return rs;
	}

	public String parseThumbnail() {
		String rs = "";
		if (this.parser != null) {
			boolean exit = false;
			for (String[] filter : THUMBNAIL) {
				Elements eles = this.parser.getElementsByTag(filter[0]);
				for (Element ele : eles) {
					if (filter[1] == null || filter[2] == null
							|| (ele.hasAttr(filter[1]) && ele.attr(filter[1]).equalsIgnoreCase(filter[2]))) {
						rs = ele.attr(filter[3]);
						exit = true;
						break;
					}
				}
				if (exit) {
					break;
				}
			}
		}

		return rs;
	}
	
	public byte[] parseThumbnailData(String thumbnail) {
		byte[] rs = null;
		if (this.parser != null) {
			try {
				if (thumbnail == null || thumbnail.isEmpty()) {
					thumbnail = parseThumbnail();
				}
				URL url = new URL(thumbnail);
				InputStream is = url.openStream();
				rs = IOUtils.toByteArray(is);
			} catch (Exception e) {
				
			}
		}
		return rs;
	}

	// Getter and Setter
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static void main(String[] args) throws IOException {
		LinkParser parser = new LinkParser("https://google.com");
		System.out.println(parser.parseTitle());
		System.out.println(parser.parseDescription());
		System.out.println(parser.parseSite());
		System.out.println(parser.parseThumbnail());
//		String thumbnail = parser.parseThumbnail();
		System.out.println("parse done ");
	}

}
