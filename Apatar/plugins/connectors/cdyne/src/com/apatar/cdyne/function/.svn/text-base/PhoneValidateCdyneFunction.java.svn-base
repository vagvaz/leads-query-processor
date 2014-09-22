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

package com.apatar.cdyne.function;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import com.apatar.cdyne.CdyneFunction;
import com.apatar.cdyne.ws.PhoneReturn;
import com.apatar.cdyne.ws.PhoneVerify;
import com.apatar.cdyne.ws.PhoneVerifyLocator;
import com.apatar.cdyne.ws.PhoneVerifySoap_PortType;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class PhoneValidateCdyneFunction extends CdyneValueAbstractETLFunction
		implements CdyneFunction {

	public Object execute(List l) {

		String phone = (String) l.get(0);
		if (phone == null) {
			return null;
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
				String phoneV = rs.getString("phone");
				if (phone.equals(phoneV)) {
					PhoneReturn pr = new PhoneReturn();

					pr.setCompany(rs.getString("PhoneReturn___Company"));
					pr.setValid(rs.getBoolean("PhoneReturn___Valid"));
					pr.setUse(rs.getString("PhoneReturn___Use"));
					pr.setState(rs.getString("PhoneReturn___State"));
					pr.set_switch(rs.getString("PhoneReturn____switch"));
					pr.setRC(rs.getString("PhoneReturn___RC"));
					pr.setOCN(rs.getString("PhoneReturn___OCN"));
					pr.setOriginalNumber(rs
							.getString("PhoneReturn___OriginalNumber"));
					pr
							.setCleanNumber(rs
									.getString("PhoneReturn___CleanNumber"));
					pr.setSwitchName(rs.getString("PhoneReturn___SwitchName"));
					pr.setSwitchType(rs.getString("PhoneReturn___SwitchType"));
					pr.setCountry(rs.getString("PhoneReturn___Country"));
					pr.setCLLI(rs.getString("PhoneReturn___CLLI"));
					pr.setPrefixType(rs.getString("PhoneReturn___PrefixType"));
					pr.setLATA(rs.getString("PhoneReturn___LATA"));
					pr.setSms(rs.getString("PhoneReturn___Sms"));
					pr.setEmail(rs.getString("PhoneReturn___Email"));
					pr.setAssignDate(rs.getString("PhoneReturn___AssignDate"));
					pr
							.setTelecomCity(rs
									.getString("PhoneReturn___TelecomCity"));
					pr.setTelecomCounty(rs
							.getString("PhoneReturn___TelecomCounty"));
					pr.setTelecomState(rs
							.getString("PhoneReturn___TelecomState"));
					pr.setTelecomZip(rs.getString("PhoneReturn___TelecomZip"));
					pr.setTimeZone(rs.getString("PhoneReturn___TimeZone"));
					pr.setLat(rs.getString("PhoneReturn___Lat"));
					pr.set_long(rs.getString("PhoneReturn____long"));
					pr.setWireless(rs.getBoolean("PhoneReturn___Wireless"));

					rs.close();
					st.close();

					return pr;
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			// Setup the service and soap implementations
			PhoneVerify service = new PhoneVerifyLocator();
			PhoneVerifySoap_PortType port = service.getPhoneVerifySoap();

			// Holder objects which will contain the returned results

			PhoneReturn pr = port.checkPhoneNumber(phone, licenseKey);

			KeyInsensitiveMap kim = new KeyInsensitiveMap();

			kim.put("phone", new JdbcObject(phone, Types.VARCHAR));
			kim.put("PhoneReturn___company", new JdbcObject(pr.getCompany(),
					Types.VARCHAR));
			kim.put("PhoneReturn___valid", new JdbcObject(pr.isValid(),
					Types.BOOLEAN));
			kim.put("PhoneReturn___use", new JdbcObject(pr.getUse(),
					Types.VARCHAR));
			kim.put("PhoneReturn___state", new JdbcObject(pr.getState(),
					Types.VARCHAR));
			kim.put("PhoneReturn____switch", new JdbcObject(pr.get_switch(),
					Types.VARCHAR));
			kim.put("PhoneReturn___RC", new JdbcObject(pr.getRC(),
					Types.VARCHAR));
			kim.put("PhoneReturn___OCN", new JdbcObject(pr.getOCN(),
					Types.VARCHAR));
			kim.put("PhoneReturn___originalNumber", new JdbcObject(pr
					.getOriginalNumber(), Types.VARCHAR));
			kim.put("PhoneReturn___cleanNumber", new JdbcObject(pr
					.getCleanNumber(), Types.VARCHAR));
			kim.put("PhoneReturn___switchName", new JdbcObject(pr
					.getSwitchName(), Types.VARCHAR));
			kim.put("PhoneReturn___switchType", new JdbcObject(pr
					.getSwitchType(), Types.VARCHAR));
			kim.put("PhoneReturn___country", new JdbcObject(pr.getCountry(),
					Types.VARCHAR));
			kim.put("PhoneReturn___CLLI", new JdbcObject(pr.getCLLI(),
					Types.VARCHAR));
			kim.put("PhoneReturn___prefixType", new JdbcObject(pr
					.getPrefixType(), Types.VARCHAR));
			kim.put("PhoneReturn___LATA", new JdbcObject(pr.getLATA(),
					Types.VARCHAR));
			kim.put("PhoneReturn___sms", new JdbcObject(pr.getSms(),
					Types.VARCHAR));
			kim.put("PhoneReturn___email", new JdbcObject(pr.getEmail(),
					Types.VARCHAR));
			kim.put("PhoneReturn___assignDate", new JdbcObject(pr
					.getAssignDate(), Types.VARCHAR));
			kim.put("PhoneReturn___telecomCity", new JdbcObject(pr
					.getTelecomCity(), Types.VARCHAR));
			kim.put("PhoneReturn___telecomCounty", new JdbcObject(pr
					.getTelecomCounty(), Types.VARCHAR));
			kim.put("PhoneReturn___telecomState", new JdbcObject(pr
					.getTelecomState(), Types.VARCHAR));
			kim.put("PhoneReturn___telecomZip", new JdbcObject(pr
					.getTelecomZip(), Types.VARCHAR));
			kim.put("PhoneReturn___timeZone", new JdbcObject(pr.getTimeZone(),
					Types.VARCHAR));
			kim.put("PhoneReturn___lat", new JdbcObject(pr.getLat(),
					Types.VARCHAR));
			kim.put("PhoneReturn____long", new JdbcObject(pr.get_long(),
					Types.VARCHAR));
			kim.put("PhoneReturn___wireless", new JdbcObject(pr.isWireless(),
					Types.BOOLEAN));

			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), node
					.getDBTempVerificationName(), node
					.getTempVerificationRecords(), ApplicationData
					.getTempJDBC()), kim);

			return pr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static FunctionInfo fi = new FunctionInfo("Verify Phone", 1, 0,
			new String[] { "Address Line 1", "Address Line 2", "City", "State",
					"Zip", "Firm", "Urbanization" }, null);
	static {
		fi.getCategories().add(FunctionCategory.Function);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
