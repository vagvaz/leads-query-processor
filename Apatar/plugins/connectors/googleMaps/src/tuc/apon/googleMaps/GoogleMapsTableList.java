package tuc.apon.googleMaps;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapsTableList {

	private static Map<String, GoogleMapsTable> GoogleMapsTables = 
		new HashMap<String, GoogleMapsTable>();
	
	private static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){
		map.clear();
		
		for(int i=0;i<attList.length;i++){
			map.put(attList[i], types[i]);
		}
		
	}
	
	static{
		/*
		 * for each function we create a table to model it and 
		 * we define the arguments and returns for each function.
		 * 
		 * we put each table in TableList
		 */
		
		HashMap<String, Object> arguments = new HashMap<String, Object>(); 
		HashMap<String, Object> returns = new HashMap<String, Object>();
		
		/*
		 * pinAddresses table
		 */
		
		arguments.clear();
		returns.clear();
		
		putAttributeList(arguments, 
				new String[]{	//argument name
				"address",
				"addressInfo"
				}, 
				new String[]{	//argument type
				"LONGVARCHAR",
				"LONGVARCHAR"
				});
		
		putAttributeList(returns, 
				new String[]{	//return name
				
				}, 
				new String[]{	//return type
				
				});
		
		GoogleMapsTables.put("pinAddresses", new GoogleMapsTable("pinAddresses",arguments,returns));
		
		/*
		 * pinAddressesInRagne table
		 */
		arguments.clear();
		returns.clear();

		putAttributeList(arguments, new String[]{
				"address",
				"startAddress",
				"range",
				"addressInfo"
				}, new String[]{
				"LONGVARCHAR",
				"LONGVARCHAR",
				"NUMERIC",
				"LONGVARCHAR"
				});
		
		putAttributeList(returns, 
				new String[]{
				}, 
				new String[]{
				
				});
		
		GoogleMapsTables.put("pinAddressesInRange", new GoogleMapsTable("pinAddressesInRange",arguments,returns));
		
		/*
		 * pinCloserAddress table
		 */
		
		arguments.clear();
		returns.clear();

		putAttributeList(arguments, new String[]{
				"address",
				"referenceAddress",
				"addressInfo"
				}, new String[]{
				"LONGVARCHAR",
				"LONGVARCHAR",
				"LONGVARCHAR"
				});
		
		putAttributeList(returns, 
				new String[]{
				"address",
				"referenceAddress",
				"addressInfo"
				}, 
				new String[]{
				"LONGVARCHAR",
				"LONGVARCHAR",
				"LONGVARCHAR"
				});
		
		GoogleMapsTables.put("pinClosestAddress", new GoogleMapsTable("pinClosestAddress",arguments,returns));
	}
	
	public static Map<String, GoogleMapsTable> getGoogleMapsTables() {
		return GoogleMapsTables;
	}

	public static GoogleMapsTable getTableByName( String name ){
		return GoogleMapsTables.get(name);
	}
}
