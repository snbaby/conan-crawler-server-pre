package com.conan.crawler.server.pre.util;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class Utils {

	public static String getKeyWordUrl(String keyWord, int pageNo) {
		try {
			return "https://s.taobao.com/search?q=" + URLEncoder.encode(keyWord, "utf-8") + "&s=" + pageNo;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "https://s.taobao.com/search?q=" + keyWord + "&s=" + pageNo + "&style=grid";
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

	public static String getDateString(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeLong = Long.parseLong(time);
		Date date = new Date(timeLong);
		return sdf.format(date);
	}

	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String getSign(String jdata) {
		String salt = "b133b8bfb44b88e7fafff108619adbdd";
		String temp = "version=1&biztype=1&jdata=" + jdata + salt;
		return MD5(temp);
	}

	private static String MD5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(s.getBytes("utf-8"));
			return toHex(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String toHex(byte[] bytes) {

		final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
		StringBuilder ret = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
			ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
		}
		return ret.toString();
	}
	
	public static String getIp() {
		try {
			String ipString = "";
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
						return ip.getHostAddress();
					}
				}
			}
			return ipString;
		} catch (Exception e) {
			// TODO: handle exception
			return "127.0.0.1";
		}
	}

}
