package eu.leads.infext.proc.com.indexing;

public class KeywordMatchInfo {
	public Long id;
	public String matched;
	public Double score;
	
	public KeywordMatchInfo(Long id, String matched, Double score) {
		super();
		this.id = id;
		this.matched = matched;
		this.score = score;
	}

	@Override
	public String toString() {
		return "KeywordScore [id=" + id + ", matched=" + matched + ", score=" + score + "]";
	}
}
