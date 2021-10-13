/**
 * 
 */
package com.venkat.poc.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;

/**
 * @author VenkaT
 *
 */
public class S3GetObject {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_EAST_1)
				.build();
		System.out.println("Object retriaval started..with connection"+s3Client);

		String bucketName = "vr-lab-inbound-file-bin";
		String objectKey = "test1.txt";
		S3Object s3Object = s3Client.getObject(bucketName, objectKey);
		System.out.println("S3Object Content: "+s3Object.getObjectContent().toString());
		
		//String s3ObjectStr = s3Client.getObjectAsString("poc-bin-one", "test.json");
		//System.out.println("S3Object Content: "+s3ObjectStr);
		
		System.out.println("Object successfully uploaded..");
		
		File outputFile=transformText(s3Object);
		
		String outputBucketName = "vr-lab-outbound-file-bin";
		String outputFileKey = "test_vr.json";
		s3Client.putObject(outputBucketName, outputFileKey, outputFile);
		System.out.println("Object successfully uploaded..");

		URL url = null;

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 15; // 15 Minutes
		expiration.setTime(msec);

		url = s3Client.generatePresignedUrl(bucketName, objectKey, expiration, HttpMethod.GET);

		System.out.println("Input File URL - "+ url);
		URL outputPresignedUrl = s3Client.generatePresignedUrl(outputBucketName, outputFileKey, expiration, HttpMethod.GET);
		System.out.println("Output File URL - "+ outputPresignedUrl);

	}
	
	
	public static void main1(String[] args) throws IOException {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_WEST_2)
				.build();
		System.out.println("Object retriaval started..with connection"+s3Client);

		String bucketName = "vr-inbound-csv-file-bin";
		String objectKey = "test1.txt";
		S3Object s3Object = s3Client.getObject(bucketName, objectKey);
		System.out.println("S3Object Content: "+s3Object.getObjectContent().toString());
		
		//String s3ObjectStr = s3Client.getObjectAsString("poc-bin-one", "test.json");
		//System.out.println("S3Object Content: "+s3ObjectStr);
		
		System.out.println("Object successfully uploaded..");
		
		File outputFile=transformText(s3Object);
		
		String outputBucketName = "vr-inbound-json-file-bin";
		String outputFileKey = "test_vr.json";
		s3Client.putObject(outputBucketName, outputFileKey, outputFile);
		System.out.println("Object successfully uploaded..");

		URL url = null;

		java.util.Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 15; // 15 Minutes
		expiration.setTime(msec);

		url = s3Client.generatePresignedUrl(bucketName, objectKey, expiration, HttpMethod.GET);

		System.out.println("Input File URL - "+ url);
		URL outputPresignedUrl = s3Client.generatePresignedUrl(outputBucketName, outputFileKey, expiration, HttpMethod.GET);
		System.out.println("Output File URL - "+ outputPresignedUrl);

	}
	
	
	private static File transformText(S3Object s3Object) throws IOException {
		File transformedFile = new File("transformedfile.txt");
		String inputLine = null;
		StringBuffer outputStrBuf = new StringBuffer(1024);
		outputStrBuf.append("[\n");

		try {
			Scanner s = new Scanner(s3Object.getObjectContent());
			FileOutputStream fos = new FileOutputStream(transformedFile);
			s.useDelimiter("\n");
			while (s.hasNextLine()) {
				inputLine = s.nextLine();
				outputStrBuf.append(transformLineToJson(inputLine));
			}
			// Remove trailing comma at the end of the content. Close the array.
			outputStrBuf.deleteCharAt(outputStrBuf.length() - 2);
			outputStrBuf.append("]\n");
			fos.write(outputStrBuf.toString().getBytes());
			fos.flush();
			fos.close();
			s.close();

		} catch (IOException e) {
			System.out.println("DataTransformer: Unable to create transformed file");
			e.printStackTrace();
		}

		return transformedFile;
	}
	
	public static final String[] ATTRS = { "id", "name","company" };
	
	private static String transformLineToJson(String inputLine) {
		String[] inputLineParts = inputLine.split(",");
		int len = inputLineParts.length;

		String jsonAttrText = "{\n  " + "Json Data transfer" + "\n";
		for (int i = 0; i < len; i++) {
			jsonAttrText = jsonAttrText + "  \"" + ATTRS[i] + "\"" + ":" + "\"" + inputLineParts[i] + "\"";
			if (i != len - 1) {
				jsonAttrText = jsonAttrText + ",\n";
			} else {
				jsonAttrText = jsonAttrText + "\n";
			}
		}
		jsonAttrText = jsonAttrText + "},\n";
		return jsonAttrText;
	}
	
	
	public static void main2(String[] args) {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_EAST_1)
				.build();
		System.out.println("Object retriaval started..with connection"+s3Client);

		S3Object s3Object = s3Client.getObject("poc-bin-one", "test.json");
		System.out.println("S3Object Content: "+s3Object.getObjectContent().toString());
		
		String s3ObjectStr = s3Client.getObjectAsString("poc-bin-one", "test.json");
		System.out.println("S3Object Content: "+s3ObjectStr);
		
		System.out.println("Object successfully uploaded..");
		
		
	}

}
