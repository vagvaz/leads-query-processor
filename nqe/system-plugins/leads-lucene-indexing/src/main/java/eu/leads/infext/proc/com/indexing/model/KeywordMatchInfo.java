package eu.leads.infext.proc.com.indexing.model;

public class KeywordMatchInfo {
	public Long id;
	public String matched;
	public Float score;
	
	public KeywordMatchInfo(Long id, String matched, Float score) {
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
