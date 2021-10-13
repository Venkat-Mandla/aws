/**
 * 
 */
package com.venkat.poc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author VenkaT
 *
 */
public class SQSListenerHandlerTest {
	
//	@Test
	public void testHandler() throws JsonParseException, JsonMappingException, IOException {
		SQSListenerHandler handler=new SQSListenerHandler();
		SQSEvent event=new SQSEvent();
		ArrayList records = new ArrayList();
		SQSMessage message=new SQSMessage();
			
		ObjectMapper mapper=new ObjectMapper();
		//JsonNode value=mapper.readValue(new File("src/test/java/test.json"),JsonNode.class);
		
		String text=null;
		try (Scanner scanner = new Scanner( new File("src/test/java/test1.json"), "UTF-8" )) {
		    text = scanner.useDelimiter("\\A").next();
		}
		
		message.setBody(text);
		records.add(message);
		event.setRecords(records);
		//handler.handleRequest(event, null);
		
		System.out.println(Regions.fromName("us-east-1"));

	}
	
//private void processAndSaveInRds1(SQSMessage msg) throws JsonMappingException, JsonProcessingException {
//
//		
//		Gson gson=new Gson();
//		String json=gson.toJson(msg.getBody(), String.class);
//		
//		System.out.println("GSon : "+json);
//		//json=StringEscapeUtils.escapeJson(json);
//		
//	//	JsonNode node=gson.fromJson(json, JsonNode.class);
//		//System.out.println("JSONObject : "+jsonObject);
//		//JsonContent jsonContent=new JsonContent(msg.getBody().getBytes(), new ArrayNode(JsonNodeFactory.instance));
//		
//		
//		// json = json.replace("\"[","[").replace("]\"", "]").replace("\\\"", "\"").replace("\n", "");
//		// System.out.println("Json after StringEscapeUtils: "+json);
//		 
//		// json=json.replace(": \"{", ": {").replace(" : \\\"{", " : {").replace("}\",","},").replace("}\" ,","},").replace("}\\\",", "},");
//		// System.out.println("Json after replace: "+json);
//		 Map<String, Object> variables = new LinkedHashMap<>();
//		    ;
//		    if (json != null) {
//		        variables = new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
//		        });
//		    }
//		System.out.println("JsonNode" + variables);
//		
//		//JsonNode messages = jsonNode.get("Message");
//		//System.out.println("Message : "+messages);
//		
//		////if(Objects.isNull(messages)) {
//		///	return;
//		//}
//		JsonNode jsonNode = new ObjectMapper().readTree(json);//jsonContent.getJsonNode();//	
//		JsonNode records=jsonNode.get("Records");
//		
//		if (Objects.nonNull(records) && records.isArray()) {
//		    for (final JsonNode objNode : records) {
//		        System.out.println(objNode);
//		        
//		        JsonNode bucket = objNode.get("bucket");
//				JsonNode bodyObject = objNode.get("object");
//
//				System.out.println("Bucket " + bucket);
//				System.out.println("Object " + bodyObject);
//				System.out.println("awsRegion " + jsonNode.get("awsRegion"));
//
//				try {
//
//					System.out.println("Object Key " + bodyObject.get("key"));
//					System.out.println("Bucket Name " + bucket.get("name"));
//
//					Regions awsRegion=Regions.fromName(jsonNode.get("awsRegion").textValue());
//					
//					String bucketName=bucket.get("name").textValue();
//					String objectName=bodyObject.get("key").toString();
//					
//					//S3Object s3Object=extractedContentFromS3(awsRegion, bucketName, objectName);
//					System.out.println("Object successfully retriaved..");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		        
//		    }
//		}else {
//			System.out.println("No records found -"+records);
//		}
//		
//
//	}

}
