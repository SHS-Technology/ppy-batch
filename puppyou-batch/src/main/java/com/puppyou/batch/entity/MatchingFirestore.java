package com.puppyou.batch.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class MatchingFirestore {
	
	private BigInteger matchingFirestoreNo;
	private BigInteger matchingNo;
	private String matchStatus;
	private String failReason;
	private int retryCnt;
	private String commitYn;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
    
    

}
