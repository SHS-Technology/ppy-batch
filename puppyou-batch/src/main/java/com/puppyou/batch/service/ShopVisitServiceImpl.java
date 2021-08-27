package com.puppyou.batch.service;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.puppyou.batch.mapper.ShopVisitMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShopVisitServiceImpl implements ShopVisitService {

	@Autowired
	ShopVisitMapper shopVisitMapper;
	
	@Value("${PUSH.URL}")
	private String pushUrl;
	
	@Override
	public void visitReviewRequest() {
		// 1. 방문한지 3시간 이상 4시간 이하 된 멤버중
		// 2. push_history에 내역이 target_no 가 visit_no인 push가 없는.
		// 3. notification 수락한 멤버.
		List<HashMap<String, Object>> requestMem = shopVisitMapper.getVisitReviewRequestMember(); 
		for(int i = 0; i < requestMem.size(); i++) {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			Map<String, String> request = new HashMap<String,String>();
			request.put("type", "SHOP_VISIT_REVIEW_REQ");
			request.put("targetNo", requestMem.get(i).get("visitNo").toString());
			request.put("memNo", requestMem.get(i).get("memNo").toString());
			request.put("businessRegNo", requestMem.get(i).get("businessRegNo").toString());
			try {
				restTemplate.postForObject(pushUrl+"/push/shop-send", request, Map.class);
			}catch (HttpClientErrorException e) {
				log.info("## push server HttpClientErrorException !! ");
				e.printStackTrace();
			}catch (Exception e) {
				log.info("## push server exception !! ");
				e.printStackTrace();
			}finally {
				restTemplate = null;
				request = null;
			}
		}
	}
}
