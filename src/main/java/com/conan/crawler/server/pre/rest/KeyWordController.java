package com.conan.crawler.server.pre.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.conan.crawler.server.pre.entity.KeyWordTb;
import com.conan.crawler.server.pre.entity.ResponseResult;
import com.conan.crawler.server.pre.mapper.KeyWordTbMapper;
import com.conan.crawler.server.pre.util.Utils;

@RestController
@RequestMapping("key-word")
public class KeyWordController {

	private int queryPageNumber = 1;

	@Autowired
	private KeyWordTbMapper keyWordTbMapper;
	@Autowired
	private KafkaTemplate kafkaTemplate;

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

	@RequestMapping(value = "scan-start", method = RequestMethod.POST)
	@ResponseBody
	@Scheduled(fixedDelay = 60000, initialDelay=60000)
	public ResponseEntity<ResponseResult> postKeyWordScanStart() throws Exception {
		List<KeyWordTb> keyWordTbList = new ArrayList<>();
		keyWordTbList = keyWordTbMapper.selectByStatus("0");
		if(keyWordTbList==null||keyWordTbList.isEmpty()) {
			keyWordTbList = keyWordTbMapper.selectAll();
		}
		for (KeyWordTb keyWordTb : keyWordTbList) {
			for (int index = 0; index < queryPageNumber; index++) {
				System.out.println("start---key-word-scan---"+keyWordTb.getKeyWord()+"---"+Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index*44));
				ListenableFuture future = kafkaTemplate.send("key-word-scan", keyWordTb.getKeyWord(),Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index*44));
				System.out.println("end---key-word-scan---"+keyWordTb.getKeyWord()+"---"+Utils.getKeyWordUrl(keyWordTb.getKeyWord(), index*44));
				Thread.sleep(10000);
			}
			keyWordTb.setStatus("1");
			keyWordTbMapper.updateByPrimaryKey(keyWordTb);
		}

		return new ResponseEntity<ResponseResult>(
				new ResponseResult(HttpStatus.CREATED.toString(), keyWordTbList),
				HttpStatus.CREATED);
	}

}
