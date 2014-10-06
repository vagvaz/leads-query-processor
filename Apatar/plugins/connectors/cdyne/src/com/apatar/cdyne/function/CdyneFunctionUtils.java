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

import org.apache.axis.types.UnsignedByte;

import com.apatar.cdyne.Cdyne;
import com.apatar.cdyne.ws.demographix.DemographixQueryLocator;
import com.apatar.cdyne.ws.demographix.DemographixQuerySoap_PortType;
import com.apatar.cdyne.ws.demographix.GenderPercentagesCls;
import com.apatar.cdyne.ws.demographix.MaritalStatusPercentagesCls;
import com.apatar.cdyne.ws.demographix.PlaceInformationCls;
import com.apatar.cdyne.ws.demographix.RacePercentagesCls;
import com.apatar.cdyne.ws.demographix.SummaryInformation;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;

public class CdyneFunctionUtils {

	public static Object demographicsFunction(Cdyne node, String licenseKey,
			String addressLine1, String city, String stateAbbrev, String zipCode) {
		SQLQueryString sqs = DataBaseTools.CreateSelectString(ApplicationData
				.getTempDataBaseInfo(),
				new SQLCreationData(node.getTempVerificationRecords(), node
						.getDBTempVerificationName()), null);
		try {
			Statement st = ApplicationData.getTempJDBCConnection()
					.createStatement();
			ResultSet rs = st.executeQuery(sqs.query);
			while (rs.next()) {
				String _addressLine1 = rs.getString("addressLine1");
				String _city = rs.getString("city");
				String _stateAbbrev = rs.getString("stateAbbrev");
				String _zipCode = rs.getString("zipCode");
				if (addressLine1.equalsIgnoreCase(_addressLine1)
						&& city.equalsIgnoreCase(_city)
						&& stateAbbrev.equalsIgnoreCase(_stateAbbrev)
						&& zipCode.equals(_zipCode)) {
					SummaryInformation si = new SummaryInformation();

					PlaceInformationCls pi = new PlaceInformationCls();
					pi
							.setPlaceID(rs
									.getString("SummaryInformation_PlaceInformation___placeID"));
					pi
							.setStateAbbrev(rs
									.getString("SummaryInformation_PlaceInformation___stateAbbrev"));
					pi
							.setRural(rs
									.getBoolean("SummaryInformation_PlaceInformation___rural"));
					si.setPlaceInformation(pi);

					si.setError(rs.getBoolean("SummaryInformation_error"));
					si.setErrorString(rs
							.getString("SummaryInformation_errorString"));
					si.setMedianAge(new UnsignedByte(rs
							.getInt("SummaryInformation_medianAge")));
					si.setMedianIncome(rs
							.getInt("SummaryInformation_medianIncome"));
					si.setMedianRoomsInHouse(new UnsignedByte(rs
							.getInt("SummaryInformation_medianRoomsInHouse")));
					si.setMedianHouseValue(rs
							.getInt("SummaryInformation_medianHouseValue"));
					si.setMedianVehicles(new UnsignedByte(rs
							.getInt("SummaryInformation_medianVehicles")));

					MaritalStatusPercentagesCls msp = new MaritalStatusPercentagesCls();
					msp
							.setNeverMarried(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___neverMarried"));
					msp
							.setMarried(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___married"));
					msp
							.setSeparated(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___separated"));
					msp
							.setMarriedOther(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___marriedOther"));
					msp
							.setWidowed(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___widowed"));
					msp
							.setDivorced(rs
									.getBigDecimal("SummaryInformation_MaritalStatusPercentages___divorced"));
					si.setMaritalStatusPercentages(msp);

					RacePercentagesCls rp = new RacePercentagesCls();
					rp
							.setAsian(rs
									.getBigDecimal("SummaryInformation_RacePercentages___asian"));
					rp
							.setBlack(rs
									.getBigDecimal("SummaryInformation_RacePercentages___black"));
					rp
							.setIndian(rs
									.getBigDecimal("SummaryInformation_RacePercentages___indian"));
					rp
							.setMixed(rs
									.getBigDecimal("SummaryInformation_RacePercentages___mixed"));
					rp
							.setNativeHawaiian(rs
									.getBigDecimal("SummaryInformation_RacePercentages___nativeHawaiian"));
					rp
							.setOther(rs
									.getBigDecimal("SummaryInformation_RacePercentages___other"));
					rp
							.setWhite(rs
									.getBigDecimal("SummaryInformation_RacePercentages___white"));
					si.setRacePercentages(rp);

					GenderPercentagesCls gp = new GenderPercentagesCls();
					gp
							.setFemale(rs
									.getBigDecimal("SummaryInformation_GenderPercentages___female"));
					gp
							.setMale(rs
									.getBigDecimal("SummaryInformation_GenderPercentages___male"));
					si.setGenderPercentages(gp);

					rs.close();
					st.close();

					return si;
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			// Setup the service and soap implementations
			DemographixQueryLocator service = new DemographixQueryLocator();
			DemographixQuerySoap_PortType port = service
					.getDemographixQuerySoap();

			// Holder objects which will contain the returned results

			KeyInsensitiveMap kim = new KeyInsensitiveMap();
			SummaryInformation si = port.getLocationInformationByAddress(
					addressLine1, city, stateAbbrev, zipCode, licenseKey);

			kim
					.put("addressLine1", new JdbcObject(addressLine1,
							Types.VARCHAR));
			kim.put("city", new JdbcObject(city, Types.VARCHAR));
			kim.put("zipCode", new JdbcObject(zipCode, Types.VARCHAR));
			kim.put("stateAbbrev", new JdbcObject(stateAbbrev, Types.VARCHAR));

			PlaceInformationCls pi = si.getPlaceInformation();
			kim.put("SummaryInformation_PlaceInformation___placeID",
					new JdbcObject(pi.getPlaceID(), Types.VARCHAR));
			kim.put("SummaryInformation_PlaceInformation___stateAbbrev",
					new JdbcObject(pi.getStateAbbrev(), Types.VARCHAR));
			kim.put("SummaryInformation_PlaceInformation___rural",
					new JdbcObject(pi.isRural(), Types.BOOLEAN));

			kim.put("SummaryInformation_error", new JdbcObject(si.isError(),
					Types.BOOLEAN));
			kim.put("SummaryInformation_errorString", new JdbcObject(si
					.getErrorString(), Types.VARCHAR));
			kim.put("SummaryInformation_medianAge", new JdbcObject(si
					.getMedianAge().intValue(), Types.INTEGER));
			kim.put("SummaryInformation_medianIncome", new JdbcObject(si
					.getMedianIncome(), Types.INTEGER));
			kim.put("SummaryInformation_medianRoomsInHouse", new JdbcObject(si
					.getMedianRoomsInHouse().intValue(), Types.INTEGER));
			kim.put("SummaryInformation_medianHouseValue", new JdbcObject(si
					.getMedianHouseValue(), Types.INTEGER));
			kim.put("SummaryInformation_medianVehicles", new JdbcObject(si
					.getMedianVehicles().intValue(), Types.INTEGER));

			MaritalStatusPercentagesCls msp = si.getMaritalStatusPercentages();
			kim
					.put(
							"SummaryInformation_MaritalStatusPercentages___neverMarried",
							new JdbcObject(msp.getNeverMarried(), Types.DECIMAL));
			kim.put("SummaryInformation_MaritalStatusPercentages___married",
					new JdbcObject(msp.getMarried(), Types.DECIMAL));
			kim.put("SummaryInformation_MaritalStatusPercentages___separated",
					new JdbcObject(msp.getSeparated(), Types.DECIMAL));
			kim
					.put(
							"SummaryInformation_MaritalStatusPercentages___marriedOther",
							new JdbcObject(msp.getMarriedOther(), Types.DECIMAL));
			kim.put("SummaryInformation_MaritalStatusPercentages___widowed",
					new JdbcObject(msp.getWidowed(), Types.DECIMAL));
			kim.put("SummaryInformation_MaritalStatusPercentages___divorced",
					new JdbcObject(msp.getDivorced(), Types.DECIMAL));

			RacePercentagesCls rp = si.getRacePercentages();
			kim.put("SummaryInformation_RacePercentages___asian",
					new JdbcObject(rp.getAsian(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___black",
					new JdbcObject(rp.getBlack(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___indian",
					new JdbcObject(rp.getIndian(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___mixed",
					new JdbcObject(rp.getMixed(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___nativeHawaiian",
					new JdbcObject(rp.getNativeHawaiian(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___other",
					new JdbcObject(rp.getOther(), Types.DECIMAL));
			kim.put("SummaryInformation_RacePercentages___white",
					new JdbcObject(rp.getWhite(), Types.DECIMAL));

			GenderPercentagesCls gp = si.getGenderPercentages();
			kim.put("SummaryInformation_GenderPercentages___female",
					new JdbcObject(gp.getFemale(), Types.DECIMAL));
			kim.put("SummaryInformation_GenderPercentages___male",
					new JdbcObject(gp.getMale(), Types.DECIMAL));

			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), node
					.getDBTempVerificationName(), node
					.getTempVerificationRecords(), ApplicationData
					.getTempJDBC()), kim);
			return si;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
