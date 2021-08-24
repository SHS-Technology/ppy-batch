package com.puppyou.batch.job;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.puppyou.batch.service.MatchingService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MatchingJob {
	
	@Autowired
	MatchingService matchingService;
	
	@Scheduled(fixedRate = 300000) // 5분이면 너무 많은 리스트가 생기게 되나?.
    public void beforeMatchingEndJob() throws IOException {
		log.info("Before Matching END Job Start");
		matchingService.beforeMatchingEndJob();
	}
	
	@Scheduled(cron="0 0 5 * * *") // 오전 5시 모든 매칭 건 종료 
	public void resetMatchingJob() throws IOException {
		log.info("Reset Matching END Job Start");
		matchingService.resetMatchingJob();
	}
	
	@Scheduled(fixedRate = 60000) // firestore 실패한 값들을 모아서 1분간격으로 retry한다.
    public void firestoreJob() throws IOException, InterruptedException, ExecutionException {
		matchingService.firestoreFailRetry();
	}
	
	@Scheduled(cron="0 0 9 * * *") // 오전 9시 retry 3회 넘은 건들은 모아서 아침에 갯수 알려주기.  
	public void firestore3RetrySMS() throws Exception {
		matchingService.retrySMSpush();
	}
}
