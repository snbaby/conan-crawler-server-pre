package com.conan.crawler.server.pre.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.conan.crawler.server.pre.entity.CommentTb;
import com.conan.crawler.server.pre.entity.GoodsTb;
import com.conan.crawler.server.pre.mapper.CommentTbMapper;
import com.conan.crawler.server.pre.mapper.GoodsTbMapper;
import com.conan.crawler.server.pre.util.HttpClientUtils;
import com.conan.crawler.server.pre.util.Utils;

import net.sf.json.JSONObject;

@Component
public class CommentController {
	@Autowired
	private GoodsTbMapper goodsTbMapper;
	@Autowired
	private CommentTbMapper commentTbMapper;
	
	@Value("${conan.url.middleware}")
	private String middlewareUrl;

	@Scheduled(fixedDelay = 30000, initialDelay=60000)
	public void postCommentScanTotalStart(){
		System.out.println("postCommentScanTotalStart--"+middlewareUrl);
		List<GoodsTb> goodsTbList = new ArrayList<>();
		goodsTbList = goodsTbMapper.selectByStatus("0");
		System.out.println("postCommentScanTotalStart--"+goodsTbList.size());
		for (GoodsTb goodsTb : goodsTbList) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", goodsTb.getId());
			jsonObject.put("key", goodsTb.getItemId());
			jsonObject.put("value", Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			if(HttpClientUtils.httpPostWithJson(jsonObject, middlewareUrl+"/comment/total-scan")) {
				System.out.println("start---comment-total-scan---" + goodsTb.getItemId() + "---"
						+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
				goodsTb.setStatus("1");
				goodsTbMapper.updateByPrimaryKeySelective(goodsTb);
				System.out.println("end---comment-total-scan---" + goodsTb.getItemId() + "---"
						+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			}else{
				System.out.println("exception---comment-total-scan---" + goodsTb.getItemId() + "---"
						+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			};
		}
	}

	@Scheduled(fixedDelay = 30000, initialDelay=60000)
	public void postCommentScanDetailStart(){
		List<CommentTb> commentTbList = new ArrayList<>();
		commentTbList = commentTbMapper.selectByStatus("0");
		for (CommentTb commentTb : commentTbList) {
			String itemId = commentTb.getItemId();
			int total = Integer.parseInt(commentTb.getTotal());
			GoodsTb goodsTb = goodsTbMapper.selectByItemId(itemId);
			String userNumberId = goodsTb.getUserNumberId();
			String shopType = goodsTb.getShopType();
			int maxPage = 0;
			if (total % 20 == 0) {
				maxPage = total / 20;
			} else {
				maxPage = total / 20 + 1;
			}
			if(maxPage>255) {
				maxPage = 255;
			}

			for (int pageNo = 1; pageNo <= maxPage; pageNo++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", commentTb.getId());
				jsonObject.put("key", commentTb.getItemId());
				jsonObject.put("value", Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				if(HttpClientUtils.httpPostWithJson(jsonObject, middlewareUrl+"/comment/detail-scan")) {
					System.out.println("start---comment-detail-scan---" + itemId + "---"
							+ Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
					commentTb.setStatus("1");
					commentTbMapper.updateByPrimaryKeySelective(commentTb);
					System.out.println("end---comment-detail-scan---" + itemId + "---"
							+ Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				}else{
					System.out.println("exception---comment-detail-scan---" + itemId + "---"
							+ Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				};
			}
		}
	}
}
