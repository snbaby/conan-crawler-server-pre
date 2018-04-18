package com.conan.crawler.server.pre.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.conan.crawler.server.pre.entity.KeyWordTb;
import com.conan.crawler.server.pre.entity.ResponseResult;
import com.conan.crawler.server.pre.entity.SellerTb;
import com.conan.crawler.server.pre.mapper.KeyWordTbMapper;
import com.conan.crawler.server.pre.util.HttpClientUtils;
import com.conan.crawler.server.pre.util.Utils;

import net.sf.json.JSONObject;

@RestController
@Component
@RequestMapping("key-word")
public class KeyWordController {

	@Autowired
	private KeyWordTbMapper keyWordTbMapper;

	@Value("${conan.url.middleware}")
	private String middlewareUrl;

	@Value("${conan.key-word-query-page-number}")
	private int queryPageNumber;

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postKeyWordUpload(
			@RequestParam(value = "keyWordFile", required = true) MultipartFile keyWordFile) throws Exception {
		InputStreamReader read = new InputStreamReader(keyWordFile.getInputStream(), "utf-8");
		BufferedReader reader = new BufferedReader(read);
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.trim().length() > 0) {
				KeyWordTb record = new KeyWordTb();
				record.setId(UUID.randomUUID().toString());
				record.setKeyWord(line.trim());
				record.setCrtUser("admin");
				record.setCrtTime(new Date());
				record.setStatus("0");
				record.setCrtIp(Utils.getIp());
				keyWordTbMapper.insert(record);
			}
		}
		read.close();
		return new ResponseEntity<ResponseResult>(new ResponseResult(HttpStatus.CREATED.toString()),
				HttpStatus.CREATED);
	}

	@Scheduled(fixedDelay = 30000, initialDelay = 60000)
	public void postKeyWordScanStart() {
		System.out.println("postKeyWordScanStart--"+middlewareUrl);
		List<KeyWordTb> keyWordTbList = new ArrayList<>();
		keyWordTbList = keyWordTbMapper.selectByStatus("0");
		System.out.println("postKeyWordScanStart--"+keyWordTbList.size());
		for (KeyWordTb keyWordTb : keyWordTbList) {
			for (int index = 0; index < queryPageNumber; index++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", keyWordTb.getId());
				jsonObject.put("key", keyWordTb.getKeyWord());
				jsonObject.put("value", Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index * 44));
				if (HttpClientUtils.httpPostWithJson(jsonObject, middlewareUrl + "/key-word/scan")) {
					System.out.println("start---key-word-scan---" + keyWordTb.getKeyWord() + "---"
							+ Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index * 44));
					keyWordTb.setStatus("1");
					keyWordTbMapper.updateByPrimaryKey(keyWordTb);
					System.out.println("end---key-word-scan---" + keyWordTb.getKeyWord() + "---"
							+ Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index * 44));
				} else {
					System.out.println("exception---key-word-scan---" + keyWordTb.getKeyWord() + "---"
							+ Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index * 44));
				}
				;
			}
			keyWordTb.setStatus("1");
			keyWordTbMapper.updateByPrimaryKey(keyWordTb);
		}
	}

}
