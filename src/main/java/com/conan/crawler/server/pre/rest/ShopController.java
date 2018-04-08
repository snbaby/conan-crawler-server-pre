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

import com.conan.crawler.server.pre.entity.ResponseResult;
import com.conan.crawler.server.pre.entity.ShopTb;
import com.conan.crawler.server.pre.mapper.ShopTbMapper;
import com.conan.crawler.server.pre.util.Utils;

@RestController
@RequestMapping("shop")
public class ShopController {
	@Autowired
	private ShopTbMapper shopTbMapper;

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@RequestMapping(value = "scan-start", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postRateScanStart() throws Exception {
		List<ShopTb> shopTbList = shopTbMapper.selectAll();
		for (ShopTb shopTb : shopTbList) {
			ListenableFuture future = kafkaTemplate.send("rate-scan", Utils.getShopUrl(shopTb.getShopId()));
			future.addCallback(o -> System.out.println("rate-scan-消息发送成功：" + Utils.getShopUrl(shopTb.getShopId())),
					throwable -> System.out.println("rate-scan-消息发送失败：" + Utils.getShopUrl(shopTb.getShopId())));
		}

		return new ResponseEntity<ResponseResult>(
				new ResponseResult(HttpStatus.CREATED.toString(), shopTbList),
				HttpStatus.CREATED);
	}
}
