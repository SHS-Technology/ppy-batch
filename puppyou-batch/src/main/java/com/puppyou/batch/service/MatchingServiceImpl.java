package com.puppyou.batch.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.puppyou.batch.core.CD.EventType;
import com.puppyou.batch.entity.BoneHistory;
import com.puppyou.batch.entity.Matching;
import com.puppyou.batch.entity.MatchingFirestore;
import com.puppyou.batch.mapper.EventListMapper;
import com.puppyou.batch.mapper.MatchingFireStoreMapper;
import com.puppyou.batch.mapper.MatchingMapper;
import com.puppyou.batch.util.FireBaseUtil;
import com.puppyou.batch.util.SMSUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MatchingServiceImpl implements MatchingService {
	
	@Autowired
	MatchingMapper matchingMapper;
	
	@Autowired
	AppPushService appPushService;
	
	@Autowired
	EventListMapper eventListMapper;
	
	@Autowired
	MatchingFireStoreMapper firestoreMapper;
	
	@Autowired
	Environment env;
	
	Firestore db ;
	
	@PostConstruct
	public void init() throws IOException {
		System.out.println("#### profile : " + env.getActiveProfiles()[0]);
		if("prod".equals(env.getActiveProfiles()[0])) {
			db = FireBaseUtil.connectionFireStore("prodFireStoreAccount.json");
		}else {
			db = FireBaseUtil.connectionFireStore("stgFireStoreAccount.json");
		}
	}

	@Override
	@Transactional
	public void beforeMatchingEndJob() throws IOException {
		// 매칭 상태 값이 GOING인 사람 중 3시간이 지난 매칭 건을 확인한다. 
		List<Matching> list = matchingMapper.getPastMatchingTimeList();
		
		if(!list.isEmpty()) {
			list.forEach(p ->{
				try {
					//1. 해당 매칭을 종료시킨다. -> cancel 
					matchingMapper.updateEndMatching(p.getMatchingNo());
					//2. 해당 유저들에 대한 매칭 상태를 변경해준다. -> NO_MATCH
					matchingMapper.updateMemNoMatching(p.getMemNo());
					//신청자
					matchingMapper.updateMemNoMatching(p.getDstMemNo());
					//수락자
					log.info("Change matching end job number : {}", p.getMatchingNo());
					
					DocumentReference  docRef = db.collection("purchings").document("matchingNo_" + p.getMatchingNo());
					ApiFuture<DocumentSnapshot> future = docRef.get();
					DocumentSnapshot document = future.get();
					if(document.exists()) {
						Map<String, Object> update = new HashMap<String, Object>();
						update.put("matchStatus", "CANCEL");
						ApiFuture<WriteResult> initialResult = db.collection("purchings").document("matchingNo_" + p.getMatchingNo()).update(update);//p.getMatchingNo());
						try {
							initialResult.get();
						}catch (Exception e) {
							firestoreMapper.insertMatchingFirestore(p.getMatchingNo(), e.getClass().toString(),"CANCEL");
							e.printStackTrace();
						}
						
					}else {
						firestoreMapper.insertMatchingFirestore(p.getMatchingNo(), "target matcingNo is null", "CANCEL");
					}
					
				}catch (Exception e) {
					log.info("EXCEPTION Matching No : {} Member NO : {} DstMember NO : {}"
											, p.getMatchingNo(),p.getMemNo(), p.getDstMemNo());
					e.printStackTrace();
				}
			});
		}
		log.info("Matching END Job Finish");
		 
		
		
//		db.collection("purchings").addSnapshotListener( (target, exception)->{
//			System.out.println(" - select start - "); 
//			target.forEach( item->{
//				System.out.println("primary id : "+item.getId() + "  ||  value : " + item.getData());
//			});
//			System.out.println(" - select end - ");
//		});
		
		return;
	}

	@Override
	@Transactional
	public void resetMatchingJob() throws IOException {
		//오전 5시 모든 매칭중인 건 검색 going 은 제외. 
		List<Matching> list = matchingMapper.getGoingMatchingList();
		if(!list.isEmpty()) {
			list.forEach(p ->{
				try {
					//1. 해당 매칭을 종료시킨다. -> end 
					matchingMapper.updateEndMatching(p.getMatchingNo());
					//2. 해당 유저들에 대한 매칭 상태를 변경해준다. -> NO_MATCH
					matchingMapper.updateMemNoMatching(p.getMemNo());
					//신청자
					matchingMapper.updateMemNoMatching(p.getDstMemNo());
					//수락자
					int firstMatchStatus = eventListMapper.getFirstMatchStatus(p.getMemNo());
					if(firstMatchStatus < 1) {
						int point = eventListMapper.getEventPoint(EventType.FIRST_MATCHING.toString());
						BigInteger targetNo = eventListMapper.getEventNo(EventType.FIRST_MATCHING.toString());
						int balance = eventListMapper.getBoneBalance(p.getMemNo());
						int plusBalance = balance + point;
						eventListMapper.insertBalance(BoneHistory.builder().build().reqToEntityBoneHistoryEventInsert(p.getMemNo(), targetNo, "FIRST_MATCHING", point, plusBalance));
						eventListMapper.updateBalance(p.getMemNo(), plusBalance);
						appPushService.pushSend(p.getMemNo(), EventType.FIRST_MATCHING.toString());
					}
					int dstFirstMatchStatus = eventListMapper.getFirstMatchStatus(p.getDstMemNo());
					if(dstFirstMatchStatus < 1) {
						int point = eventListMapper.getEventPoint(EventType.FIRST_MATCHING.toString());
						BigInteger targetNo = eventListMapper.getEventNo(EventType.FIRST_MATCHING.toString());
						int balance = eventListMapper.getBoneBalance(p.getDstMemNo());
						int plusBalance = balance + point;
						eventListMapper.insertBalance(BoneHistory.builder().build().reqToEntityBoneHistoryEventInsert(p.getDstMemNo(), targetNo, "FIRST_MATCHING", point, plusBalance));
						eventListMapper.updateBalance(p.getDstMemNo(), plusBalance);
						appPushService.pushSend(p.getDstMemNo(), EventType.FIRST_MATCHING.toString());
					}
					
					DocumentReference  docRef = db.collection("purchings").document("matchingNo_" + p.getMatchingNo());
					ApiFuture<DocumentSnapshot> future = docRef.get();
					DocumentSnapshot document = future.get();
					System.out.println("###!@# : " + document.exists());
					if(document.exists()) {
						Map<String, Object> update = new HashMap<String, Object>();
						update.put("matchStatus", "END");
						ApiFuture<WriteResult> initialResult = db.collection("purchings").document("matchingNo_" + p.getMatchingNo()).update(update);//p.getMatchingNo());
						try {
							initialResult.get();
						}catch (Exception e) {
							firestoreMapper.insertMatchingFirestore(p.getMatchingNo(), e.getClass().toString(),"END");
							e.printStackTrace();
						}
						
					}else {
						firestoreMapper.insertMatchingFirestore(p.getMatchingNo(), "target matcingNo is null", "END");
					}
					
					log.info("Change matching reset job number : {}", p.getMatchingNo());
				}catch (Exception e) {
					log.info("EXCEPTION Matching No : {} Member NO : {} DstMember NO : {}"
											, p.getMatchingNo(),p.getMemNo(), p.getDstMemNo());
					e.printStackTrace();
				}
			});
		}
		log.info("Matching Reset Job Finish");
		return;
	}

	@Override
	public void firestoreFailRetry() throws IOException, InterruptedException, ExecutionException {
		List<MatchingFirestore> matchingFirestore = firestoreMapper.getFailedFirestore();
		
		if(matchingFirestore.size() == 0) {
			return;
		}
		
		if(!matchingFirestore.isEmpty()) {
//				matchingFirestore.forEach(p ->{
				for(int i = 0; i < matchingFirestore.size(); i++) {
					String status = matchingFirestore.get(i).getMatchStatus();
					BigInteger matchingFirestoreNo = matchingFirestore.get(i).getMatchingFirestoreNo();
					System.out.println(matchingFirestore.get(i).getMatchingNo());
					System.out.println("matchingNo_" + matchingFirestore.get(i).getMatchingNo());
					DocumentReference  docRef = db.collection("purchings").document("matchingNo_" + matchingFirestore.get(i).getMatchingNo());
					ApiFuture<DocumentSnapshot> future = docRef.get();
					DocumentSnapshot document = future.get();
					if(document.exists()) {
						Map<String, Object> update = new HashMap<String, Object>();
						update.put("matchStatus", status);
						ApiFuture<WriteResult> initialResult = db.collection("purchings").document("matchingNo_" + matchingFirestore.get(i).getMatchingNo()).update(update);//p.getMatchingNo());
						try {
							initialResult.get();
							firestoreMapper.updateMatchingFirestore(matchingFirestoreNo, "Y");                                                                               
						}catch (Exception e) {
							firestoreMapper.updateMatchingFirestore(matchingFirestoreNo, "N");
							e.printStackTrace();
						}
						
					}else {
						firestoreMapper.updateMatchingFirestore(matchingFirestoreNo, "N");
					}
				};
		}
	}

	@Override
	public void retrySMSpush() throws Exception {
		int matchingFirestoreCnt = firestoreMapper.getFailedFirestoreCnt3();
		
		if(matchingFirestoreCnt != 0 ) {
			SMSUtil.sendSMS("01040116508", "Matching Firestore에 " + matchingFirestoreCnt + "개의 문제가 발생");
		}
	}
}
