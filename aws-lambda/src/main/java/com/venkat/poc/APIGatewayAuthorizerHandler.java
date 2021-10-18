/**
 * 
 */
package com.venkat.poc;

import java.util.Objects;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.venkat.poc.domain.AuthPolicy;
import com.venkat.poc.domain.TokenAuthorizerContext;

/**
 * @author VenkaT
 *
 */
public class APIGatewayAuthorizerHandler implements RequestHandler<TokenAuthorizerContext, AuthPolicy> {

	private LambdaLogger logger;
    
	@Override
    public AuthPolicy handleRequest(TokenAuthorizerContext input, Context context) {
        buildLogger(context);
    	String token = input.getAuthorizationToken();
    	logger.log("Request token is - "+token);
    	// validate the incoming token
        // and produce the principal user identifier associated with the token

        // this could be accomplished in a number of ways:
        // 1. Call out to OAuth provider
        // 2. Decode a JWT token in-line
        // 3. Lookup in a self-managed DB
        String principalId = "xxxx";

        // if the client token is not recognized or invalid
        // you can send a 401 Unauthorized response to the client by failing like so:
        // throw new RuntimeException("Unauthorized");

        // if the token is valid, a policy should be generated which will allow or deny access to the client

        // if access is denied, the client will receive a 403 Access Denied response
        // if access is allowed, API Gateway will proceed with the back-end integration configured on the method that was called

    	String methodArn = input.getMethodArn();
    	String[] arnPartials = methodArn.split(":");
    	String region = arnPartials[3];
    	String awsAccountId = arnPartials[4];
    	String[] apiGatewayArnPartials = arnPartials[5].split("/");
    	String restApiId = apiGatewayArnPartials[0];
    	String stage = apiGatewayArnPartials[1];
    	String httpMethod = apiGatewayArnPartials[2];
    	String resource = ""; // root resource
    	if (apiGatewayArnPartials.length == 4) { 
    		resource = apiGatewayArnPartials[3];
    	}
    	
    	if(Objects.isNull(input.getAuthorizationToken())) {
    		return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId, restApiId, stage));
    	}

        // this function must generate a policy that is associated with the recognized principal user identifier.
        // depending on your use case, you might store policies in a DB, or generate them on the fly

        // keep in mind, the policy is cached for 5 minutes by default (TTL is configurable in the authorizer)
        // and will apply to subsequent calls to any method/resource in the RestApi
        // made with the same token

        // the example policy below denies access to all resources in the RestApi
        return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getAllowAllPolicy(region, awsAccountId, restApiId, stage));
    }

	private void buildLogger(Context context) {
		if(Objects.isNull(logger)) {
        	logger = context.getLogger();
        }
	}

}

