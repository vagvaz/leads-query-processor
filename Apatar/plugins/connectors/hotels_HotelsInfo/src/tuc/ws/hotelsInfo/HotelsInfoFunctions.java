package tuc.ws.hotelsInfo;

import tuc.ws.hotelsInfo.HotelsInfoStub.*;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.*;

import org.apache.axiom.om.OMElement;

import com.apatar.core.ApplicationData;
import com.apatar.core.DBTypeRecord;
import com.apatar.core.DataBaseInfo;
import com.apatar.core.DataBaseTools;
import com.apatar.core.DataProcessingInfo;
import com.apatar.core.JdbcObject;
import com.apatar.core.KeyInsensitiveMap;
import com.apatar.core.Record;
import com.apatar.core.TableInfo;

public class HotelsInfoFunctions{


	public static void getHotels(HashMap<java.lang.String, java.lang.Object> argVals,
			TableInfo ti, ArrayList<Record> recList, java.lang.String tableName, DataBaseInfo dbi) throws AxisFault,RemoteException {

		DataBaseTools.completeTransfer();
		try {
			ti.getSchemaTable().updateRecords(recList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		KeyInsensitiveMap data = new KeyInsensitiveMap();
		int sqlType;

		HotelsInfoStub _stub = new HotelsInfoStub();
		GetHotelsResponse _getHotelsResponse = new GetHotelsResponse();
		_getHotelsResponse = _stub.getHotels( );
		ArrayOfHotel _getHotelsResponse_return= _getHotelsResponse.get_return();
		Hotel[] _getHotelsResponse_return_hotels= _getHotelsResponse_return.getHotels();
		if( _getHotelsResponse_return_hotels != null ){
			for(int i_0=0;i_0<_getHotelsResponse_return_hotels.length;i_0++){
			java.lang.String _getHotelsResponse_return_hotels_address = _getHotelsResponse_return_hotels[i_0].getAddress();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("address")).getSqlType();
data.put("address", new JdbcObject(_getHotelsResponse_return_hotels_address, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_cleanRating = _getHotelsResponse_return_hotels[i_0].getCleanRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("cleanRating")).getSqlType();
data.put("cleanRating", new JdbcObject(_getHotelsResponse_return_hotels_cleanRating, sqlType));
//add to map here
			java.lang.Long _getHotelsResponse_return_hotels_hID = _getHotelsResponse_return_hotels[i_0].getHID();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("hID")).getSqlType();
data.put("hID", new JdbcObject(_getHotelsResponse_return_hotels_hID, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_locRating = _getHotelsResponse_return_hotels[i_0].getLocRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("locRating")).getSqlType();
data.put("locRating", new JdbcObject(_getHotelsResponse_return_hotels_locRating, sqlType));
//add to map here
			java.lang.String _getHotelsResponse_return_hotels_name = _getHotelsResponse_return_hotels[i_0].getName();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("name")).getSqlType();
data.put("name", new JdbcObject(_getHotelsResponse_return_hotels_name, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_overRating = _getHotelsResponse_return_hotels[i_0].getOverRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("overRating")).getSqlType();
data.put("overRating", new JdbcObject(_getHotelsResponse_return_hotels_overRating, sqlType));
//add to map here
			java.lang.String _getHotelsResponse_return_hotels_reviewURL = _getHotelsResponse_return_hotels[i_0].getReviewURL();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("reviewURL")).getSqlType();
data.put("reviewURL", new JdbcObject(_getHotelsResponse_return_hotels_reviewURL, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_roomsRating = _getHotelsResponse_return_hotels[i_0].getRoomsRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("roomsRating")).getSqlType();
data.put("roomsRating", new JdbcObject(_getHotelsResponse_return_hotels_roomsRating, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_servRating = _getHotelsResponse_return_hotels[i_0].getServRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("servRating")).getSqlType();
data.put("servRating", new JdbcObject(_getHotelsResponse_return_hotels_servRating, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_sleepRating = _getHotelsResponse_return_hotels[i_0].getSleepRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("sleepRating")).getSqlType();
data.put("sleepRating", new JdbcObject(_getHotelsResponse_return_hotels_sleepRating, sqlType));
//add to map here
			java.lang.Integer _getHotelsResponse_return_hotels_stars = _getHotelsResponse_return_hotels[i_0].getStars();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("stars")).getSqlType();
data.put("stars", new JdbcObject(_getHotelsResponse_return_hotels_stars, sqlType));
//add to map here
			java.lang.Float _getHotelsResponse_return_hotels_valRating = _getHotelsResponse_return_hotels[i_0].getValRating();
sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),
					(java.lang.String)HotelsInfoTableList.getTableByName(tableName).getReturns().get("valRating")).getSqlType();
data.put("valRating", new JdbcObject(_getHotelsResponse_return_hotels_valRating, sqlType));
//add to map here

		try{
			DataBaseTools.insertData(new DataProcessingInfo(ApplicationData.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti.getRecords(),ApplicationData.getTempJDBC()), data);
		} catch (Exception e) {
			e.printStackTrace();
		}
//remove from map here
			}
		}
	}
}
