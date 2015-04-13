package eu.leads.datastore.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import eu.leads.datastore.AbstractDataStore;
import eu.leads.datastore.datastruct.Cell;
import eu.leads.datastore.datastruct.URIVersion;
import eu.leads.processor.web.QueryResults;
import eu.leads.utils.LEADSUtils;

public class LeadsDataStore extends AbstractDataStore {

	private Map<String,List<String>> tablesColumns = new HashMap<>();
	
	public LeadsDataStore(Properties mapping, int port, String... hosts) {
		super(mapping);
		
		boolean connected = LeadsQueryInterface.initialize(hosts[0], port);
		
		System.out.printf("Bzzzzzzzz "+connected+" Connected to host: %s:%d\n", 
				hosts[0], port);
		
		listColumnsPerTable();
		System.out.println(tablesColumns);
	}

	@Override
	public SortedSet<URIVersion> getLeadsResourceMDFamily(String uri,
			String family, int lastVersions, String beforeTimestamp) {
		SortedSet<URIVersion> uriVersions = new TreeSet<URIVersion>();
		
		boolean reverse = false;
		
		String queryP01 = "SELECT * FROM " + family;
		//
		String queryP02 = " WHERE ";
		//
		String queryP03 = "uri = '" + uri + "'";
		//
		String queryP04 = "";
		if(beforeTimestamp != null) {
			queryP04 += " AND ts < " + beforeTimestamp;
		}
		//
		String queryP05 = " ORDER BY ts DESC";
		//
		String queryP06 = " LIMIT " + lastVersions;
		//
		String query = queryP01+queryP02+queryP03+queryP04+queryP05+queryP06;

		System.out.println(query);
		
//		Iterable<Row> rs;
//		try {
//			rs = session.execute(query);
//		} catch(Exception e) {
//			rs = new TreeSet<Row>();
//		}
		QueryResults rs;
		rs = LeadsQueryInterface.sendQuery(query);
		
		if(rs == null || rs.getResult().size() == 0) {
			reverse = true;
			//
			queryP04 = "";
			//
			queryP05 = " ORDER BY ts ASC";
			//
			query = queryP01+queryP02+queryP03+queryP04+queryP05+queryP06;

			System.out.println(query);
			
			rs = LeadsQueryInterface.sendQuery(query);		
		}
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow = new JSONObject(row);
				Map<String,Cell> columnsMap = new HashMap<String, Cell>();
				Set<String> columns = jsonRow.keySet();
				for(String col : columns) {
					String name = col;
					Object value = jsonRow.get(name);
					// To be extended to any type...
					if(value instanceof java.lang.String){
						Cell cell = new Cell(name, value, 0);
						columnsMap.put(name, cell);
					}
					else {
						Cell cell = new Cell(name, value, 0);
						columnsMap.put(name, cell);
					}
				}
				Long ts = new Long(jsonRow.getLong("ts"));
				
				URIVersion uriVersion = new URIVersion(ts.toString(), columnsMap);
				uriVersions.add(uriVersion);
			}
		}
		
		if(reverse) {
			List<URIVersion> uriVersionsList = new ArrayList<>(uriVersions);
			Collections.reverse(uriVersionsList);
			uriVersions = new TreeSet<>(uriVersionsList);
		}
		
		return uriVersions;
	}

	@Override
	public boolean putLeadsResourceMDFamily(String uri, String ts,
			String family, List<Cell> cells) {
		
		List<String> fullColumnsList = tablesColumns.get(family);
		List<String> columnsList = new ArrayList<String>();
		List<Object> valuesList  = new ArrayList<Object>();
		for(Cell cell : cells) {
			String columnName = cell.getKey();
			Object value = cell.getValue();
			//
			columnsList.add(columnName);
			//
			if(LEADSUtils.isNumber(value))
				valuesList.add(value);
			else
				valuesList.add("'"+value+"'");
			fullColumnsList.remove(columnName);
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
		for(i=0; i<columnsList.size()-1; i++)
			queryP04 += columnsList.get(i) + ", ";
		queryP04 += columnsList.get(i) + ") ";
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
		
		QueryResults rs = LeadsQueryInterface.sendQuery(query);
		if(rs == null || rs.getResult().size() == 0)
			return false;		
		
		return true;
	}

	@Override
	public HashMap<String, List<Object>> getLeadsResourcePartsMD(String uri,
			String ts, String partType) {

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
		
		System.out.println(query);

		QueryResults rs = LeadsQueryInterface.sendQuery(query);
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow = new JSONObject(row);
				
				String type = jsonRow.getString(mapping.getProperty("leads_resourcepart-type"));
				String value= jsonRow.getString(mapping.getProperty("leads_resourcepart-value"));
				List<Object> values = returnMap.get(type);
				if(values == null)
					values = new ArrayList<Object>();
				values.add(value);
				returnMap.put(type, values);
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
		String queryP04 = mapping.getProperty("leads_resourcepart-type") + ", ";
		queryP04       += mapping.getProperty("leads_resourcepart-value") + ") ";
		//
		for(Entry<String, Object> partTypeValues : partsTypeValuesMap.entrySet()) {
			String queryP05 = "VALUES ";
			String keyId = partTypeValues.getKey();
			String [] keyIdArray = keyId.split(":");
			String key = keyIdArray[0];
			String id = keyIdArray[1];
			Object value = partTypeValues.getValue();
			queryP05   += "(";
			queryP05   += "'" + uri + "', ";
			queryP05   += ts + ", ";
			queryP05   += "'" + id + "', ";
			queryP05   += "'" + key + "', ";
			queryP05   += "%s";			
			queryP05   += ");";
			//queryP05 = new StringBuilder(queryP05).replace(queryP05.length()-2, queryP05.length(), "; ").toString();
			//
			String query = queryP01+queryP02+queryP03+queryP04+queryP05;
			
			System.out.printf(query, value.toString());
			
			System.out.println();
			QueryResults rs = LeadsQueryInterface.sendQuery(query);
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
		
		String query = "SELECT * FROM " + mapping.getProperty("leads_keywords")
				+ " WHERE keywords = '" + element + "'";
		System.out.println(query);
		
		QueryResults rs = LeadsQueryInterface.sendQuery(query);
		
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
		for(i=0; i<cells.size()-1; i++)
			queryP04 += columnsList.get(i) + ", ";
		queryP04 += columnsList.get(i) + ") ";
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
		
		System.out.println(query);
		QueryResults rs = LeadsQueryInterface.sendQuery(query);
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

		QueryResults rs = LeadsQueryInterface.sendQuery(query);
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow = new JSONObject(row);
				
				String uri= jsonRow.getString("uri");
				uris.add(uri);
			}
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
		throw new UnsupportedOperationException("Use eu.leads.processor.web.WebServiceClient singleton for this storage");
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
		
		QueryResults rs = LeadsQueryInterface.sendQuery("SELECT * FROM default.adidas_keywords");
		
		if(rs != null) {
			for(String row : rs.getResult()) {
				JSONObject jsonRow 	= new JSONObject(row);
				
				String keyword		= jsonRow.getString("default.adidas_keywords.keywords");
				keywords.add(keyword.toLowerCase());
			}
		}
		
		return keywords;
	}
	
}
