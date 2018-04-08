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
	
	public static String getCommentTotalUrl(String itemId,String shopType) {
		if(shopType.equals("0")) {//天猫
			return "https://dsr-rate.tmall.com/list_dsr_info.htm?itemId="+itemId+"&callback=jsonp145";
		}else {
			return "https://rate.taobao.com/detailCount.do?itemId="+itemId+"&callback=jsonp145";	
		}
		
	}
}
