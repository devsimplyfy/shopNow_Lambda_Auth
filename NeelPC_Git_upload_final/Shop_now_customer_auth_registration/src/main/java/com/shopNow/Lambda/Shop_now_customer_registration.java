package com.shopNow.Lambda;

import com.shopNow.Lambda.CognitoHelper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

public class Shop_now_customer_registration implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings("unchecked")
	public JSONObject handleRequest(JSONObject input, Context context) {
		CognitoHelper helper = new CognitoHelper();
		String url = "";
		String username = "root";
		String password = "";
				
		Connection conn;
		Statement stmt = null;
		ResultSet resultSet;
		ResultSet resultSet1 = null;

		final String customermail = input.get("email").toString();
		final String cust_psw = input.get("password").toString();
		final String phonenumber = input.get("phone_number").toString();
		
		
		JSONObject jsonObject_Register_customer_result = new JSONObject();

		String strMsg;

		final String sql = "INSERT INTO wsimcpsn_shopnow.customers(email,password,phone_number,status)VALUES('" + customermail + "','" + cust_psw + "','" + phonenumber + "','0')";

		if (customermail == "" || customermail == null) {
			if (cust_psw == "" || cust_psw == null) {

				strMsg = "Email-id and Passowrd cannot be empty";
				jsonObject_Register_customer_result.put("status", "0");
				jsonObject_Register_customer_result.put("message", strMsg);
				System.out.println("null");
			} else {
				strMsg = "Email-Id cannot be empty";
				jsonObject_Register_customer_result.put("status", "0");
				jsonObject_Register_customer_result.put("message", strMsg);
				System.out.println(strMsg);

			}
		} else if (cust_psw == "" || cust_psw == null) {

			strMsg = "Password cannot be empty";
			jsonObject_Register_customer_result.put("status", "0");
			jsonObject_Register_customer_result.put("message", strMsg);
			System.out.println(strMsg);

		}

		else {
			 boolean success = helper.SignUpUser(customermail, password, customermail, phonenumber);
			 if(success) {
			try {
				conn = DriverManager.getConnection(url, username, password);
				stmt = conn.createStatement();
				resultSet = stmt.executeQuery("SELECT id FROM wsimcpsn_shopnow.customers where email='" + customermail	+"'");

				if (resultSet.next()) {
					strMsg = "User already exists with this email-id";
					jsonObject_Register_customer_result.put("status", "0");
					jsonObject_Register_customer_result.put("message", strMsg);

				} else {

					try {
						int count = stmt.executeUpdate(sql);
						System.out.println("Number of recorde inserted=" + count);
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

					strMsg = "Registered Customer Successfully";
					jsonObject_Register_customer_result.put("status", "1");
					jsonObject_Register_customer_result.put("message", strMsg);
					jsonObject_Register_customer_result.put("id", id1);

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
			 else {
				 jsonObject_Register_customer_result.put("message", "Either enetered data is not valid or User exists already");
			 }
		}
		return jsonObject_Register_customer_result;
		
	}
}