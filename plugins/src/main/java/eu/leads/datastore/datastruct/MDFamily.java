package eu.leads.datastore.datastruct;

public class MDFamily {
	public StringPair urlTimestamp = null;
	public String family = null;
	
	public MDFamily(StringPair urlTimestamp, String family) {
		this.urlTimestamp = urlTimestamp;
		this.family = family;
	}
	public MDFamily(String url, String timestamp, String family) {
		this.urlTimestamp = new StringPair(url, timestamp);
		this.family = family;
	}
}
