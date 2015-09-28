package eu.leads.datastore.persistent;

import java.util.HashMap;
import java.util.Map;

import eu.leads.datastore.datastruct.StringPair;
import eu.leads.processor.web.QueryResults;

public class TablesCache {
	
	private static Map<StringPair,QueryResults> rowsMap = new HashMap<>();
	
	public static void setTableRow(String table, String id, QueryResults qr) {
		StringPair key = new StringPair(table, id);
		rowsMap.put(key,qr);
	}
	
	public static QueryResults getTableRow(String table, String id) {
		StringPair key = new StringPair(table, id);
		QueryResults row = rowsMap.get(key);
		if(row != null)
			System.out.println("Retrieving row "+id+" of table "+table+" from local table's cache.");
		return row;
	}
	
}
