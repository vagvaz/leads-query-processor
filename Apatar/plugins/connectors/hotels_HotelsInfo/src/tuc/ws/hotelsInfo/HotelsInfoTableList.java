/**
 *
 * this is auto generated code
 *
 */

package tuc.ws.hotelsInfo;

import java.util.HashMap;
import java.util.Map;

public class HotelsInfoTableList {
	
	
	
	private static Map<String, HotelsInfoTable> HotelsInfoTables =new HashMap<String, HotelsInfoTable>();

	private static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){
		map.clear();
		for(int i=0;i<attList.length;i++){
			map.put(attList[i], types[i]);
		}
	}

	static{
		HashMap<String, Object> arguments = new HashMap<String, Object>();
		HashMap<String, Object> returns = new HashMap<String, Object>();

		/* *************************************
		 * getHotelstable
		 * *************************************/

		arguments.clear();
		returns.clear();

		putAttributeList(arguments, new String[]{
		}, new String[]{
		});

		putAttributeList(returns, new String[]{
			"servRating",
			"reviewURL",
			"address",
			"stars",
			"roomsRating",
			"valRating",
			"name",
			"locRating",
			"cleanRating",
			"overRating",
			"hID",
			"sleepRating"		
		}, new String[]{
			"DECIMAL",
			"TEXT",
			"TEXT",
			"NUMERIC",
			"DECIMAL",
			"DECIMAL",
			"TEXT",
			"DECIMAL",
			"DECIMAL",
			"DECIMAL",
			"NUMERIC",
			"DECIMAL"		
		});

		HotelsInfoTables.put("getHotels", new HotelsInfoTable("getHotels",arguments,returns));


	}

	public static Map<String, HotelsInfoTable> getHotelsInfoTables() {
		return HotelsInfoTables;
	}

	public static HotelsInfoTable getTableByName( String name ){
		return HotelsInfoTables.get(name);
	}

}

