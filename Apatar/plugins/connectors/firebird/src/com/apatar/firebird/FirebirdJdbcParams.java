//////////////////////////////////////////////////////////
// Firebird JDBC Params Class
// Author : Aamir Tauqeer (Kuwait)
// Date   : December 12, 2008
// Final  : December 25, 2008
//////////////////////////////////////////////////////////
package com.apatar.firebird;

import java.util.Properties;

import com.apatar.core.JdbcParams;

public class FirebirdJdbcParams extends JdbcParams {
	public FirebirdJdbcParams ()
	{
		init();
	}
	
	protected void init()
    {
        super.setJdbcDriver("org.firebirdsql.jdbc.FBDriver");
        super.setDriverName("firebird");
        super.setPort(3050);
    }

    public String getConnUrl()
    {
    	return (new StringBuilder("jdbc:firebirdsql:")).append(getHost()).append("/").append(getPort()).append(":").append(getDbName()).toString();
    }

    public Properties getProperties()
    {
        return null;
    }

    public void setProperties(Properties properties)
    {
    }	

}
