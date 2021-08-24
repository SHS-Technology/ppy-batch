package com.puppyou.batch.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface MatchingService {
	
	public void beforeMatchingEndJob() throws IOException;
	
	public void resetMatchingJob() throws IOException;
	
	public void firestoreFailRetry() throws IOException, InterruptedException, ExecutionException;
	
	public void retrySMSpush() throws Exception;

}
