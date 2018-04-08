package com.conan.crawler.server.pre.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

public class Utils {

	public static String getKeyWordUrl(String keyWord, int pageNo) {
		try {
			return "https://s.taobao.com/search?q=" + URLEncoder.encode(keyWord, "utf-8") + "&s=" + pageNo;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "https://s.taobao.com/search?q=" + keyWord + "&s=" + pageNo;
		}
	}

	public static String getShopUrl(String shopId) {
		return "https://store.taobao.com/shop/view_shop.htm?user_number_id=" + shopId;
	}

	public static String getCommentTotalUrl(String itemId, String shopType) {
		if (shopType.equals("0")) {// 天猫
			return "https://dsr-rate.tmall.com/list_dsr_info.htm?itemId=" + itemId + "&callback=jsonp145";
		} else {
			return "https://rate.taobao.com/detailCount.do?itemId=" + itemId + "&callback=jsonp145";
		}

	}

	public static String getCommentDetailUrl(String itemId, String userNumberId, int pageNo, String shopType) {
		if (shopType.equals("0")) {// 天猫
			return "https://rate.tmall.com/list_detail_rate.htm?itemId=" + itemId + "&sellerId=" + userNumberId
					+ "&order=1&currentPage=" + pageNo + "&append=0&content=0&tagId=&posi=&picture=&callback=jsonp145";
		} else {
			return "https://rate.taobao.com/feedRateList.htm?auctionNumId=" + itemId + "&userNumId=" + userNumberId
					+ "&currentPageNum=" + pageNo
					+ "&pageSize=20&rateType=&orderType=feedbackdate&attribute=&folded=0&callback=jsonp145";
		}

	}
}
