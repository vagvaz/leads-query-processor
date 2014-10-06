/*
_______________________

Apatar Open Source Data Integration

Copyright (C) 2005-2007, Apatar, Inc.

info@apatar.com

195 Meadow St., 2nd Floor

Chicopee, MA 01013



    This program is free software; you can redistribute it and/or modify

    it under the terms of the GNU General Public License as published by

    the Free Software Foundation; either version 2 of the License, or

    (at your option) any later version.



    This program is distributed in the hope that it will be useful,

    but WITHOUT ANY WARRANTY; without even the implied warranty of

    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

    GNU General Public License for more details.



    You should have received a copy of the GNU General Public License along

    with this program; if not, write to the Free Software Foundation, Inc.,

    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

________________________

 */

package com.apatar.strikeiron.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.apatar.core.ApatarException;
import com.apatar.core.ApplicationData;
import com.apatar.core.CoreUtils;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.functions.FunctionInfo;
import com.apatar.strikeiron.StrikeIronFunction;
import com.apatar.strikeiron.ws.login.EmailVerification;
import com.apatar.strikeiron.ws.login.EmailVerificationLocator;
import com.apatar.strikeiron.ws.login.EmailVerificationSoap_PortType;
import com.apatar.strikeiron.ws.login.ValidateEmailResult;
import com.apatar.strikeiron.ws.login.holders.SISubscriptionInfoHolder;
import com.apatar.strikeiron.ws.login.holders.ValidateEmailResultHolder;
import com.apatar.ui.FunctionCategory;

public class EmailValidateStrikeIronFunction extends
		StrikeIronValueAbstractETLFunction implements StrikeIronFunction {

	public Object execute(List l) {

		String email = (String) l.get(0);
		if (email != null) {
			email = email.trim().toLowerCase();
		} else {
			return false;
		}

		try {
			if (!CoreUtils.validEmail(email)) {
				return false;
			}
		} catch (ApatarException e2) {
			e2.printStackTrace();
			return false;
		}

		SQLQueryString sqs = DataBaseTools.CreateSelectString(ApplicationData
				.getTempDataBaseInfo(),
				new SQLCreationData(node.getTempVerificationRecords(), node
						.getDBTempVerificationName()), null);
		try {
			Statement st = ApplicationData.getTempJDBCConnection()
					.createStatement();
			ResultSet rs = st.executeQuery(sqs.query);
			while (rs.next()) {
				String emailV = rs.getString("email");
				if (email.equalsIgnoreCase(emailV)) {
					rs.close();
					st.close();
					if (rs.getString("isValid").equalsIgnoreCase("true")) {
						return true;
					} else {
						return false;
					}
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		EmailVerification service = new EmailVerificationLocator();
		EmailVerificationSoap_PortType port;
		try {
			port = service.getEmailVerificationSoap();

			ValidateEmailResultHolder holder = new ValidateEmailResultHolder();
			SISubscriptionInfoHolder siHolder = new SISubscriptionInfoHolder();

			port.validateEmail("", userName, password, email, false, holder,
					siHolder);

			ValidateEmailResult result = holder.value;

			ApplicationData.ProcessingProgress.Log("StrikeIron: E-mail: "
					+ email + "-" + result.getIsValid());

			if (result.getIsValid().equalsIgnoreCase("VALID")) {
				KeyInsensitiveMap kim = new KeyInsensitiveMap();
				kim.put("email", email);
				kim.put("isValid", "true");
				DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
						.getTempDataBase().getDataBaseInfo(), node
						.getDBTempVerificationName(), node
						.getTempVerificationRecords(), ApplicationData
						.getTempJDBC()), kim);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		KeyInsensitiveMap kim = new KeyInsensitiveMap();
		kim.put("email", email);
		kim.put("isValid", "false");
		try {
			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), node
					.getDBTempVerificationName(), node
					.getTempVerificationRecords(), ApplicationData
					.getTempJDBC()), kim);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static FunctionInfo fi = new FunctionInfo("Verify Email Address", 1, 0);
	static {
		fi.getCategories().add(FunctionCategory.Function);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}
}
