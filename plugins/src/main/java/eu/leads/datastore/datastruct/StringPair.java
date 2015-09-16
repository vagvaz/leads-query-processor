package eu.leads.datastore.datastruct;

public class StringPair {
	public String str1 = null;
	public String str2= null;
	
	public StringPair(String str1, String str2) {
		this.str1 = str1;
		this.str2 = str2;
	}
	
	@Override
	public String toString() {
		return str1+":"+str2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StringPair) {
			StringPair o = (StringPair) obj;
			if(o.str2.equals(str2) && o.str1.equals(str1))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return str1.hashCode() + str2.hashCode();
	}
}
