package eu.leads.datastore.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ch.lambdaj.function.matcher.LambdaJMatcher;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.sun.tools.corba.se.idl.constExpr.GreaterThan;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.processor.web.QueryResults;
import eu.leads.utils.LEADSUtils;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;
import static java.util.Arrays.*;


public class LeadsDataStore extends AbstractDataStore {

	private Map<String,List<String>> tablesColumns = new HashMap<>();
	
	public LeadsDataStore(Properties mapping, int port, String... hosts) {
		super(mapping);

		boolean connected = LeadsQueryInterface.initialize(hosts[0], port);
		
		System.out.printf(connected+"! Connected to LEADS host: %s:%d\n", 
				hosts[0], port);
		
		listColumnsPerTable();
//		System.out.println(tablesColumns);
	}

	@Override
	public SortedSet<URIVersion> getLeadsResourceMDFamily(String uri, String family, int lastVersions, String timestamp, boolean before) {
		SortedSet<URIVersion> uriVersions = new TreeSet<URIVersion>();

//		boolean reverse = false;

		String queryP01 = "SELECT * FROM " + family;
		//
		String queryP02 = " WHERE ";
		//
		String queryP03 = "uri = '" + uri + "'";
		//
		String queryP04 = "";
//		if(timestamp != null) {
//			queryP04 += " AND ";
//			if(before) 	queryP04 += "ts < ";
//			else		queryP04 += "ts = ";
//			queryP04 += timestamp;
//		}
		//
//		if(before) {
//			queryP04 += " ORDER BY ts DESC";
//			queryP04 += " LIMIT " + lastVersions;
//		}
		//
		String query = queryP01+queryP02+queryP03+queryP04;

		QueryResults rs;
		rs = LeadsQueryInterface.execute(query);
		
//		if((rs == null || rs.getResult().size() == 0) && timestamp != null && before == true) {
//
//			reverse = true;
//			//
//			queryP04 = "";
//			//
//			queryP04 += " ORDER BY ts ASC";
//			//
//			query = queryP01+queryP02+queryP03+queryP04;
//
//			System.out.println(query);
//			
//			rs = LeadsQueryInterface.sendQuery(query);		
//		}
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow = new JSONObject(row);
				Map<String,Cell> columnsMap = new HashMap<String, Cell>();
				Set<String> columns = jsonRow.keySet();

				try {
					for(String col : columns) {
						String name = col;
						if(name.startsWith(family) && !name.endsWith("uri") && !name.endsWith("ts")) {
							Object value = jsonRow.get(name);
							// To be extended to any type...
							if(!value.equals(JSONObject.NULL)) {
								if(value instanceof java.lang.String){
									Object val = StringEscapeUtils.unescapeJava(value.toString());
									Cell cell = new Cell(name, val, 0);
									columnsMap.put(name, cell);
								}
								else {
									Cell cell = new Cell(name, value, 0);
									columnsMap.put(name, cell);
								}
							}
						}
					}
					Long ts = new Long(jsonRow.getLong(family+".ts"));
					
					URIVersion uriVersion = new URIVersion(ts.toString(), columnsMap);
					uriVersions.add(uriVersion);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

//		if(reverse) {
//			List<URIVersion> uriVersionsList = new ArrayList<>(uriVersions);
//			Collections.reverse(uriVersionsList);
//			uriVersions = new TreeSet<>(uriVersionsList);
//		}
		
		// New code replacing WHERE ts, ORDER BY and LIMIT
		List<URIVersion> uriVersionsList = new ArrayList<>(uriVersions);
		Collections.sort(uriVersionsList);
		if(timestamp != null) {
			uriVersions = new TreeSet<>();
			if(before) {
				Long timestampL = Long.parseLong(timestamp);
				int counter = 0;
				for(URIVersion uriVersion : uriVersionsList)
					if(uriVersion.getTimestampL() < timestampL) {
						uriVersions.add(uriVersion);
						counter++;
						if(counter == lastVersions)
							break;
					}
			}
			else {
				for(URIVersion uriVersion : uriVersionsList)
					if(uriVersion.getTimestamp().equals(timestamp)) {
						uriVersions.add(uriVersion);
						break;
					}
			}
		}
		else {
			uriVersions = new TreeSet<>(uriVersionsList);
		}
		

		System.out.println(uriVersions);
		return uriVersions;
	}

	@Override
	public boolean putLeadsResourceMDFamily(String uri, String ts, String family, List<Cell> cells) {
		
		List<String> fullColumnsList = tablesColumns.get(family);

		List<String> columnsList = new ArrayList<String>();
		List<Object> valuesList  = new ArrayList<Object>();
		for(Cell cell : cells) {
			String columnName = cell.getKey();
			Object value = cell.getValue();
			//
			if(fullColumnsList.contains(columnName)) {
				columnsList.add(columnName);
				//
				if(LEADSUtils.isNumber(value))
					valuesList.add(value);
				else
					valuesList.add("'"+StringEscapeUtils.escapeJava(value.toString())+"'");
				fullColumnsList.remove(columnName);
			}
		}
		for(String columnName : fullColumnsList) {
			columnsList.add(columnName);
			valuesList.add("NULL");
		}

		int i=0;

		String queryP01 = "INSERT INTO ";
		//
		String queryP02 = family;
		//
		String queryP03 = "  (uri, ts, ";
		//
		String queryP04 = "";
		for(i=0; i<columnsList.size()-1; i++) {
			String columnName = columnsList.get(i);
			int columnNameBeg = columnName.lastIndexOf('.')+1;
			columnName = columnName.substring(columnNameBeg);
			queryP04 += columnName + ", ";
		}
		String columnName = columnsList.get(i);
		int columnNameBeg = columnName.lastIndexOf('.')+1;
		columnName = columnName.substring(columnNameBeg);
		queryP04 += columnName + ") ";
		//
		String queryP05 = "VALUES (";
		//
		String queryP06 = "'" + uri + "', ";
		//
		String queryP07 = ts + ", ";
		//
		String queryP08 = "";
		for(i=0; i<columnsList.size()-1; i++)
			queryP08 += "%s, ";
		queryP08 += "%s);";
		String query = queryP01+queryP02+queryP03+queryP04+queryP05+queryP06+queryP07+queryP08;
		
		query = String.format(query,valuesList.toArray());
		
		QueryResults rs = LeadsQueryInterface.execute(query);
		if(rs == null || rs.getResult().size() == 0)
			return false;		
	
		return true;
	}

	@Override
	public HashMap<String, List<Object>> getLeadsResourcePartsMD(String uri, String ts, String partType) {


		HashMap<String, List<Object>> returnMap = new HashMap<String, List<Object>>();
		
		String queryP01 = "SELECT * FROM " + mapping.getProperty("leads_resourceparts");
		//
		String queryP02 = " WHERE ";
		//
		String queryP03 = "uri = '" + uri + "'";
		//
		String queryP04 = " AND ts = " + ts;
		//
		String queryP05 = "";
		if(partType != null)
			queryP05 += " AND " + mapping.getProperty("leads_resourcepart-type") + " = '" + partType + "';";
		//
		String query = queryP01+queryP02+queryP03+queryP04+queryP05;

		QueryResults rs = LeadsQueryInterface.execute(query);
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				try{
					JSONObject jsonRow = new JSONObject(row);
					
					String type = jsonRow.getString(mapping.getProperty("leads_resourcepart-type"));
					String value= jsonRow.getString(mapping.getProperty("leads_resourcepart-value"));
					value = StringEscapeUtils.unescapeJava(value);
					List<Object> values = returnMap.get(type);
					if(values == null)
						values = new ArrayList<Object>();
					values.add(value);
					returnMap.put(type, values);
				} catch(JSONException e){
					System.err.println(e.getMessage());
				}
			}
		}
		return returnMap;
	}

	@Override
	public boolean putLeadsResourcePartsMD(String uri, String ts,
														HashMap<String, Object> partsTypeValuesMap) {

		String queryP01 = "INSERT INTO ";
		//
		String queryP02 = mapping.getProperty("leads_resourceparts");
		//
		String queryP03 = "  (uri, ts, partid, ";
		//
		String rtCol = mapping.getProperty("leads_resourcepart-type");
		int columnNameBeg = rtCol.lastIndexOf('.')+1;
		rtCol = rtCol.substring(columnNameBeg);
		String queryP04 = rtCol + ", ";
		//
		String rvCol = mapping.getProperty("leads_resourcepart-value");
		columnNameBeg = rvCol.lastIndexOf('.')+1;
		rvCol = rvCol.substring(columnNameBeg);
		queryP04       += rvCol + ") ";
		//
		for(Entry<String, Object> partTypeValues : partsTypeValuesMap.entrySet()) {
			String queryP05 = "VALUES ";
			String keyId = partTypeValues.getKey();
			String [] keyIdArray = keyId.split(":");
			String key = keyIdArray[0];
			String id = keyIdArray[1];
			Object value = partTypeValues.getValue();
			String val = StringEscapeUtils.escapeJava(value.toString());
			queryP05   += "(";
			queryP05   += "'" + uri + "', ";
			queryP05   += ts + ", ";
			queryP05   += "'" + id + "', ";
			queryP05   += "'" + key + "', ";
			queryP05   += "'" + val +  "'";
			queryP05   += ");";
			//queryP05 = new StringBuilder(queryP05).replace(queryP05.length()-2, queryP05.length(), "; ").toString();
			//
			String query = queryP01+queryP02+queryP03+queryP04+queryP05;

			QueryResults rs = LeadsQueryInterface.execute(query);
			if(rs == null || rs.getResult().size() == 0)
				return false;
		}

		return true;
	}

	@Override
	public Map<String, SortedSet<URIVersion>> getLeadsResourceElementsMDFamily(
																												 String uri, String family, int lastVersions, String beforeTimestamp) {
		throw new UnsupportedOperationException("getLeadsResourceElementsMDFamily not implemented for LeadsDataStore");
	}

	public List<Map<String,Object>> getLeadsResourcesOfElement(String element) {
		List<Map<String,Object>> returnMapsList = new ArrayList<>();

		try {
			String query = "SELECT * FROM " + mapping.getProperty("leads_keywords")
					+ " WHERE keywords = '" + element + "'";
			System.out.println(query);
			
			QueryResults rs = LeadsQueryInterface.execute(query);
			
			if(rs != null) {
				for(String row : rs.getResult()) {
					JSONObject jsonRow = new JSONObject(row);
					
	
					Map<String,Object> keysValues = new HashMap<>();
					String url		 = jsonRow.getString("uri");
					Long ts		 	 = jsonRow.getLong("ts");
					String partid	 = jsonRow.getString("partid");
					String sentiment = jsonRow.getString(mapping.getProperty("leads_keywords-sentiment"));
					String relevance = jsonRow.getString(mapping.getProperty("leads_keywords-relevance"));
					keysValues.put("uri"	, url);
					keysValues.put("ts" 	, ts);
					keysValues.put("partid"	, partid);
					keysValues.put(mapping.getProperty("leads_keywords-sentiment"), sentiment);
					keysValues.put(mapping.getProperty("leads_keywords-relevance"), relevance);
					returnMapsList.add(keysValues);
				}
			}

		}catch(JSONException e){
			System.err.println(e.getMessage());
		}
		return returnMapsList;
	}

	@Override
	public boolean putLeadsResourceElementsMDFamily(String uri, String ts, String partid,
																	String element, String familyName, List<Cell> cells) {

		int i=0;

		List<String> columnsList = new ArrayList<String>();
		List<Object> valuesList  = new ArrayList<Object>();
		for(Cell cell : cells) {
			columnsList.add(cell.getKey());
			valuesList.add(cell.getValue());
		}

		String queryP01 = "INSERT INTO ";
		//
		String queryP02 = mapping.getProperty("leads_keywords");
		//
		String queryP03 = "  (uri, ts, partid, keywords, ";
		//
		String queryP04 = "";
		for(i=0; i<cells.size()-1; i++) {
			String columnName = columnsList.get(i);
			int columnNameBeg = columnName.lastIndexOf('.')+1;
			columnName = columnName.substring(columnNameBeg);
			queryP04 += columnName + ", ";
		}
		String columnName = columnsList.get(i);
		int columnNameBeg = columnName.lastIndexOf('.')+1;
		columnName = columnName.substring(columnNameBeg);
		queryP04 += columnName + ") ";
		//
		String queryP05 = "VALUES (";
		//
		String queryP06 = "'" + uri + "', " + ts + ", '" + partid + "', '" + element + "'";
		//
		String queryP07 = ", ";
		for(i=0; i<cells.size()-1; i++)
			queryP07 += "'" + valuesList.get(i) + "', ";
		queryP07 += "'" + valuesList.get(i) + "') ";
		//
		String query = queryP01+queryP02+queryP03+queryP04+queryP05+queryP06+queryP07;

		QueryResults rs = LeadsQueryInterface.execute(query);
		if(rs == null || rs.getResult().size() == 0)
			return false;

		return true;
	}

	@Override
	public List<String> getResourceURIsOfDirectory(String dirUri) {
		List<String> uris = new ArrayList<String>();

		String queryP01 = "SELECT uri FROM " + mapping.getProperty("leads_core");
		//
		String queryP02 = " WHERE ";
		//
		String queryP03 = mapping.getProperty("leads_core-fqdnurl") + " = '" + dirUri + "'";
		//
		String query = queryP01+queryP02+queryP03;

		System.out.println(query);

		QueryResults rs = LeadsQueryInterface.execute(query);
		try{
			if(rs != null) {
				for(String row : rs.getResult()) {
					JSONObject jsonRow = new JSONObject(row);
					
					String uri= jsonRow.getString("uri");
					uris.add(uri);
				}
			}
		}catch(JSONException e){
			System.err.println(e.getMessage());
		}
		return uris;
	}

	public String getFamilyNextUri(String family) {
		throw new UnsupportedOperationException("getFamilyNextUri not implemented for LeadsDataStore");
	}

	@Override
	public List<String> getFQDNList() {
		throw new UnsupportedOperationException("getFQDNList not implemented for LeadsDataStore");
	}

	@Override
	public Object getFamilyStorageHandle(String familyName) {
		return new LeadsQueryInterface();
	}
	
	/*
	 * PRIVATE METHODS
	 */

	private void listColumnsPerTable() {
		Set<Object> keySet = mapping.keySet();
		List<Object> keyList = new ArrayList<>(keySet);
		
		List<Object> tableList = LEADSUtils.getMatchingStrings(keyList, "((?!-).)*");
		keyList.removeAll(tableList);
		
		for(Object obj : tableList) {
			String tableAlias = obj.toString();
			String tableName = mapping.getProperty(tableAlias);
			final List<Object> objList = LEADSUtils.getMatchingStrings(keyList, tableAlias+"-"+"((?!-).)*");
			List<String> columnsList = new ArrayList<String>() {{ for(Object o : objList) add(mapping.getProperty(o.toString())); }};
			tablesColumns.put(tableName, columnsList);
			keyList.removeAll(columnsList);
		}
	}

	@Override
	public List<String> getUsersKeywordsList() {
		List<String> keywords = new ArrayList<String>();
		
		QueryResults rs = LeadsQueryInterface.execute("SELECT * FROM default.adidas_keywords");
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow 	= new JSONObject(row);
				
				String keyword		= jsonRow.getString("default.adidas_keywords.keywords");
				keywords.add(keyword.toLowerCase());
			}
		}
		
		return keywords;
	}

	@Override
	public Map<Long, Map<String, Object>> getUsersKeywordsListExt() {
		Map<Long, Map<String, Object>> keywordsTuples = new HashMap<>();
		
		QueryResults rs = LeadsQueryInterface.execute("SELECT * FROM default.adidas_keywords");
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow 	= new JSONObject(row);
				Long key = jsonRow.getLong("default.adidas_keywords.id");
				Map<String, Object> value = new HashMap<String, Object>();
				for(Object o : jsonRow.keySet()) {
					String column = (String)o;
					value.put(column, jsonRow.get(column));
				}
			}
		}
		
		return keywordsTuples;
	}
	
}
