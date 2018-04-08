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
import com.conan.crawler.server.pre.mapper.GoodsTbMapper;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("goods")
public class GoodsController {
	@Autowired
	private KafkaTemplate kafkaTemplate;
	@Autowired
	private GoodsTbMapper goodsTbMapper;

	/*@RequestMapping(value = "scan-start", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postGoodsScanStart() throws Exception {
		List<GoodsTb> goodsTbList = goodsTbMapper.selectByStatus("0");
		for (GoodsTb goodsTb : goodsTbList) {			
			ListenableFuture future = kafkaTemplate.send("goods-scan", JSONObject.fromObject(goodsTb).toString());
			future.addCallback(o -> System.out.println("goods-scan-消息发送成功：" + JSONObject.fromObject(goodsTb).toString()),
					throwable -> System.out.println("goods-scan 消息发送失败：" + JSONObject.fromObject(goodsTb).toString()));
		}

		return new ResponseEntity<ResponseResult>(
				new ResponseResult(HttpStatus.CREATED.toString(), goodsTbList),
				HttpStatus.CREATED);
	}*/
}
