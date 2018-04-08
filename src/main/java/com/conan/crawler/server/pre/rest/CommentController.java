package com.conan.crawler.server.pre.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<ResponseResult> postCommentScanTotalStart() throws Exception {
		List<GoodsTb> goodsTbList = goodsTbMapper.selectAll();
		for (GoodsTb goodsTb : goodsTbList) {
			System.out.println("start---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			ListenableFuture future = kafkaTemplate.send("comment-total-scan", goodsTb.getItemId(),
					Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			System.out.println("end---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			Thread.sleep(20000);
		}

		return new ResponseEntity<ResponseResult>(new ResponseResult(HttpStatus.CREATED.toString(), goodsTbList),
				HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "scan-detail-start", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postCommentScanDetailStart() throws Exception {
		List<GoodsTb> commentTbList = commentTbMapper.selectAll();
		for (GoodsTb goodsTb : goodsTbList) {
			System.out.println("start---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			ListenableFuture future = kafkaTemplate.send("comment-total-scan", goodsTb.getItemId(),
					Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			System.out.println("end---comment-total-scan---" + goodsTb.getItemId() + "---"
					+ Utils.getCommentTotalUrl(goodsTb.getItemId(), goodsTb.getShopType()));
			Thread.sleep(20000);
		}

		return new ResponseEntity<ResponseResult>(new ResponseResult(HttpStatus.CREATED.toString(), goodsTbList),
				HttpStatus.CREATED);
	}
}
