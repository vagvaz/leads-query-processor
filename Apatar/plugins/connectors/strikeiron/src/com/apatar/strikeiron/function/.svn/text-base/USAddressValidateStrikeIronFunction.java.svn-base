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
import java.sql.Types;
import java.util.List;

import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.functions.FunctionInfo;
import com.apatar.strikeiron.StrikeIronFunction;
import com.apatar.strikeiron.ws.usaddress.CasingEnum;
import com.apatar.strikeiron.ws.usaddress.USAddress;
import com.apatar.strikeiron.ws.usaddress.USAddressVerification;
import com.apatar.strikeiron.ws.usaddress.USAddressVerificationLocator;
import com.apatar.strikeiron.ws.usaddress.USAddressVerificationSoap_PortType;
import com.apatar.strikeiron.ws.usaddress.holders.SISubscriptionInfoHolder;
import com.apatar.strikeiron.ws.usaddress.holders.USAddressHolder;
import com.apatar.ui.FunctionCategory;

public class USAddressValidateStrikeIronFunction extends
		StrikeIronValueAbstractETLFunction implements StrikeIronFunction {

	public Object execute(List l) {

		String addressLine1 = (String) l.get(0);
		String addressLine2 = (String) l.get(1);
		String cityStateZip = (String) l.get(2) + " " + l.get(3) + " "
				+ l.get(4);
		String firm = (String) l.get(5);
		String urbanization = (String) l.get(6);

		SQLQueryString sqs = DataBaseTools.CreateSelectString(ApplicationData
				.getTempDataBaseInfo(),
				new SQLCreationData(node.getTempVerificationRecords(), node
						.getDBTempVerificationName()), null);
		try {
			Statement st = ApplicationData.getTempJDBCConnection()
					.createStatement();
			ResultSet rs = st.executeQuery(sqs.query);
			while (rs.next()) {
				String address1V = rs.getString("address1");
				String address2V = rs.getString("address2");
				String cityStateZipV = rs.getString("cityStateZip");
				if (addressLine1.equalsIgnoreCase(address1V)
						|| addressLine2.equalsIgnoreCase(address2V)
						|| cityStateZip.equalsIgnoreCase(cityStateZipV)) {

					USAddress usa = new USAddress();

					usa.setAddressLine1(rs
							.getString("USAddress___AddressLine1"));
					usa.setAddressLine2(rs
							.getString("USAddress___AddressLine2"));
					usa.setState(rs.getString("USAddress___State"));
					usa.setZipPlus4(rs.getString("USAddress___ZipPlus4"));
					usa.setZip(rs.getString("USAddress___Zip"));
					usa.setZipAddOn(rs.getString("USAddress___ZipAddOn"));
					usa
							.setCarrierRoute(rs
									.getString("USAddress___CarrieRoute"));
					usa.setPMB(rs.getString("USAddress___PMB"));
					usa.setPMBDesignator(rs
							.getString("USAddress___PMBDesignator"));
					usa.setDeliveryPoint(rs
							.getString("USAddress___DeliveryPoint"));
					usa.setDPCheckDigit(rs
							.getString("USAddress___DPCheckDigit"));
					usa.setLACS(rs.getString("USAddress___LACS"));
					usa.setCMRA(rs.getString("USAddress___CMRA"));
					usa.setDPV(rs.getString("USAddress___DPV"));
					usa.setDPVFootnote(rs.getString("USAddress___DPVFootnote"));
					usa.setRDI(rs.getString("USAddress___RDI"));
					usa.setRecordType(rs.getString("USAddress___RecordType"));
					usa.setCongressDistrict(rs
							.getString("USAddress___CongressDistrict"));
					usa.setCounty(rs.getString("USAddress___County"));
					usa.setCountyNumber(rs
							.getString("USAddress___CountyNumber"));
					usa.setStateNumber(rs.getString("USAddress___StateNumber"));
					usa.setStreetNumber(rs
							.getString("USAddress___StreetNumber"));
					usa.setPreDirection(rs
							.getString("USAddress___PreDirection"));
					usa.setStreetName(rs.getString("USAddress___StreetName"));
					usa.setStreetType(rs.getString("USAddress___StreetType"));
					usa.setPostDirection(rs
							.getString("USAddress___PostDirection"));
					usa.setExtension(rs.getString("USAddress___Extension"));
					usa.setExtensionNumber(rs
							.getString("USAddress___ExtensionNumber"));
					usa.setVillage(rs.getString("USAddress___Village"));
					usa.setCity(rs.getString("USAddress___City"));
					usa.setFirm(rs.getString("USAddress___Firm"));
					usa
							.setUrbanization(rs
									.getString("USAddress___Urbanzation"));
					usa.setAddressStatus(rs.getString("USAddress___Status"));

					rs.close();
					st.close();
					return usa;
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			// Setup the service and soap implementations
			USAddressVerification service = new USAddressVerificationLocator();
			USAddressVerificationSoap_PortType port = service
					.getUSAddressVerificationSoap();

			// Holder objects which will contain the returned results
			USAddressHolder verifyAddressUSAResult = new USAddressHolder();
			SISubscriptionInfoHolder siSubscriptionInfo = new SISubscriptionInfoHolder();
			port.verifyAddressUSA(null, userName, password, addressLine1,
					addressLine2, cityStateZip, firm, urbanization,
					CasingEnum.Proper, verifyAddressUSAResult,
					siSubscriptionInfo);

			USAddress usa = verifyAddressUSAResult.value;

			KeyInsensitiveMap kim = new KeyInsensitiveMap();

			kim.put("address1", new JdbcObject(addressLine1, Types.VARCHAR));
			kim.put("address2", new JdbcObject(addressLine1, Types.VARCHAR));
			kim
					.put("cityStateZip", new JdbcObject(cityStateZip,
							Types.VARCHAR));
			kim
					.put("urbanization", new JdbcObject(urbanization,
							Types.VARCHAR));
			kim.put("firm", new JdbcObject(firm, Types.VARCHAR));

			kim.put("USAddress___AddressLine1", new JdbcObject(usa
					.getAddressLine1(), Types.VARCHAR));
			kim.put("USAddress___AddressLine2", new JdbcObject(usa
					.getAddressLine2(), Types.VARCHAR));
			kim.put("USAddress___State", new JdbcObject(usa.getState(),
					Types.VARCHAR));
			kim.put("USAddress___ZipPlus4", new JdbcObject(usa.getZipPlus4(),
					Types.VARCHAR));
			kim.put("USAddress___Zip", new JdbcObject(usa.getZip(),
					Types.VARCHAR));
			kim.put("USAddress___ZipAddOn", new JdbcObject(usa.getZipAddOn(),
					Types.VARCHAR));
			kim.put("USAddress___CarrieRoute", new JdbcObject(usa
					.getCarrierRoute(), Types.VARCHAR));
			kim.put("USAddress___PMB", new JdbcObject(usa.getPMB(),
					Types.VARCHAR));
			kim.put("USAddress___PMBDesignator", new JdbcObject(usa
					.getPMBDesignator(), Types.VARCHAR));
			kim.put("USAddress___DeliveryPoint", new JdbcObject(usa
					.getDeliveryPoint(), Types.VARCHAR));
			kim.put("USAddress___DPCheckDigit", new JdbcObject(usa
					.getDPCheckDigit(), Types.VARCHAR));
			kim.put("USAddress___LACS", new JdbcObject(usa.getLACS(),
					Types.VARCHAR));
			kim.put("USAddress___CMRA", new JdbcObject(usa.getCMRA(),
					Types.VARCHAR));
			kim.put("USAddress___DPV", new JdbcObject(usa.getDPV(),
					Types.VARCHAR));
			kim.put("USAddress___DPVFootnote", new JdbcObject(usa
					.getDPVFootnote(), Types.VARCHAR));
			kim.put("USAddress___RDI", new JdbcObject(usa.getRDI(),
					Types.VARCHAR));
			kim.put("USAddress___RecordType", new JdbcObject(usa
					.getRecordType(), Types.VARCHAR));
			kim.put("USAddress___CongressDistrict", new JdbcObject(usa
					.getCongressDistrict(), Types.VARCHAR));
			kim.put("USAddress___County", new JdbcObject(usa.getCounty(),
					Types.VARCHAR));
			kim.put("USAddress___CountyNumber", new JdbcObject(usa
					.getCountyNumber(), Types.VARCHAR));
			kim.put("USAddress___StateNumber", new JdbcObject(usa
					.getStateNumber(), Types.VARCHAR));
			kim.put("USAddress___StreetNumber", new JdbcObject(usa
					.getStreetNumber(), Types.VARCHAR));
			kim.put("USAddress___PreDirection", new JdbcObject(usa
					.getPreDirection(), Types.VARCHAR));
			kim.put("USAddress___StreetName", new JdbcObject(usa
					.getStreetName(), Types.VARCHAR));
			kim.put("USAddress___StreetType", new JdbcObject(usa
					.getStreetType(), Types.VARCHAR));
			kim.put("USAddress___PostDirection", new JdbcObject(usa
					.getPostDirection(), Types.VARCHAR));
			kim.put("USAddress___Extension", new JdbcObject(usa.getExtension(),
					Types.VARCHAR));
			kim.put("USAddress___ExtensionNumber", new JdbcObject(usa
					.getExtensionNumber(), Types.VARCHAR));
			kim.put("USAddress___Village", new JdbcObject(usa.getVillage(),
					Types.VARCHAR));
			kim.put("USAddress___City", new JdbcObject(usa.getCity(),
					Types.VARCHAR));
			kim.put("USAddress___Firm", new JdbcObject(usa.getFirm(),
					Types.VARCHAR));
			kim.put("USAddress___Urbanzation", new JdbcObject(usa
					.getUrbanization(), Types.VARCHAR));
			kim.put("USAddress___Status", new JdbcObject(
					usa.getAddressStatus(), Types.VARCHAR));
			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), node
					.getDBTempVerificationName(), node
					.getTempVerificationRecords(), ApplicationData
					.getTempJDBC()), kim);

			return usa;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static FunctionInfo fi = new FunctionInfo("Verify US Address", 7, 0,
			new String[] { "Address Line 1", "Address Line 2", "City", "State",
					"Zip", "Firm", "Urbanization" }, null);
	static {
		fi.getCategories().add(FunctionCategory.Function);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
