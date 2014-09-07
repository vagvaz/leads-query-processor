package com.apatar.read;

import java.util.HashMap;
import java.util.Map;

import com.apatar.core.ETableMode;

public class READTable {
	
	private String tableName = "";
	
	/* 
	 * will hold the arguments of webservice's operation
	 * key is arg's name (String), value is arg's type (Object)
	 */
	private Map<String, Object> arguments 	  = 	new HashMap<String, Object>();

	/*
	 * will hold the  returns  of webservice's operation
	 * key is ret's name (String), value is ret's type (Object)
	 */
	private Map<String, Object> returns 	  = 	new HashMap<String, Object>();
	
	private ETableMode mode	= ETableMode.ReadWrite;
	
	public READTable(String tableName){
		this.tableName 	  = 	tableName;
	}

	public READTable(String tableName, Map<String, Object> arguments, Map<String,Object> returns){
		this.tableName = tableName;
		this.arguments.putAll(arguments);
		this.returns.putAll(returns);
	}
	
	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public Map<String, Object> getArguments() {
		return arguments;
	}


	public void setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
	}


	public Map<String, Object> getReturns() {
		return returns;
	}


	public void setReturns(Map<String, Object> returns) {
		this.returns = returns;
	}

	public ETableMode getMode() {
		return mode;
	}

	public void setMode(ETableMode mode) {
		this.mode = mode;
	}
	
}
