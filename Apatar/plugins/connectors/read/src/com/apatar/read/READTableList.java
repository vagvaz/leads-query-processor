package com.apatar.read;

import java.util.HashMap;
import java.util.Map;

public class READTableList {
	
	private static Map<String, READTable> readTables = 
		new HashMap<String, READTable>();
	
	private static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){
		map.clear();
		
		for(int i=0;i<attList.length;i++){
			map.put(attList[i], types[i]);
		}
	}
	
	static{
		HashMap<String, Object> arguments = new HashMap<String, Object>(); 
		HashMap<String, Object> returns = new HashMap<String, Object>();
		
		
		arguments.clear();
		returns.clear();
		
		
		putAttributeList(returns, 
				new String[]{
				"url",
                "domainname",
                "headers",
                "content",
                "responsecode",
                "language",
                "charset",
                "responsetime",
                "links",
                "title",
                "version",
                "pagerank",
                "sentiment"
				}, 
				new String[]{
				"TEXT",
				"TEXT",
				"TEXT",
				"TEXT",
				"NUMERIC",
				"TEXT",
				"TEXT",
				"NUMERIC",
				"TEXT",
				"TEXT",
				"DATE",
				"NUMERIC",
				"NUMERIC"
				});
		
		readTables.put("Webpages", new READTable("Webpages",arguments,returns));
		
		putAttributeList(returns, 
				new String[]{
				"webpageurl",
				"name",
				"sentiment",
				"version"
				}, 
				new String[]{
				"TEXT",
				"TEXT",
				"NUMERIC",
				"DATE"
				});
		readTables.put("Entities", new READTable("Entities",arguments,returns));
		
		/*
		 * getInfo2
		 */
//		arguments.clear();
//		returns.clear();
//		
//		putAttributeList(arguments, new String[]{"iGameId"}, new String[]{"TEXT"});
//		
//		putAttributeList(returns, 
//				new String[]{
//				"iId",
//				"sDescription",
//				"dPlayDate",
//				"tPlayTime",
//				"sStadiumName",
//				"iSeatsCapacity",
//				"sCityName",
//				"sWikipediaURL",
//				"sGoogleMapsURL",
//				"Team1_iId",
//				"Team1_sName",
//				"Team1_sCountryFlag",
//				"Team1_sWikipediaURL",
//				"Team2_iId",
//				"Team2_sName",
//				"Team2_sCountryFlag",
//				"Team2_sWikipediaURL",
//				"sResult",
//				"sScore",
//				"iYellowCards",
//				"iRedCards"
//				}, 
//				new String[]{
//				"NUMERIC",
//				"TEXT",
//				"DATE",
//				"TIME",
//				"TEXT",
//				"NUMERIC",
//				"TEXT",
//				"TEXT",
//				"TEXT",
//				"NUMERIC",
//				"TEXT",
//				"TEXT",
//				"TEXT",
//				"NUMERIC",
//				"TEXT",
//				"TEXT",
//				"TEXT",
//				"TEXT",
//				"TEXT",
//				"NUMERIC",
//				"NUMERIC"
//				});
//		
//		testConnectorTables.put("gameInfo2", new TestConnectorTable("gameInfo2",arguments,returns));
//		
		/*
		 * Here goes the initializations for the rest operations
		 */
	}
	
	public static Map<String, READTable> getTestConnectorTables() {
		return readTables;
	}

	public static READTable getTableByName( String name ){
		return readTables.get(name);
	}

}