package com.conan.crawler.server.pre.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conan.crawler.server.pre.entity.SellerTb;
import com.conan.crawler.server.pre.mapper.SellerTbMapper;
import com.conan.crawler.server.pre.util.HttpClientUtils;
import com.conan.crawler.server.pre.util.Utils;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("shop")
public class ShopController {
	@Autowired
	private SellerTbMapper sellerTbMapper;

	@Value("${conan.url.middleware}")
	private String middlewareUrl;

	@Scheduled(fixedDelay = 60000, initialDelay=60000)
	public void postShopScanStart(){
		List<SellerTb> sellerTbList = new ArrayList<>();
		sellerTbList = sellerTbMapper.selectByStatus("0");
		for (SellerTb sellerTb : sellerTbList) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", sellerTb.getId());
				jsonObject.put("key", sellerTb.getUserNumberId());
				jsonObject.put("value", Utils.getShopUrl(sellerTb.getUserNumberId()));
				if(HttpClientUtils.httpPostWithJson(jsonObject, middlewareUrl+"/shop/scan")) {
					System.out.println("start---shop-scan---"+sellerTb.getUserNumberId()+"---"+Utils.getShopUrl(sellerTb.getUserNumberId()));
					sellerTb.setStatus("1");
					sellerTbMapper.updateByPrimaryKeySelective(sellerTb);
					System.out.println("end---shop-scan---"+sellerTb.getUserNumberId()+"---"+Utils.getShopUrl(sellerTb.getUserNumberId()));
				}else{
					System.out.println("exception---shop-scan---"+sellerTb.getUserNumberId()+"---"+Utils.getShopUrl(sellerTb.getUserNumberId()));
				};
		}

	}
}
