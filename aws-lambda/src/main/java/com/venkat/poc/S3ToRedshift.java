/**
 * 
 */
package com.venkat.poc;

import java.util.List;
import java.util.Objects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.glue.AWSGlue;
import com.amazonaws.services.glue.AWSGlueClientBuilder;
import com.amazonaws.services.glue.model.StartJobRunRequest;
import com.amazonaws.services.glue.model.StartJobRunResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3Entity;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author VenkaT
 *
 */
public class S3ToRedshift implements RequestHandler<S3Event, Void>{
	
	ObjectMapper objectMapper;
	LambdaLogger logger;
	public S3ToRedshift() {
		objectMapper=new ObjectMapper();
	}

	@Override
	public Void handleRequest(S3Event input, Context context) {
		if(Objects.isNull(logger)) {
			logger = context.getLogger();
		}
		logger.log("S3ToRedshift labmda function triggered");
		List<S3EventNotificationRecord> eventNotifications = input.getRecords();
		if(Objects.nonNull(eventNotifications) && eventNotifications.size()>0) {
			for (S3EventNotificationRecord s3EventNotificationRecord : eventNotifications) {
				try {
					String jsonString = objectMapper.writeValueAsString(s3EventNotificationRecord);
					logger.log("s3 event + "+jsonString);
					processS3Event(s3EventNotificationRecord);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				
			}
			
		}else {
			logger.log("Glue Job did not trigger - please validate the event message :"+input);
		}
		return null;
	}

	private void processS3Event(S3EventNotificationRecord s3EventNotificationRecord) {
		S3Entity s3Entity = s3EventNotificationRecord.getS3();
		String bucketName=s3Entity.getBucket().getName();
		String jobName;
		if(StringUtils.beginsWithIgnoreCase(bucketName, "vr-lab-inbound-file-bin")) {
			jobName="transformAndSendToS3";
		}else if(StringUtils.beginsWithIgnoreCase(bucketName, "vr-lab-outbound-file-bin")) {
			jobName="s3ToRedshift";
		}else {
			throw new IllegalArgumentException("Job is not configured for bucket - "+bucketName);
		}
		AWSGlue glueClient=AWSGlueClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		StartJobRunRequest startJobRunRequest = new StartJobRunRequest();
		startJobRunRequest.setJobName(jobName);
		StartJobRunResult jobTriggerResults = glueClient.startJobRun(startJobRunRequest);
		logger.log("Job results - "+jobTriggerResults);
		
	}

}
