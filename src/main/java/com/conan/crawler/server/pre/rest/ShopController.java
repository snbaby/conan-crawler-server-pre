package com.conan.crawler.server.pre.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shop")
public class ShopController {
	/*@Autowired
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
	}*/
}
