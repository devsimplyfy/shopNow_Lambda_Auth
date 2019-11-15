
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

public class Shop_now_customer_forgot_password_confirm implements RequestHandler<JSONObject, JSONObject> {

	@SuppressWarnings("unchecked")
	public JSONObject handleRequest(JSONObject input, Context context) {
		CognitoHelper helper = new CognitoHelper();
		LambdaLogger logger = context.getLogger();

		String email = input.get("email").toString();
		String password = input.get("new_password").toString();
		String code = input.get("code").toString();
		String str_msg = null;
		JSONObject jo_change_psw = new JSONObject();

		// --RESET CONFIRM REQUESTED----//

		String confirmation = helper.UpdatePassword(email, password, code);
		if (confirmation != null) {
			logger.log("Reset password confirmed: " + confirmation);

			String sql = "select * from wsimcpsn_shopnow.customers  where email='" + email + "'";
			String url = "";
			String username_db = "root";
			String password_db = "";
			Connection conn;
			Statement stmt = null;

			try {
				conn = DriverManager.getConnection(url, username_db, password_db);
				stmt = conn.createStatement();
				ResultSet forgot_user_mailid = stmt.executeQuery(sql);

				if (forgot_user_mailid.next() == true) {
					final String sql_update = "update wsimcpsn_shopnow.customers set password='" + password
							+ "' where email='" + email + "'";
					Statement stmt1 = conn.createStatement();
					int i = stmt1.executeUpdate(sql_update);
					if (i > 0) {
						str_msg = "Password Updated Successfully";
						jo_change_psw.put("status", "1");
						jo_change_psw.put("message", str_msg);
					} else {
						str_msg = "Password Not Updated Successfully";
						jo_change_psw.put("status", "0");
						jo_change_psw.put("message", str_msg);

					}

				} else {
					str_msg = "No user found with this Email Id";
					jo_change_psw.put("status", "0");
					jo_change_psw.put("message", str_msg);

				}

			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}


		} else {
			logger.log("Reset password procedure failed.");
			jo_change_psw.put("message", "Reset password procedure failed.");
		}

		// ---END---//
		return jo_change_psw;

	}

}