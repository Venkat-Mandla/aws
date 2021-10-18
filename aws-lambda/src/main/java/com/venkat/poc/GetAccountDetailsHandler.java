/**
 * 
 */
package com.venkat.poc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * @author VenkaT
 *
 */
public class GetAccountDetailsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("request body - "+input.getBody());
		APIGatewayProxyResponseEvent output=new APIGatewayProxyResponseEvent();
		output.setBody("API Response - Successful");
		output.setStatusCode(200);
		return output;
	}

}
