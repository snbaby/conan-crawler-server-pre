package com.conan.crawler.server.pre.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.conan.crawler.server.pre.entity.CommentScanTb;
import com.conan.crawler.server.pre.entity.CommentTb;
import com.conan.crawler.server.pre.entity.GoodsTb;
import com.conan.crawler.server.pre.entity.ShopScanTb;
import com.conan.crawler.server.pre.mapper.CommentScanTbMapper;
import com.conan.crawler.server.pre.mapper.CommentTbMapper;
import com.conan.crawler.server.pre.mapper.GoodsTbMapper;
import com.conan.crawler.server.pre.mapper.ShopScanTbMapper;
import com.conan.crawler.server.pre.util.HttpClientUtils;
import com.conan.crawler.server.pre.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DataReportController {
	@Autowired
	private CommentTbMapper commentTbMapper;

	@Autowired
	private CommentScanTbMapper commentScanTbMapper;

	@Autowired
	private ShopScanTbMapper shopScanTbMapper;

	@Autowired
	private GoodsTbMapper goodsTbMapper;
	
	@Value("${conan.url.analysis}")
	private String analysisUrl;
	
	@Scheduled(fixedDelay = 2000, initialDelay=60000)
	public void postRateScanStart() {
		List<CommentScanTb> updateDataList = new ArrayList<>();
		List<CommentScanTb> commentScanTbList = commentScanTbMapper.selectByStatus("0");
		JSONArray jsonArray = new JSONArray();
		for(CommentScanTb commentScanTb:commentScanTbList) {
			GoodsTb goodsTb = goodsTbMapper.selectByItemId(commentScanTb.getItemId());
			if(goodsTb == null) {
				continue;
			}
			CommentTb commentTb = commentTbMapper.selectByItemId(commentScanTb.getItemId());
			if(commentTb == null) {
				continue;
			}
			ShopScanTb shopScanTb = shopScanTbMapper.selectByUserNumberId(goodsTb.getUserNumberId());
			if(shopScanTb == null) {
				continue;
			}
			updateDataList.add(commentScanTb);
			JSONObject jsonObject = new JSONObject();
			if(goodsTb.getShopType().equals("0")) {
				jsonObject.put("catalog", "TmallRate");
			}else {
				jsonObject.put("catalog", "TaobaoRate");
			}
			jsonObject.put("item_id", commentScanTb.getItemId());
			jsonObject.put("item_title", goodsTb.getItemTitle());
			jsonObject.put("shop_name", shopScanTb.getShopName());
			jsonObject.put("seller_nick", shopScanTb.getSellerNick());
			jsonObject.put("shop_star", shopScanTb.getShopStar());
			jsonObject.put("dsr_desc", shopScanTb.getDsrDesc());
			jsonObject.put("dsr_serv", shopScanTb.getDsrServ());
			jsonObject.put("dsr_logi", shopScanTb.getDsrLogi());
			jsonObject.put("total_review_cnt", commentTb.getTotal());
			jsonObject.put("buyer_nick", commentScanTb.getBuyerNick());
			jsonObject.put("first_review_time", Utils.getDateString(commentScanTb.getFirstReviewTime()));
			jsonObject.put("review_content", commentScanTb.getReviewContent());
			jsonObject.put("is_super_vip", commentScanTb.getIsSuperVip());
			jsonObject.put("buyer_star", commentScanTb.getBuyerStar());
			jsonObject.put("gmt_create_time", Utils.getDateString(commentScanTb.getCrtTime()));
			jsonObject.put("crawler_machine_id", commentScanTb.getCrtIp());
			jsonObject.put("shop_id", shopScanTb.getShopId());
			jsonArray.add(jsonObject);
			
		}
		if(updateDataList.size()>0) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("version", "1"));
			nvps.add(new BasicNameValuePair("biztype", "1"));
			nvps.add(new BasicNameValuePair("jdata", jsonArray.toString()));
			nvps.add(new BasicNameValuePair("sign", Utils.getSign(jsonArray.toString())));
			System.out.println("start---data-post---");
			if(HttpClientUtils.httpPostWithForm(nvps, analysisUrl)) {
				for(CommentScanTb commentScanTb:updateDataList) {
					commentScanTb.setStatus("1");
					commentScanTbMapper.updateByPrimaryKey(commentScanTb);
				}
				System.out.println("end---data-post---");
			}else {
				System.out.println("exception---data-post---");
			}
		}
	}
}
