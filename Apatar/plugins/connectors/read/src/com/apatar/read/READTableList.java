package com.apatar.read;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.apatar.ui.ApatarUiMain;

public class READTableList {
	
	private static Map<String, READTable> readTables = 
		new HashMap<String, READTable>();
	
	private static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){
		map.clear();
		
		for(int i=0;i<attList.length;i++){
			map.put(attList[i], types[i]);
		}
	}
	private static void putAttributeList(String tableName,HashMap<String, Object> returns, HashMap<String, Object> arguments, String[] attList, String[] types ){
		returns.clear();
		
		for(int i=0;i<attList.length;i++){
			returns.put(attList[i], types[i]);
		}
		readTables.put(tableName, new READTable(tableName,arguments,returns));
	}
	private static void initTables(HashMap<String, Object> returns, HashMap<String, Object> arguments,String keysFilename){
		
		
	       Path path = Paths.get(keysFilename);

	       BufferedReader keyReader=null;
	       if (Files.exists(path)) {
	           try {
	               keyReader = new BufferedReader(new InputStreamReader(new FileInputStream(keysFilename)));
	           } catch (FileNotFoundException e) {
	               System.out.println("Unable to read keys file, skipping " + keysFilename);
	               e.printStackTrace();
	               return;
	           }
	           System.out.println(" Loading key from file " + keysFilename);
	       }else{
	           System.err.println(" No keys file, skipping " +keysFilename);
	           return;
	       }
	       String keyLine = "";

           try {
			while ((keyLine =keyReader.readLine()) != null) {
				if(keyLine.contains(":")){
					String [] data = keyLine.split(":");
				       String[] columns = null;
				       String[] columnsTypes = null;
					String tableName = data[0];
					String pairs = data[1];
					String [] keysTypePairs  = pairs.split(",");
					if(keysTypePairs.length>0)
					{
						columns = new String[keysTypePairs.length];
						columnsTypes = new String[keysTypePairs.length];
					}else
						continue;
					
					int index = 0;
					for (String keyTypePair: keysTypePairs){
                        String [] pair = keyTypePair.trim().split("\\s+");
                        if(pair.length!=2){
                            System.err.print("Column Key Data are not correct! Key line must be at Tablename:Column name space ColumnType, form");
                            JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
                            		"Column Key Data are not correct! Key line must be at Tablename:Column name space ColumnType, form");
                            continue;
                        }else{
                        	 columns[index] = tableName+"."+pair[0];
                        	 columnsTypes[index]= pair[1];
                        	 index++;
                        }
					}
					if(index==keysTypePairs.length){
						 putAttributeList(tableName, returns,  arguments,columns,columnsTypes);
					}else{
						 System.err.print("Column Key Data pairs incorrect please check Schema file!");
                         JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME,
                        		 "Column Key Data pairs incorrect please check Schema file!");
					}
					
			   }
			}
			System.out.println(" Loaded file " + keysFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static{
		HashMap<String, Object> arguments = new HashMap<String, Object>(); 
		HashMap<String, Object> returns = new HashMap<String, Object>();
		
		
		arguments.clear();
		returns.clear();
	
		putAttributeList("webpages",returns, arguments,
				new String[]{
				"url",
                "domainname",
                "headers",
                "body",
                "responsecode",
                "language",
                "charset",
                "responsetime",
                "links",
                "title",
                "pagerank",
                "sentiment",
                "published"
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
				"NUMERIC",
				"NUMERIC",
				"DATE"
				});
		
		
		
		putAttributeList("entities",returns,  arguments,
				new String[]{
				"webpageurl",
				"name",
				"sentimentscore"				 
				}, 
				new String[]{
				"TEXT",
				"TEXT",
				"NUMERIC" 
				});
		 
		String keysFilename = "Schema.keys";
		initTables(returns, arguments,keysFilename);
		
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
