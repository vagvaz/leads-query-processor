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
import java.util.Date;
import java.util.List;

import com.apatar.cdyne.CdyneFunction;
import com.apatar.cdyne.ws.deathindex.DeceasedInfo;
import com.apatar.cdyne.ws.deathindex.QueryDeathIndexLocator;
import com.apatar.cdyne.ws.deathindex.QueryDeathIndexSoap_PortType;
import com.apatar.core.ApplicationData;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.DataBaseTools.SQLCreationData;
import com.apatar.core.DataBaseTools.SQLQueryString;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class DeathIndexCdyneFunction extends CdyneValueAbstractETLFunction
		implements CdyneFunction {

	public Object execute(List l) {
		String ssn = l.get(0).toString();
		QueryDeathIndexLocator service = new QueryDeathIndexLocator();
		try {
			SQLQueryString sqs = DataBaseTools.CreateSelectString(
					ApplicationData.getTempDataBaseInfo(), new SQLCreationData(
							node.getTempVerificationRecords(), node
									.getDBTempVerificationName()), null);
			try {
				Statement st = ApplicationData.getTempJDBCConnection()
						.createStatement();
				ResultSet rs = st.executeQuery(sqs.query);
				while (rs.next()) {
					String _ssn = rs.getString("ssn");
					if (ssn.equals(_ssn)) {
						DeceasedInfo di = new DeceasedInfo();
						di.setSSN(_ssn);
						di.setLastName(rs.getString("lastName"));
						di.setNameSuffix(rs.getString("nameSuffix"));
						di.setFirstName(rs.getString("firstName"));
						di.setMiddleName(rs.getString("middleName"));
						di.setVerified(rs.getString("verified"));
						Date date = rs.getDate("birthDate");
						java.util.Calendar birthDate = java.util.Calendar
								.getInstance();
						birthDate.setTime(date);
						di.setBirthDate(birthDate);
						date = rs.getDate("deathDate");
						java.util.Calendar deathDate = java.util.Calendar
								.getInstance();
						deathDate.setTime(date);
						di.setDeathDate(deathDate);
						di
								.setZipLastResidence(rs
										.getString("zipLastResidence"));
						di.setZipLumpSumPay(rs.getString("zipLumpSumPay"));
						di.setMatch(rs.getBoolean("match"));
						di.setErrorText(rs.getString("errorText"));

						rs.close();
						st.close();

						return di;
					}
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			QueryDeathIndexSoap_PortType port = service
					.getQueryDeathIndexSoap();
			DeceasedInfo di = port.deceasedBySSN(ssn, licenseKey);

			// SearchDeceasedInfoReturnDataSetResponseSearchDeceasedInfoReturnDataSetResult
			// sr = port.searchDeceasedInfoReturnDataSet("Roberds", "William",
			// "62010", licenseKey);

			KeyInsensitiveMap kim = new KeyInsensitiveMap();
			kim.put("ssn", new JdbcObject(ssn, Types.VARCHAR));
			kim.put("__SSN", new JdbcObject(di.getSSN(), Types.VARCHAR));
			kim
					.put("lastName", new JdbcObject(di.getLastName(),
							Types.VARCHAR));
			kim.put("nameSuffix", new JdbcObject(di.getNameSuffix(),
					Types.VARCHAR));
			kim.put("firstName", new JdbcObject(di.getFirstName(),
					Types.VARCHAR));
			kim.put("middleName", new JdbcObject(di.getMiddleName(),
					Types.VARCHAR));
			kim
					.put("verified", new JdbcObject(di.getVerified(),
							Types.VARCHAR));
			kim.put("birthDate", new JdbcObject(new Date(di.getBirthDate()
					.getTimeInMillis()), Types.DATE));
			kim.put("deathDate", new JdbcObject(new Date(di.getDeathDate()
					.getTimeInMillis()), Types.DATE));
			kim.put("zipLastResidence", new JdbcObject(
					di.getZipLastResidence(), Types.VARCHAR));
			kim.put("zipLumpSumPay", new JdbcObject(di.getZipLumpSumPay(),
					Types.VARCHAR));
			kim.put("match", new JdbcObject(di.isMatch(), Types.BOOLEAN));
			kim.put("errorText", new JdbcObject(di.getErrorText(),
					Types.VARCHAR));

			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData
					.getTempDataBase().getDataBaseInfo(), node
					.getDBTempVerificationName(), node
					.getTempVerificationRecords(), ApplicationData
					.getTempJDBC()), kim);

			return di;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static FunctionInfo fi = new FunctionInfo("Death Index", 1, 0,
			new String[] { "SSN" }, null);
	static {
		fi.getCategories().add(FunctionCategory.Function);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
