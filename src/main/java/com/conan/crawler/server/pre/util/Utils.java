package com.conan.crawler.server.pre.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

public class Utils {
	
	public static String getKeyWordUrl(String keyWord,int pageNo) {
		try {
			return "https://s.taobao.com/search?q=" + URLEncoder.encode(keyWord, "utf-8") + "&s=" + pageNo;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "https://s.taobao.com/search?q=" + keyWord + "&s=" + pageNo;
		}
	}
	
	public static String getShopUrl(String shopId) {
		return "https://store.taobao.com/shop/view_shop.htm?user_number_id="+shopId;
	}
}
