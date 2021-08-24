package com.puppyou.batch.mapper;

import java.math.BigInteger;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.puppyou.batch.entity.MatchingFirestore;

public interface MatchingFireStoreMapper {
	
	public void insertMatchingFirestore(@Param("matchingNo") BigInteger matchingNo, @Param("failReason") String failReason, @Param("matchStatus") String matchStatus);
	
	public List<MatchingFirestore> getFailedFirestore();
	public int getFailedFirestoreCnt3();
	
	public void updateMatchingFirestore(@Param("matchingFirestoreNo") BigInteger matchingFirestoreNo , @Param("commitYn") String commitYn);

}
