package eu.leads.processor.sentiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.BinarizerAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
/*import edu.stanford.nlp.pipeline.BinarizerAnnotator;
*/import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.SentimentAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


public class SentimentAnalysisModuleOpt {

    static Properties props;
    static StanfordCoreNLP pipeline;
    private static POSTaggerAnnotator posAnnotator;
    private static ParserAnnotator parserAnnotator;
    private static BinarizerAnnotator binarizerAnnotator;
    private static SentimentAnnotator sentimentAnnotator;
	private static SentimentAnalysisModuleOpt sam;

    private SentimentAnalysisModuleOpt(Properties properties) {
        initialize(properties);
    }
    
    // TODO hashmap for objects of classifiers in case more are added -> turn it into factory
    public static SentimentAnalysisModuleOpt getInstance(Properties properties) {
    	if(sam==null) sam = new SentimentAnalysisModuleOpt(properties);
    	return sam;
    }


    public static void initialize(Properties properties) {
    	Long start = System.currentTimeMillis();
    	
        props = new Properties();
        
        props.setProperty("annotators", "tokenize, ssplit");
        props.setProperty("parse.maxlen", "20");
        props.setProperty("tokenize.options", "untokenizable=noneDelete"); // sentences with more than 20 words
        pipeline = new StanfordCoreNLP(props);
        
        posAnnotator = new POSTaggerAnnotator();
        ParserGrammar parser = ShiftReduceParser.loadModel(properties.getProperty("srmodel"));
        parserAnnotator = new ParserAnnotator(parser, false, 20);
        
        binarizerAnnotator = new BinarizerAnnotator("ba", props);
        sentimentAnnotator = new SentimentAnnotator("sa", props);
        
        System.err.println("CoreNLP init: "+(System.currentTimeMillis()-start));
    }
	
    protected Annotation documentAnnotation = null;
	protected final double SENTIMENT_VAL_CORRECTION = -1.5;   
    
    /**
     * 
     * @param text
     */
    public void setupSearchDocument(String text) {
        long start = System.currentTimeMillis();
        this.documentAnnotation = pipeline.process(text);
        System.err.println("CoreNLP processing: "+(System.currentTimeMillis()-start));  
    }
    
    /**
     * @param text
     * @param fuzzyKeywords
     * @return
     */
    public Sentiment getSentimentForFuzzyPhrase(Map<String,String> fuzzyKeywords) {
    	
    	if(this.documentAnnotation == null)
    		throw new IllegalStateException("Method called without search document setup");
        
        Set<String> originalKeywords = new HashSet<>();
        for(Entry<String, String> fuzzyKeywordEntry : fuzzyKeywords.entrySet()) {
        	originalKeywords.add(fuzzyKeywordEntry.getValue());
        }
        Double originalKeywordsCount = (double) originalKeywords.size();
        
        List<CoreMap> sentences = documentAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
        double [] sentencesWeight = new double[sentences.size()];
        int [] sentencesSentiment = new int[sentences.size()];
        
        for (int i=0; i<sentences.size(); i++) {
            CoreMap sentence = sentences.get(i);
            
            Set<String> originalKeywordsInSentence = new HashSet<>();
            
            List<CoreLabel> words = sentence.get(TokensAnnotation.class);
            for(int j=0; j<words.size(); j++) {
            	String word = words.get(j).get(TextAnnotation.class).toLowerCase();
            	if(fuzzyKeywords.containsKey(word))
            		originalKeywordsInSentence.add(fuzzyKeywords.get(word));
            }
            
            if(!originalKeywordsInSentence.isEmpty()) {
            	int sentenceNo = i;
            	
            	List<CoreMap> listWithSentence = new ArrayList<CoreMap>();
            	listWithSentence.add(sentence);
            	Annotation sentenceAnnotation  = new Annotation(listWithSentence);
            	
                // Annotators
            	long start = System.currentTimeMillis();
            	posAnnotator.annotate(sentenceAnnotation);
                parserAnnotator.annotate(sentenceAnnotation);
            	System.out.println("Parser work on one sentence: "+(System.currentTimeMillis()-start));
                binarizerAnnotator.annotate(sentenceAnnotation);
                sentimentAnnotator.annotate(sentenceAnnotation);
                
                sentence = sentenceAnnotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);
            	
            	double weight  = originalKeywordsInSentence.size()/originalKeywordsCount;
            	//int sentiment_s  = sentimentStringToValue(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
            	int sentiment_s = RNNCoreAnnotations.getPredictedClass(sentences.get(sentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
            	sentencesWeight[sentenceNo] += weight;
            	if(sentencesSentiment[sentenceNo]==0) sentencesSentiment[sentenceNo] = sentiment_s;
            	
            	weight = weight/4.0;
            	
//            	if(sentenceNo != 0) {
//            		int thisSentenceNo = sentenceNo-1;
//	            	//sentiment_s  = sentimentStringToValue(sentence.get(SentimentCoreAnnotations.SentimentClass.class));
//            		sentiment_s = RNNCoreAnnotations.getPredictedClass(sentences.get(thisSentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
//                	sentencesWeight[thisSentenceNo] += weight;
//	            	if(sentencesSentiment[thisSentenceNo]==0) sentencesSentiment[thisSentenceNo] = sentiment_s;
//            	}
//            	if(sentenceNo != sentences.size()-1) {
//            		int thisSentenceNo = sentenceNo+1;
//            		sentiment_s  = RNNCoreAnnotations.getPredictedClass(sentences.get(thisSentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
//	            	sentencesWeight[thisSentenceNo] += weight;
//	            	if(sentencesSentiment[thisSentenceNo]==0) sentencesSentiment[thisSentenceNo] = sentiment_s;
//            	}
            }
        }
        
        double sentimentValue = 0.0;
        double sum_w_x_s    = 0.0; // sum weight times sentiment per sentence
        double sum_w		= 0.0; // sum weights
        
        // COUNTING THE SENTIMENT
        for (int i=0; i<sentences.size(); i++) {
        	double s = sentencesSentiment[i];
        	double w    = sentencesWeight[i];
        	sum_w_x_s += s*w;
        	sum_w     += w;
        	//System.out.println("Sentence "+i+": weight "+w+" -> sum weights "+sum_w+", sentiment "+s+" -> sum weighted sentiments "+sum_w_x_s);
        }
        sentimentValue = sum_w_x_s / sum_w + SENTIMENT_VAL_CORRECTION;
        //System.out.println("Temp value "+sentimentValue);
        if(sentimentValue > 0) sentimentValue /= 4+SENTIMENT_VAL_CORRECTION;
        else sentimentValue /= -SENTIMENT_VAL_CORRECTION;
        //System.out.println("Final sentiment "+sentimentValue);
        
        return new Sentiment(sentimentValue);
    }

    private int sentimentStringToValue(String string) {
    	System.out.println(string);
		if(string.equalsIgnoreCase("very negative"))
			return 0;
		else if(string.equalsIgnoreCase("negative"))
			return 1;
		else if(string.equalsIgnoreCase("neutral"))
			return 2;
		else if(string.equalsIgnoreCase("positive"))
			return 3;
		else if(string.equalsIgnoreCase("very positive"))
			return 4;
		return 2;
	}

}
