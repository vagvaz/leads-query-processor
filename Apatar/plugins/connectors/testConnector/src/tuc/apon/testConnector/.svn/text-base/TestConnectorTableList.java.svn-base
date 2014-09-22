package tuc.apon.testConnector;

import java.util.HashMap;
import java.util.Map;

public class TestConnectorTableList {
	
	private static Map<String, TestConnectorTable> testConnectorTables = 
		new HashMap<String, TestConnectorTable>();
	
	private static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){
		map.clear();
		
		for(int i=0;i<attList.length;i++){
			map.put(attList[i], types[i]);
		}
	}
	
	static{
		HashMap<String, Object> arguments = new HashMap<String, Object>(); 
		HashMap<String, Object> returns = new HashMap<String, Object>();
		
		/*
		 * gameInfo table
		 */
		
		arguments.clear();
		returns.clear();
		
		
		putAttributeList(returns, 
				new String[]{
				"iId",
				"sDescription",
				"dPlayDate",
				"tPlayTime",
				"sStadiumName",
				"iSeatsCapacity",
				"sCityName",
				"sWikipediaURL",
				"sGoogleMapsURL",
				"Team1_iId",
				"Team1_sName",
				"Team1_sCountryFlag",
				"Team1_sWikipediaURL",
				"Team2_iId",
				"Team2_sName",
				"Team2_sCountryFlag",
				"Team2_sWikipediaURL",
				"sResult",
				"sScore",
				"iYellowCards",
				"iRedCards"
				}, 
				new String[]{
				"NUMERIC",
				"TEXT",
				"DATE",
				"TIME",
				"TEXT",
				"NUMERIC",
				"TEXT",
				"TEXT",
				"TEXT",
				"NUMERIC",
				"TEXT",
				"TEXT",
				"TEXT",
				"NUMERIC",
				"TEXT",
				"TEXT",
				"TEXT",
				"TEXT",
				"TEXT",
				"NUMERIC",
				"NUMERIC"
				});
		
		testConnectorTables.put("gameInfo", new TestConnectorTable("gameInfo",arguments,returns));
		
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
	
	public static Map<String, TestConnectorTable> getTestConnectorTables() {
		return testConnectorTables;
	}

	public static TestConnectorTable getTableByName( String name ){
		return testConnectorTables.get(name);
	}

}