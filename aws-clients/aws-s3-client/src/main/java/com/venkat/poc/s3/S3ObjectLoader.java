/**
 * 
 */
package com.venkat.poc.s3;

import java.io.File;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * @author VenkaT
 *
 */
public class S3ObjectLoader {

	/**
	 * @param args
	 */
	public static void main1(String[] args) {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_EAST_1)
				.build();
		System.out.println("Object upload started..");
		s3Client.putObject("poc-bin-one", "test1.json", new File("C:/VenkaT/AWS/test.json"));
		System.out.println("Object successfully uploaded..");

	}
	
	public static void main2(String[] args) {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_EAST_1)
				.build();
		System.out.println("Object upload started..");
		s3Client.putObject("poc-bin-one", "test1.json", new File("C:/VenkaT/AWS/test.json"));
		System.out.println("Object successfully uploaded..");

	}
	
	public static void main(String[] args) {
		AmazonS3 s3Client=AmazonS3Client.builder()
				.withRegion(Regions.US_EAST_1)
				.build();
		System.out.println("Object upload started..");
		s3Client.putObject("vr-lab-inbound-file-bin", "test1.txt", new File("C:/VenkaT/AWS/test_vr.txt"));
		System.out.println("Object successfully uploaded..");

	}

}
