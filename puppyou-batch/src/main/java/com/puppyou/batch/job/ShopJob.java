package com.puppyou.batch.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.puppyou.batch.service.ShopVisitService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShopJob {
	
	@Autowired
	ShopVisitService shopVisitService;

	@Scheduled(fixedRate = 300000)
    public void visitReqReview() {
		shopVisitService.visitReviewRequest();
    }
}
