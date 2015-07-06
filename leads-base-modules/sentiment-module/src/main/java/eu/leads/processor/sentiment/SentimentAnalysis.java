package eu.leads.processor.sentiment;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.pipeline.Annotation;


public abstract class SentimentAnalysis {
	
	public SentimentAnalysis(Properties properties) {
		initialize(properties);
	}

	protected abstract void initialize(Properties properties);
	
	public abstract void setupSearchDocument(String text);
	public abstract Sentiment getSentimentForFuzzyPhrase(Map<String,String> fuzzyKeywords);
}
