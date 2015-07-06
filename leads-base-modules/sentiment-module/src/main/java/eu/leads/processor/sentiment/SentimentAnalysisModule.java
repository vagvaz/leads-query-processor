package eu.leads.processor.sentiment;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceOptions;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.BinarizerAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
/*import edu.stanford.nlp.pipeline.BinarizerAnnotator;
*/import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.SentimentAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.TreeCoreAnnotations.BinarizedTreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import java.io.PrintStream;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSplitPaneUI;


public class SentimentAnalysisModule {

    static Properties props;
    static StanfordCoreNLP pipeline;
//    static String serializedClassifier;
//    static AbstractSequenceClassifier<CoreLabel> classifier;
	private static SentimentAnalysisModule sam;

    private SentimentAnalysisModule(String serializedClassifier) {
        initialize(serializedClassifier);
    }
    
    // TODO hashmap for objects of classifiers in case more are added -> turn it into factory
    public static SentimentAnalysisModule getInstance(String serializedClassifier) {
    	if(sam==null) sam = new SentimentAnalysisModule(serializedClassifier);
    	return sam;
    }

    public static void initialize(String classifierName) {
        long start = System.currentTimeMillis();
        props = new Properties();
        
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        props.setProperty("parse.maxlen", "20");
        props.setProperty("tokenize.options", "untokenizable=noneDelete"); // sentences with more than 20 words
        pipeline = new StanfordCoreNLP(props);
      
/*
        serializedClassifier = classifierName;
        classifier = CRFClassifier
                         .getClassifierNoExceptions(serializedClassifier);
*/
        System.err.println("CoreNLP init: "+(System.currentTimeMillis()-start));
    }

    /**
     * Reads an annotation from the given filename using the requested input.
     */
    public static Annotation getAnnotation(Input inputFormat, String text,
                                              boolean filterUnknown) {
        switch (inputFormat) {
            case TEXT: {
                Annotation annotation = new Annotation(text);
                return annotation;
            }
            default:
                throw new IllegalArgumentException("Unknown format " + inputFormat);
        }
    }

    /**
     * Outputs a tree using the output style requested
     */

    static double outputTree(PrintStream out, CoreMap sentence,
                                List<Output> outputFormats) {
//        double r = 0;
//        for (Output output : outputFormats) {
//            switch (output) {
//                case ROOT: {
//                    if (sentence.get(SentimentCoreAnnotations.ClassName.class)
//                            .equalsIgnoreCase("Very Positive")) {
//                        // out.println("2");
//                        r = 2;
//                    } else if (sentence.get(
//                                               SentimentCoreAnnotations.ClassName.class)
//                                   .equalsIgnoreCase("Positive")) {
//                        // out.println("1");
//                        r = 1;
//                    } else if (sentence.get(
//                                               SentimentCoreAnnotations.ClassName.class)
//                                   .equalsIgnoreCase("Negative")) {
//                        // out.println("-1");
//                        r = -0.1;
//                    } else if (sentence.get(
//                                               SentimentCoreAnnotations.ClassName.class)
//                                   .equalsIgnoreCase("Very Negative")) {
//                        // out.println("-2");
//                        r = -0.2;
//                    } else {
//                        // out.println("0");
//
//                    }
//                    break;
//                }
//                default:
//                    throw new IllegalArgumentException("Unknown output format "
//                                                           + output);
//            }
//        }
//        return r;
    	return 0.0;
    }


    public Sentiment getOverallSentiment(String text) {
        boolean filterUnknown = false;

        List<Output> outputFormats = Arrays
                                         .asList(new Output[] {Output.ROOT});
        Input inputFormat = Input.TEXT;


        if (text == null) {
            System.out.println("No text provided");
            System.exit(-1);
        }


        Annotation annotation =
            pipeline.process(text);// getAnnotation(inputFormat, text, filterUnknown);
        //pipeline.annotate(annotation);

        Sentiment s = new Sentiment();
        s.value = 0;
        for (CoreMap sentence : annotation
                                    .get(CoreAnnotations.SentencesAnnotation.class)) {
            // System.out.print(sentence + " --> ");
            s.value += outputTree(null, sentence, outputFormats);
        }

        // System.out.print("The final sentiment is ");
        if (s.value > 0)
            s.tag = "Positive";
        else if (s.value < 0)
            s.tag = "Negative";
        else
            s.tag = "Neutral";

        // System.out.print("Sentiment value:" + s.value);

        return s;
    }
    

    private Annotation documentAnnotation = null;
	final double SENTIMENT_VAL_CORRECTION = -1.5;
    
    /**
     * 
     * @param text
     */
    public void setupSearchDocument(String text) {
        long start = System.currentTimeMillis();
        this.documentAnnotation = pipeline.process(text);
        System.err.println("CoreNLP processing: "+(System.currentTimeMillis()-start));  
    }
    
    public Sentiment getSentimentForFuzzyPhrase(Map<String,String> fuzzyKeywords) {
    	if(this.documentAnnotation == null)
    		throw new IllegalStateException("Method called without search document setup");
    	
        Set<String> originalKeywords = new HashSet<>();
        for(Entry<String, String> fuzzyKeywordEntry : fuzzyKeywords.entrySet()) {
        	originalKeywords.add(fuzzyKeywordEntry.getValue());
        }
        Double originalKeywordsCount = (double) originalKeywords.size();
        
        List<CoreMap> sentences = this.documentAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
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
            	
            	double weight  = originalKeywordsInSentence.size()/originalKeywordsCount;
            	int sentiment_s = RNNCoreAnnotations.getPredictedClass(sentences.get(sentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
            	sentencesWeight[sentenceNo] += weight;
            	if(sentencesSentiment[sentenceNo]==0) sentencesSentiment[sentenceNo] = sentiment_s;
            	
            	weight = weight/4.0;
            	
            	if(sentenceNo != 0) {
            		int thisSentenceNo = sentenceNo-1;
            		sentiment_s = RNNCoreAnnotations.getPredictedClass(sentences.get(thisSentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
                	sentencesWeight[thisSentenceNo] += weight;
	            	if(sentencesSentiment[thisSentenceNo]==0) sentencesSentiment[thisSentenceNo] = sentiment_s;
            	}
            	if(sentenceNo != sentences.size()-1) {
            		int thisSentenceNo = sentenceNo+1;
            		sentiment_s  = RNNCoreAnnotations.getPredictedClass(sentences.get(thisSentenceNo).get(SentimentCoreAnnotations.SentimentAnnotatedTree.class));
	            	sentencesWeight[thisSentenceNo] += weight;
	            	if(sentencesSentiment[thisSentenceNo]==0) sentencesSentiment[thisSentenceNo] = sentiment_s;
            	}
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
    
    /**
     * @param text
     * @param fuzzyKeywords
     * @return
     */
    public Sentiment getSentimentForFuzzyPhraseOnDocument(String text, Map<String,String> fuzzyKeywords) {
    	
        if (fuzzyKeywords == null || text == null) {
            System.out.println("Null parameter provided");
            return null;
        }

        setupSearchDocument(text);
        return getSentimentForFuzzyPhrase(fuzzyKeywords);
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


    public Sentiment getSentimentForEntity(String targetEntity,
                                              String text) {
    	
        List<Output> outputFormats = Arrays.asList(new Output[] {Output.ROOT});

        if (targetEntity == null || text == null) {
            System.out.println("Null parameter provided");
            return null;
        }

        Annotation annotation =
            pipeline.process(text);

        int tempCount = 1, i = 0; // also include tempCount sentences after the
        // entity is found
        CoreMap s1 = null, s2 = null, s3 = null;
        Sentiment s = new Sentiment();
        s.value = 0;
        for (CoreMap sentence : annotation
                                    .get(CoreAnnotations.SentencesAnnotation.class)) {
            if (i == 0) {
                s2 = sentence;
            } else if (i == 1) {
                s3 = sentence;
            } else {
                s2 = s3;
                s3 = sentence;
            }


                if (tempCount == 1) {
                    //               if (s1 != null) {
                    //                  // System.out.print(s1 + " --> ");
                    //                  s.value += outputTree(System.out, s1, outputFormats);
                    //               }
                    if (s2 != null) {

                        s.value += outputTree(System.out, s2, outputFormats);
//                        System.err.println(targetEntity + "\n " + s2 + "  --> " + s.value);
                    }
                    if (s3 != null) {
                        //                  System.out.print(s3 + " --> ");
                        s.value += outputTree(System.out, s3, outputFormats);
//                        System.err.println(targetEntity + "\n " + s3 + "  --> " + s.value);
                    }

                // System.out.print(sentence + " --> ");
                s.value += outputTree(System.out, sentence, outputFormats);

                tempCount--;
            } else if (tempCount >= 0 && tempCount < 1) {
                tempCount--;
                // System.out.print(sentence + " --> ");
                s.value += outputTree(System.out, sentence, outputFormats);
            } else
                tempCount = 3;

            if (tempCount < 0)
                tempCount = 1;

            i++;
        }

        // System.out.print("The final sentiment is ");
        if (s.value > 0)
            s.tag = "Positive";
        else if (s.value < 0)
            s.tag = "Negative";
        else
            s.tag = "Neutral";

        // System.out.print("Sentiment value:" + s.value);
        return s;
    }

//    @Override
//    public Set<Entity> getEntities(String text) {
//
//        Set<Entity> entities = new HashSet<Entity>();
//        List<List<CoreLabel>> out = classifier.classify(text);
//        for (List<CoreLabel> sentence : out) {
//            for (CoreLabel word : sentence) {
//                if (!word.get(CoreAnnotations.AnswerAnnotation.class)
//                         .equalsIgnoreCase("O")) {
//                    Entity e = new Entity();
//                    e.name = word.word();
//                    e.type = word.get(CoreAnnotations.AnswerAnnotation.class);
//                    entities.add(e);
//                }
//            }
//        }
//        return entities;
//    }

    static enum Output {
        PENNTREES, VECTORS, ROOT, PROBABILITIES
    }


    static enum Input {
        TEXT, TREES
    }

}
