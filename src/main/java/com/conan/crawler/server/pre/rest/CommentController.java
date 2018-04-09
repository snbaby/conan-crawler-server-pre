package com.conan.crawler.server.pre.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.conan.crawler.server.pre.entity.CommentTb;
import com.conan.crawler.server.pre.entity.GoodsTb;
import com.conan.crawler.server.pre.entity.ResponseResult;
import com.conan.crawler.server.pre.mapper.CommentTbMapper;
import com.conan.crawler.server.pre.mapper.GoodsTbMapper;
import com.conan.crawler.server.pre.util.Utils;

@RestController
@RequestMapping("comment")
public class CommentController {
	@Autowired
	private KafkaTemplate kafkaTemplate;
	@Autowired
	private GoodsTbMapper goodsTbMapper;
	@Autowired
	private CommentTbMapper commentTbMapper;

	@RequestMapping(value = "scan-total-start", method = RequestMethod.POST)
	@ResponseBody
	@Scheduled(fixedDelay = 60000)
	public ResponseEntity<ResponseResult> postCommentScanTotalStart() throws Exception {
		List<GoodsTb> goodsTbList = new ArrayList<>();
		goodsTbList = goodsTbMapper.selectByStatus("0");
		if(goodsTbList==null || goodsTbList.isEmpty()) {
			goodsTbList = goodsTbMapper.selectAll();
		}
		for (GoodsTb goodsTb : goodsTbList) {
			System.out.println("start---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			ListenableFuture future = kafkaTemplate.send("comment-total-scan", goodsTb.getItemId(),
					Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			System.out.println("end---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			goodsTb.setStatus("1");
			goodsTbMapper.updateByPrimaryKeySelective(goodsTb);
			Thread.sleep(10000);
		}

		return new ResponseEntity<ResponseResult>(new ResponseResult(HttpStatus.CREATED.toString(), goodsTbList),
				HttpStatus.CREATED);
	}

	@RequestMapping(value = "scan-detail-start", method = RequestMethod.POST)
	@ResponseBody
	@Scheduled(fixedDelay = 60000)
	public ResponseEntity<ResponseResult> postCommentScanDetailStart() throws Exception {
		List<CommentTb> commentTbList = new ArrayList<>();
		commentTbList = commentTbMapper.selectByStatus("0");
		if(commentTbList == null || commentTbList.isEmpty()) {
			commentTbList = commentTbMapper.selectAll();
		}
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

			for (int pageNo = 1; pageNo <= maxPage; pageNo++) {
				System.out.println("start---comment-detail-scan---" + itemId + "---"
						+ Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				ListenableFuture future = kafkaTemplate.send("comment-detail-scan", commentTb.getItemId(),
						Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				System.out.println("end---comment-detail-scan---" + itemId + "---"
						+ Utils.getCommentDetailUrl(itemId, userNumberId, pageNo, shopType));
				Thread.sleep(10000);
			}
			commentTb.setStatus("1");
			commentTbMapper.updateByPrimaryKeySelective(commentTb);
		}

		return new ResponseEntity<ResponseResult>(new ResponseResult(HttpStatus.CREATED.toString(), commentTbList),
				HttpStatus.CREATED);
	}
}
