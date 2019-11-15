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

public class Shop_now_customer_signup_confirm implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings("unchecked")
	public JSONObject handleRequest(JSONObject input, Context context) {
		LambdaLogger logger = context.getLogger();
		CognitoHelper helper = new CognitoHelper();
		String url = "";
		String username = "root";
		String password = "";

		Connection conn;
		Statement stmt = null;
		ResultSet resultSet;
		ResultSet resultSet1 = null;

		final String customermail = input.get("email").toString();
		final String code = input.get("code").toString();

		// --SignUp Confirmation to User Pool--//

		/*
		 * boolean success = helper.VerifyAccessCode(username, code); if (success) {
		 * logger.log("User verification succeeded."); } else {
		 * logger.log("User verification failed."); }
		 */
		// ---END--///

		JSONObject jsonObject_Register_customer_result = new JSONObject();

		String strMsg;

		final String sql = "UPDATE wsimcpsn_shopnow.customers SET status = '1' WHERE email ='" + customermail + "'";

		if (customermail == "" || customermail == null) {
			if (code == "" || code == null) {

				strMsg = "Email-id and Verification Code cannot be empty";
				jsonObject_Register_customer_result.put("status", "0");
				jsonObject_Register_customer_result.put("message", strMsg);
				System.out.println("null");
			} else {
				strMsg = "Email-Id cannot be empty";
				jsonObject_Register_customer_result.put("status", "0");
				jsonObject_Register_customer_result.put("message", strMsg);
				System.out.println(strMsg);

			}
		} else if (code == "" || code == null) {

			strMsg = "Verification Code cannot be empty";
			jsonObject_Register_customer_result.put("status", "0");
			jsonObject_Register_customer_result.put("message", strMsg);
			System.out.println(strMsg);

		}

		else {
			boolean success = helper.VerifyAccessCode(customermail, code);
			if (success) {
				try {
					conn = DriverManager.getConnection(url, username, password);
					stmt = conn.createStatement();
					resultSet = stmt.executeQuery("SELECT id FROM wsimcpsn_shopnow.customers where email='"	+ customermail + "'");

					if (resultSet.next()) {

						try {
							int count = stmt.executeUpdate(sql);
							logger.log("Number of record updated=" + count);
							resultSet1 = stmt
									.executeQuery("SELECT id FROM wsimcpsn_shopnow.customers order by id DESC LIMIT 1");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						int id1 = 0;
						if (resultSet1.first()) {
							id1 = resultSet1.getInt("id");

						}

						strMsg = "User Confirmed Successfully";
						jsonObject_Register_customer_result.put("status", "1");
						jsonObject_Register_customer_result.put("message", strMsg);
						jsonObject_Register_customer_result.put("id", id1);
					} else {
						strMsg = "No User Found";
						jsonObject_Register_customer_result.put("status", "0");
						jsonObject_Register_customer_result.put("message", strMsg);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return jsonObject_Register_customer_result;

	}
}