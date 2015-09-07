package eu.leads.processor.core.index;

/**
 * Created by trs on 18/6/2015.
 */
public class LeadsIndexHelper {

	LeadsIndex lInd = null;

	public LeadsIndex CreateLeadsIndex(Object value, String key, String atributeName, String cacheName) {
		if (value instanceof String)
			lInd = new LeadsIndexString();
		else if (value instanceof Integer)
			lInd = new LeadsIndexInteger();
		else if (value instanceof Float)
			lInd = new LeadsIndexFloat();
		else if (value instanceof Long)
			lInd = new LeadsIndexLong();
		else if (value instanceof Double)
			lInd = new LeadsIndexDouble();

		lInd.setCacheName(cacheName);
		lInd.setAttributeName(atributeName);
		lInd.setKeyName(key);
		lInd.setAttributeValue(value);
		return lInd;
	}
}
