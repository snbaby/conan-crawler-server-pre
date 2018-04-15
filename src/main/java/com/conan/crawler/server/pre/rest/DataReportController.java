package com.conan.crawler.server.pre.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.conan.crawler.server.pre.entity.AnalysisV;
import com.conan.crawler.server.pre.entity.CommentScanTb;
import com.conan.crawler.server.pre.mapper.AnalysisVMapper;
import com.conan.crawler.server.pre.mapper.CommentScanTbMapper;
import com.conan.crawler.server.pre.util.HttpClientUtils;
import com.conan.crawler.server.pre.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class DataReportController {

	@Autowired
	private CommentScanTbMapper commentScanTbMapper;

	@Autowired
	private AnalysisVMapper analysisVMapper;
	
	@Value("${conan.url.analysis}")
	private String analysisUrl;
	
	@Scheduled(fixedDelay = 1000, initialDelay=60000)
	public void postRateScanStart() {
		System.out.println("postRateScanStart--"+analysisUrl);
		List<AnalysisV> analysisVList = analysisVMapper.selectAll();
		System.out.println("postRateScanStart--"+analysisVList.size());
		if(analysisVList == null || analysisVList.isEmpty()) {
			System.out.println("postRateScanStart--null--"+analysisVList.size());
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for(AnalysisV analysisV:analysisVList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("catalog", analysisV.getCatalog());
			jsonObject.put("item_id", analysisV.getItemId());
			jsonObject.put("item_title", analysisV.getItemTitle());
			jsonObject.put("shop_name", analysisV.getShopName());
			jsonObject.put("seller_nick", analysisV.getSellerNick());
			jsonObject.put("shop_star", analysisV.getShopStar());
			jsonObject.put("dsr_desc", analysisV.getDsrDesc());
			jsonObject.put("dsr_serv", analysisV.getDsrServ());
			jsonObject.put("dsr_logi", analysisV.getDsrLogi());
			jsonObject.put("total_review_cnt", analysisV.getTotal());
			jsonObject.put("buyer_nick", analysisV.getBuyerNick());
			jsonObject.put("first_review_time", Utils.getDateString(analysisV.getFirstReviewTime()));
			jsonObject.put("review_content", analysisV.getReviewContent());
			jsonObject.put("is_super_vip", analysisV.getIsSuperVip());
			jsonObject.put("buyer_star", analysisV.getBuyerStar());
			jsonObject.put("gmt_create_time", Utils.getDateString(analysisV.getCrtTime()));
			jsonObject.put("crawler_machine_id", analysisV.getCrtIp());
			jsonObject.put("shop_id", analysisV.getShopId());
			jsonArray.add(jsonObject);
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("version", "1"));
		nvps.add(new BasicNameValuePair("biztype", "1"));
		nvps.add(new BasicNameValuePair("jdata", jsonArray.toString()));
		nvps.add(new BasicNameValuePair("sign", Utils.getSign(jsonArray.toString())));
		System.out.println("start---data-post---");
		if(HttpClientUtils.httpPostWithForm(nvps, analysisUrl)) {
			for(AnalysisV analysisV:analysisVList) {
				CommentScanTb commentScanTb = commentScanTbMapper.selectByPrimaryKey(analysisV.getId());
				commentScanTb.setStatus("1");
				commentScanTbMapper.updateByPrimaryKey(commentScanTb);
			}
			System.out.println("end---data-post---");
		}else {
			System.out.println("exception---data-post---");
		}
	}
	
	/*
	 * public static void main(String[] args) { JSONArray jsonArray = new
	 * JSONArray(); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("catalog", "TmallRate"); jsonObject.put("item_id",
	 * "558485793759"); jsonObject.put("item_title",
	 * "朴西 家居情侣全包跟棉拖鞋男室内居家防滑厚底保暖月子拖鞋女冬"); jsonObject.put("shop_name", "朴西旗舰店");
	 * jsonObject.put("seller_nick", "朴西旗舰店"); jsonObject.put("shop_star", "");
	 * jsonObject.put("dsr_desc", "4.9"); jsonObject.put("dsr_serv", "4.8");
	 * jsonObject.put("dsr_logi", "4.8"); jsonObject.put("total_review_cnt", 17243);
	 * jsonObject.put("buyer_nick", "t***i"); jsonObject.put("first_review_time",
	 * Utils.getDateString("1517139617000")); jsonObject.put("review_content", "");
	 * jsonObject.put("is_super_vip", true); jsonObject.put("buyer_star", "");
	 * jsonObject.put("gmt_create_time", "2018-04-16 00:34:16");
	 * jsonObject.put("crawler_machine_id", "172.27.16.6");
	 * jsonObject.put("shop_id", "'109604918'"); jsonArray.add(jsonObject);
	 * 
	 * List<NameValuePair> nvps = new ArrayList<NameValuePair>(); nvps.add(new
	 * BasicNameValuePair("version", "1")); nvps.add(new
	 * BasicNameValuePair("biztype", "1")); nvps.add(new BasicNameValuePair("jdata",
	 * jsonArray.toString())); nvps.add(new BasicNameValuePair("sign",
	 * Utils.getSign(jsonArray.toString())));
	 * 
	 * HttpClientUtils.httpPostWithForm(nvps,
	 * "http://223.93.147.217:4433/PtBrain/listen"); }
	 */
}
