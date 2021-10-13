/**
 * 
 */
package com.venkat.poc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Objects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.KMSEncryptionMaterials;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.venkat.poc.domain.Member;
import com.venkat.poc.exception.ParserException;

/**
 * @author VenkaT
 *
 */
public class SQSListenerHandler implements RequestHandler<SQSEvent, Void> {

	private static final String[] COLUMN_NAMES = new String[] { "id", "name", "company" };

	private ObjectMapper objectMapper;

	public SQSListenerHandler() {

		JsonFactory factory = new JsonFactory();
		factory.enable(Feature.CANONICALIZE_FIELD_NAMES);
		objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Override
	public Void handleRequest(SQSEvent event, Context context) {
		LambdaLogger logger=context.getLogger();
		logger.log("Message consumed from SQS..");
		for (SQSMessage msg : event.getRecords()) {
			try {
				logger.log("SQS Event: " + objectMapper.writeValueAsString(msg));
				Member member=getS3Object(msg,logger);
				saveDetailsInRDS(member,logger);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		logger.log("Message processing done..");
		return null;
	}

	private Member getS3Object(SQSMessage msg, LambdaLogger logger) throws JsonProcessingException, JsonMappingException {
		JsonNode jsonNode=null;
		try {
			jsonNode=objectMapper.readValue(msg.getBody(), JsonNode.class);
		}catch(Exception e) {
			e.printStackTrace();
			jsonNode=objectMapper.readTree(msg.getBody());
//			msg.getMd5OfBody()
		}
		Member member=new Member();
		logger.log("SQS Notification Message body : "+jsonNode);
		JsonNode records=jsonNode.get("Records");
		if (Objects.nonNull(records) && records.isArray()) {
		    for (final JsonNode objNode : records) {
		        logger.log("SQS Notification Record: "+ objNode);
		        JsonNode s3Node=objNode.get("s3");
		        JsonNode bucket = s3Node.get("bucket");
				JsonNode bodyObject = s3Node.get("object");

				try {

					String bucketName=bucket.get("name").textValue();
					String objectName=bodyObject.get("key").textValue();
					Regions awsRegion = Regions.fromName(msg.getAwsRegion());
					
					String s3Object = extractEncryptedContentFromS3(awsRegion, bucketName, objectName,logger);
					logger.log("Object successfully retriaved.." + s3Object);
					member = objectMapper.readValue(s3Object, Member.class);
					logger.log("S3Oject converted to Member object");
					
				} catch (Exception e) {
					throw new ParserException("Unable to Extract S3 Ojbect Details", e);
				}
		        
		    }
		}else {
			logger.log("No records found -"+records);
		}
		return member;
	}

	private void saveDetailsInRDS(Member member, LambdaLogger logger) {
		Connection connection = null;
		// String
		// host="jdbc:mysql://database-mysql-poc.cpcnpcxbmsiy.us-east-1.rds.amazonaws.com:3306";
		try {
			logger.log("Trying to get the connection...");
			String secretName = "dev/mysql-poc";
			String region = "us-east-1";
			String secretDetails = getSecret(secretName,region,logger);
			JsonNode secretNode = objectMapper.readTree(secretDetails);
			String host = "jdbc:mysql://"+secretNode.get("host").asText()+":"+secretNode.get("port");
			logger.log("Host of RDS - " + host);
			String username = secretNode.get("username").asText();
			logger.log("username of RDS - " + username);
			String password = secretNode.get("password").textValue();
			// logger.log("pwd of RDS - "+password);

			connection = DriverManager.getConnection(host, username, password);

			logger.log("Connection forked...");
			PreparedStatement preparedStatement = connection.prepareStatement("insert into dev.member values (?,?,?,?,?)",
					COLUMN_NAMES);
			preparedStatement.setInt(1, member.getId());
			preparedStatement.setString(2, member.getName());
			preparedStatement.setString(3, member.getCompany());
			preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			// Statement statement=connection.createStatement();
			int insertFlag = preparedStatement.executeUpdate();
			logger.log("Number of records inserted: " + insertFlag);
		} catch (SQLException | JsonProcessingException e) {
			e.printStackTrace();
		} finally {
			if (Objects.nonNull(connection)) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String extractedContentFromS3(Regions awsRegion, String bucketName, String objectName, LambdaLogger logger) {
		AmazonS3 s3Client = AmazonS3Client.builder().withRegion(awsRegion).build();
		logger.log("Object retriaval started..with connection - " + s3Client);
		String s3Object = s3Client.getObjectAsString(bucketName, objectName);
		logger.log("S3Object Content: " + s3Object);
		return s3Object;
	}
	
	private String extractEncryptedContentFromS3(Regions awsRegion, String bucketName, String objectName, LambdaLogger logger) {
		AWSKMS kmsClient = AWSKMSClientBuilder
				.standard()
				.withRegion(awsRegion)
				.build();
		String secretName = "kms-customer-master-key";
		String region = "us-east-1";
		AmazonS3 s3Client=AmazonS3EncryptionClientBuilder.standard()
				.withRegion(awsRegion)
				.withEncryptionMaterials(new KMSEncryptionMaterialsProvider(new KMSEncryptionMaterials(getSecret(secretName, region,logger))))
				.withKmsClient(kmsClient)
				.build();
		logger.log("Object retriaval started..with connection - " + s3Client);
		String s3Object = s3Client.getObjectAsString(bucketName, objectName);
		logger.log("S3Object Content: " + s3Object);
		return s3Object;
	}

	public static String getSecret(String secretName, String region, LambdaLogger logger) {

		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
		String secret;
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;

		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
		} catch (Exception e) {
			throw e;
		}
		if (getSecretValueResult.getSecretString() != null) {
			secret = getSecretValueResult.getSecretString();
		} else {
			secret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
		}
		logger.log("SecretDetails - " + secret);
		return secret;
	}
	
}
