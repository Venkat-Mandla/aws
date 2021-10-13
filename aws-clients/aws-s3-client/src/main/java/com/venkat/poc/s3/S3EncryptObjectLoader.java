/**
 * 
 */
package com.venkat.poc.s3;

import java.io.File;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.KMSEncryptionMaterials;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;

/**
 * @author VenkaT
 *
 */
public class S3EncryptObjectLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AWSKMS kmsClient = AWSKMSClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_1)
				.build();
		AmazonS3 s3Client=AmazonS3EncryptionClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withEncryptionMaterials(new KMSEncryptionMaterialsProvider(new KMSEncryptionMaterials("28bd3926-15ab-4da7-b539-a7504fb04aba")))
				.withKmsClient(kmsClient)
				.build();
		System.out.println("Object upload started..");
		s3Client.putObject("poc-bin-one", "test.json", new File("C:\\VenkaT\\AWS\\test.json"));
		System.out.println("Object successfully uploaded..");

	}

}
