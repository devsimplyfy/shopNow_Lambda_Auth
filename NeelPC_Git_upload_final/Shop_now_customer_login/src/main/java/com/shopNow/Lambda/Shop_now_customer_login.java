package com.shopNow.Lambda;

import com.shopNow.Lambda.CognitoHelper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONObject;

public class Shop_now_customer_login implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings("unchecked")
	public JSONObject handleRequest(JSONObject input, Context context) {
		JSONObject jsonObject_login_result = new JSONObject();
		CognitoHelper helper = new CognitoHelper();
		LambdaLogger logger = context.getLogger();
		logger.log("Invoked JDBCSample.getCurrentTime");

		final String email = input.get("email").toString();
		final String psw = input.get("password").toString();

		// --USER POOL LOGIN--//

		try {
			String result = helper.ValidateUser(email, psw);
			if (result != null) {
				logger.log("User is authenticated: " + result);
				
				String strMsg;

				if (email == "" || email == null) {
					if (psw == "" || psw == null) {

						strMsg = "Email-Id and Password cannot be empty.";
						jsonObject_login_result.put("status", "0");
						jsonObject_login_result.put("message", strMsg);

					} else {

						strMsg = "Email-Id cannot be empty";
						jsonObject_login_result.put("status", "0");
						jsonObject_login_result.put("message", strMsg);

					}

				} else if (psw == "" || psw == null) {

					strMsg = "Password cannot be empty";
					jsonObject_login_result.put("status", "0");
					jsonObject_login_result.put("message", strMsg);

				} else {

					// Get time from DB server
					try {
						String url = "";
						String username = "root";
						String password = "";
						Connection conn = DriverManager.getConnection(url, username, password);

						Statement stmt = conn.createStatement();
						ResultSet resultSet = stmt.executeQuery("SELECT id FROM wsimcpsn_shopnow.customers where email='"
								+ email + "' and password='" + psw + "'");

						if (resultSet.next()) {

							strMsg = "Login Sucessfull";
							jsonObject_login_result.put("status", "1");
							jsonObject_login_result.put("message", strMsg);
							jsonObject_login_result.put("id_token", result);
						} else {

							strMsg = "Incorrect Email-Id or Password";
							jsonObject_login_result.put("status", "0");
							jsonObject_login_result.put("message", strMsg);
						}

					} catch (Exception e) {
						e.printStackTrace();
						logger.log("Caught exception: " + e.getMessage());
					}

				}

			} else {
				logger.log("User is not authenticated");
				jsonObject_login_result.put("message", "User is not authenticated");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			jsonObject_login_result.put("Exception",e1.toString());
		}

		// ---END---//

		return jsonObject_login_result;
	}
}
