
package com.shopNow.Lambda;

import com.shopNow.Lambda.CognitoHelper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class Shop_now_customer_forgot_password implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings("unchecked")
	public JSONObject handleRequest(JSONObject input, Context context) {
		 CognitoHelper helper = new CognitoHelper();
		LambdaLogger logger = context.getLogger();
		
		String email = input.get("email").toString();
		String str_msg = null;
		JSONObject jo_change_psw = new JSONObject();

		//--RESET REQUESTED----//
		
		
		String result = helper.ResetPassword(email);
        if (result != null) {
           // System.out.println("Reset password code sent: " + result);
            jo_change_psw.put("message", "Reset password code sent: " + result);
        } else {
           logger.log("Reset password procedure failed.");
           jo_change_psw.put("message", "Reset password procedure failed.");
            
        }

		//---END---//
		
		
	return jo_change_psw;

	}

}